package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainApp {

    public static String appRootPath = new File("").getAbsolutePath();

    public static String redirectURI = "http://localhost:8080/authCallback/officeAuth"; //Use your redirect URI. YOU NEED TO USE SAME URI IN MS REGISTERED APP IN AZURE PORTAL
    public static String client_id = "66e8b695-004e-416c-b2ed-3d4b41353d51"; // Use your app's client_id from azure portal
    public static String client_secret = "HB98Q~UEaVLKWeRfwUvbU2h4UJdWDr4.aY~S2cPG"; // Use your app's client_secret from azure portal ( you need to create under "Certificates & secrets" menu)

    //Pass the folder name from where you want to pull/read emails
    public static String folderName = "sentitems";

    public static void main(String[] args) {
        System.out.println("Application started....");
        try {
            //to generate the authorize url
            //authInfoUrlForOffice("inbox");
            authInfoUrlForOffice("sentitems");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public static void authInfoUrlForOffice(String folderName) throws IOException {
        String url = null;
        Boolean isTokenFound = false;
        isTokenFound = getToken("access_token") != null;
        if (!isTokenFound) {
            try {
                ArrayList<String> scopes = new ArrayList<String>();
                scopes.add("offline_access");
                scopes.add("openid");
                scopes.add("https://outlook.office.com/mail.read");
                scopes.add("https://outlook.office.com/mail.send");

                StringJoiner scope = new StringJoiner("%20");
                for (CharSequence c : scopes) {
                    scope.add(c);
                }
                String forwardURL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" + "?client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8);
                forwardURL += "&redirect_uri=" + redirectURI + "&response_type=code";
                forwardURL += "&scope=" + scope + "&state=" + redirectURI;
                url = forwardURL;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println("Click on the link to get authorize from Microsoft >> " + url);
        }

        try {
            if (!isTokenFound) {
                getTokenByAuthCode();
            }
            readEmails(folderName, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getResponseText(URLConnection conn) throws IOException {
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (Exception e) {
            System.out.println("error");
            reader = new BufferedReader(new InputStreamReader(((HttpsURLConnection) conn).getErrorStream()));
        }
        String line;
        while ((line = reader.readLine()) != null) {
            answer.append(line);
        }
        reader.close();
        return answer.toString();
    }

    //Only first time when user authorized the app this method is required
    public static void getTokenByAuthCode() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the code when you will get back from Microsoft:");
        String authCode = sc.nextLine();

        System.out.println("Your authCode: " + authCode);

        String post = "grant_type=authorization_code";
        post += "&code=" + authCode;
        post += "&redirect_uri=" + redirectURI;
        post += "&client_id=" + client_id;
        post += "&client_secret=" + client_secret;

        String requestURL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

        URL url = new URL(requestURL);
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(post);
        writer.flush();
        writer.close();
        String responseText = getResponseText(conn);
        //save to updated credential
        saveCredential(responseText);
        System.out.print(responseText);
        // You need to save access_token to send/read/pull emails and refresh token to generate again access_token as access_token has a validity.

        System.out.println("Reading user info.....");

        String mailReadEndpoint = "https://outlook.office.com/api/v2.0/me";
        URL userInfoUrl = new URL(mailReadEndpoint);
        URLConnection userInoConn = userInfoUrl.openConnection();
        userInoConn.setRequestProperty("Content-Type", "application/json");
        userInoConn.setRequestProperty("Accept", "application/json");
        userInoConn.setRequestProperty("Authorization", "Bearer " + getToken("access_token"));
        userInoConn.setDoInput(true);
        userInoConn.setUseCaches(false);

        String userInoResponseText = getResponseText(userInoConn);

        Map<String, Object> result = new ObjectMapper().readValue(userInoResponseText, HashMap.class);
        System.out.println("Name: "+result.get("DisplayName"));
        System.out.println("EmailAddress: "+result.get("EmailAddress")); // Save email address along wih the token info so that in next time you can get the token by searching email from your DB

    }

    public static String getToken(String tokenType) throws IOException {
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(appRootPath + "/tokens/credential.json")) {
            String content = (String) parser.parse(reader);
            Map<String, Object> result = new ObjectMapper().readValue(content, HashMap.class);
            return (String) result.get(tokenType);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void saveCredential(String responseText) throws IOException {
        new ObjectMapper().writeValue(new File(appRootPath + "/tokens/credential.json"), responseText);
    }

    public static void refreshToken() throws IOException {
        //get the refresh_token from saved file
        String currentRefreshToken = getToken("refresh_token");
        System.out.println("Current refreshToken is : " + currentRefreshToken);

        String post = "grant_type=refresh_token";
        post += "&refresh_token=" + currentRefreshToken;
        post += "&redirect_uri=" + redirectURI;
        post += "&client_id=" + client_id;
        post += "&client_secret=" + client_secret;

        String requestURL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

        URL url = new URL(requestURL);
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(post);
        writer.flush();
        writer.close();

        String responseText = getResponseText(conn);
        System.out.print(responseText);
        //save to updated credential
        saveCredential(responseText);
        //Always remember new refresh token to generate access_token again when you need.
        System.out.println("Token successfully refreshed!");
    }

    public static EmailAddress getEmailAddress(LinkedHashMap email){
        EmailAddress emailAddress = new EmailAddress();
        LinkedHashMap emailData = (LinkedHashMap) email.get("EmailAddress");
        emailAddress.setName(emailData.get("Name").toString());
        emailAddress.setAddress((String) emailData.get("Address"));
        return emailAddress;
    }

    public static List<EmailAddress> prepareEmailAddress(ArrayList<LinkedHashMap> emails){
        List<EmailAddress> emailAddressList = new ArrayList<>();
        for (LinkedHashMap email : emails) {
            emailAddressList.add(getEmailAddress(email));
        }
        return emailAddressList;
    }

    public static  Calendar stringToCalendar(String createdDateTime) throws java.text.ParseException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+4"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        cal.setTime(sdf.parse(createdDateTime));
        return cal;
    };


    public static void readEmails(String folderName, Integer retryCount) throws IOException {
        List<EmailMessages> emailMessagesList = new ArrayList<>();
        try {
            System.out.println("System going to read user mail : ");
            String mailReadEndpoint = "https://outlook.office.com/api/v2.0/me/MailFolders/" + folderName + "/messages?$top=10&$expand=attachments&$orderby=receivedDateTime%20DESC";

            //Scanner sc = new Scanner(System.in);
            //System.out.print("Enter Subject to filer the email: ");
            String subject = null; //sc.nextLine();
            String from = null; //sc.nextLine();

            if(subject != null){
                mailReadEndpoint +="&?$search=subject:"+URLEncoder.encode(subject, StandardCharsets.UTF_8);
            }
            if(from != null){
                mailReadEndpoint +="&?$search=from:"+URLEncoder.encode(from, StandardCharsets.UTF_8);
            }
            //If you need more filtering here is the list of available properties >> https://docs.microsoft.com/en-us/Exchange/policy-and-compliance/ediscovery/message-properties-and-search-operators?redirectedfrom=MSDN&view=exchserver-2019

            URL url = new URL(mailReadEndpoint);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //get the access_token from saved file
            conn.setRequestProperty("Authorization", "Bearer " + getToken("access_token"));
            conn.setDoInput(true);
            conn.setUseCaches(false);

            String responseText = getResponseText(conn);
           // System.out.print(responseText);

            Map<String, Object> result = new ObjectMapper().readValue(responseText, HashMap.class);
            ArrayList<LinkedHashMap> emails = (ArrayList<LinkedHashMap>) result.get("value");

            for (LinkedHashMap content : emails) {
                try {
                    EmailMessages emailMessages = new EmailMessages();
                    LinkedHashMap body = (LinkedHashMap) content.get("Body");

                    emailMessages.setId(content.get("Id").toString());
                    emailMessages.setReplyTo(content.get("ReplyTo").toString());
                    emailMessages.setBody(body.get("Content").toString());
                    emailMessages.setSubject(content.get("Subject").toString());
                    emailMessages.setHasAttachments((Boolean) content.get("HasAttachments"));
                    emailMessages.setHasRead((Boolean) content.get("HasAttachments"));
                    emailMessages.setCreatedDateTime(stringToCalendar(content.get("CreatedDateTime").toString()));
                    emailMessages.setReceivedDateTime(stringToCalendar(content.get("ReceivedDateTime").toString()));

                    emailMessages.setSender(getEmailAddress((LinkedHashMap) content.get("Sender")));

                    emailMessages.setToRecipients(prepareEmailAddress((ArrayList<LinkedHashMap>) content.get("ToRecipients")));
                    emailMessages.setCcRecipients(prepareEmailAddress((ArrayList<LinkedHashMap>) content.get("CcRecipients")));
                    emailMessages.setBccRecipients(prepareEmailAddress((ArrayList<LinkedHashMap>) content.get("BccRecipients")));

                    Boolean hasAttachments = (Boolean) content.get("HasAttachments");



                /*System.out.println("=======================================");
                System.out.println("Id: " + content.get("Id"));
                System.out.println("=======================================");
                System.out.println("CreatedDateTime: " + content.get("CreatedDateTime"));
                System.out.println("=======================================");
                System.out.println("ReceivedDateTime: " + content.get("ReceivedDateTime"));
                System.out.println("================*************************=======================");
                System.out.println("================*********************=======================");
                System.out.println("HasAttachments: " + hasAttachments);
                System.out.println("================***************************=======================");
                System.out.println("BodyPreview: " + content.get("BodyPreview"));
                System.out.println("==============***************************=========================");
                System.out.println("Body: " + body.get("Content"));
                System.out.println("=============**************************==========================");
                System.out.println("Sender: " + content.get("Sender").toString()); // to understand Array and JSON string
                System.out.println("===============*********************========================");
                System.out.println("ToRecipients: " + content.get("ToRecipients"));
                System.out.println("================************************=======================");
                System.out.println("CcRecipients: " + content.get("CcRecipients"));
                System.out.println("================*************************=======================");
                System.out.println("BccRecipients: " + content.get("BccRecipients"));
                System.out.println("==============***********************************=========================");
                System.out.println("ReplyTo: " + content.get("ReplyTo"));
                System.out.println("===============***************************========================");
                System.out.println("Attachments: " + content.get("Attachments"));
                System.out.println("=================*****************************************======================");*/

                    ArrayList<Attachment> attachments = new ArrayList<>();

                    if (hasAttachments) {
                        //If there is attachment(s) then will print the sourceUrl
                        for (LinkedHashMap attachment : (ArrayList<LinkedHashMap>) content.get("Attachments")) {
                            Attachment attachmentObj = new Attachment();
                        /*System.out.println("attachment Name : " + attachment.get("Name"));
                        System.out.println("attachment ContentType : " + attachment.get("ContentType"));
                        System.out.println("attachment ContentBytes : " + attachment.get("ContentBytes"));*/
                            attachmentObj.setName((String) attachment.get("Name"));
                            attachmentObj.setContentType((String) attachment.get("ContentType"));
                            //attachmentObj.setContentBytes((byte[]) attachment.get("ContentBytes"));

                            try (FileOutputStream stream = new FileOutputStream(appRootPath+"/resources/"+attachmentObj.getName())) {
                                stream.write(Base64.getDecoder().decode(attachment.get("ContentBytes").toString()));
                            } catch (IOException exception) {
                                System.out.println("Error: "+exception.getMessage());
                            }
                            attachments.add(attachmentObj);
                        }
                    }
               /* System.out.println("=======================================");
                System.out.println("Full Content: "+content.toString());
                System.out.println("=================================================");*/

                    emailMessagesList.add(emailMessages);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            //Suppose Email not pulling due to the access_token is expired. So we can retry once again after refreshing access token
            if (retryCount == 0) {
                refreshToken();
                readEmails(folderName, 1);
            }
        }

        System.out.println(emailMessagesList.size()+" Email(s) Found!");
        System.out.println("Done!");
    }


    public static void DownloadExample(){
       /* response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", "attachment; filename=\"" + FilenameUtils.getName(pathInfo.path) + "\"")
        response.outputStream << inputStream
        response.outputStream.flush()
        inputStream.close();*/
    }


};
