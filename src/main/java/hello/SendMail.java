package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SendMail extends MainApp {

    private static List parseAndSetEmail(String mail) {
        List addresses = new ArrayList();
        if (mail != null) {
            for (String email : mail.split(",")) {
                LinkedHashMap address = new LinkedHashMap();
                LinkedHashMap emailAddress = new LinkedHashMap();
                address.put("Address", email.trim());
                emailAddress.put("EmailAddress", address);
                addresses.add(emailAddress);
            }
        }
        return addresses;
    }

    static void sendEmail(LinkedHashMap messageDetails, String access_token, Integer retryCount) {
        String messageAsJson = "";
        try {
            messageDetails.put("CustomHeaderId", UUID.randomUUID().toString());
            messageDetails.put("CustomHeaderValue", UUID.randomUUID().toString());
            LinkedHashMap message = new LinkedHashMap();
            LinkedHashMap mailContent = new LinkedHashMap();
            if (messageDetails.get("subject") == null) {
                message.put("Subject", "No Subject");
            }
            mailContent.put("ContentType", "HTML");
            mailContent.put("Content", messageDetails.get("mailContent"));
            message.put("Body", mailContent);
            message.put("ToRecipients", parseAndSetEmail("mahfuzcmt@gmail.com, mahfuzcmt2@gmail.com"));
            message.put("CcRecipients", parseAndSetEmail("mahfuzcmt@gmail.com, mahfuzcmt2@gmail.com"));
            //message.put("BccRecipients", parseAndSetEmail("mahfuzcmt@gmail.com, mahfuzcmt2@gmail.com"));

            LinkedHashMap singleValueExtendedProperty = new LinkedHashMap();
            List SingleValueExtendedProperties = new ArrayList();
            singleValueExtendedProperty.put("PropertyId", "String {" + messageDetails.get("CustomHeaderId") + "} Name MAIL_SENT_FROM_WEB_APP");
            singleValueExtendedProperty.put("Value", messageDetails.get("CustomHeaderValue"));
            SingleValueExtendedProperties.add(singleValueExtendedProperty);
            message.put("SingleValueExtendedProperties", SingleValueExtendedProperties);

            List<File> attachments = new ArrayList<>();

            attachments.add(new File(appRootPath+"/resources/profile_picture.jpg"));
            attachments.add(new File(appRootPath+"/resources/test_pdf.pdf"));

            List Attachments = new ArrayList();

            for (File attachment : attachments) {
                LinkedHashMap attachmentObj = new LinkedHashMap();
                attachmentObj.put("@odata.type", "#Microsoft.OutlookServices.FileAttachment");
                attachmentObj.put("Name", attachment.getName());
                byte[] byteData = Files.readAllBytes(Path.of(attachment.getAbsolutePath()));
                String base64String = Base64.getEncoder().encodeToString(byteData);
                attachmentObj.put("ContentBytes", base64String);
                Attachments.add(attachmentObj);
                message.put("Attachments", Attachments);
            }

            LinkedHashMap Message = new LinkedHashMap();
            Message.put("Message", message);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(Message);
            messageAsJson = json.toString();

            String apiUrl = "https://outlook.office.com/api/v2.0/me/sendmail";

            URL url = new URL(apiUrl);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Authorization", "Bearer " + access_token);
            conn.setRequestProperty("Authorization", "Bearer " + access_token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", "" + messageAsJson.length() + "");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(messageAsJson);
            writer.flush();
            writer.close();
            String responseText = getResponseText(conn);
            System.out.println(responseText);
        } catch (Exception ex) {
            System.out.println("Unable to sent Mail. Request : ${messageAsJson}, error: ${ex.message}");
            if (retryCount == 0) {
                try {
                    refreshToken();
                    sendEmail(messageDetails, getToken("access_token"), 1);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Error Message: " + ex.getMessage());
            }
        }
    }

}
