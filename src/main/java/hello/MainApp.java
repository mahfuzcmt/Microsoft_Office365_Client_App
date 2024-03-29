package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
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

            FbLeadCollector.collectLeads();



            //to generate the authorize url
            //authInfoUrlForOffice("inbox");
            //createDraft();
            //authInfoUrlForOffice("sentitems");


            //prepare mail object
            //LinkedHashMap mailData = new LinkedHashMap();
           // mailData.put("mailContent", "<h1> Hey</h1>");

            //SendMail.sendEmail(mailData, getToken("access_token"), 0);
            //getAttachments("AQMkADAwATNiZmYAZC0xMzhmLTc4ZmYALTAwAi0wMAoARgAAA_LhcNSfXdJJlwyQ0nsChsEHAF3tBPZShnZEqBM1afPoEEQAAAIBCQAAAF3tBPZShnZEqBM1afPoEEQAA9B8L50AAAA", 0);
            //updateIsRead("AQMkADAwATNiZmYAZC0xMzhmLTc4ZmYALTAwAi0wMAoARgAAA_LhcNSfXdJJlwyQ0nsChsEHAF3tBPZShnZEqBM1afPoEEQAAAIBCQAAAF3tBPZShnZEqBM1afPoEEQAA9B8L50AAAA=", false, 0);
            //deleteMail("AQMkADAwATNiZmYAZC0xMzhmLTc4ZmYALTAwAi0wMAoARgAAA_LhcNSfXdJJlwyQ0nsChsEHAF3tBPZShnZEqBM1afPoEEQAAAIBCQAAAF3tBPZShnZEqBM1afPoEEQAA9B8L50AAAA=", 0);

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
                scopes.add("https://outlook.office.com/mail.readwrite");
                scopes.add("https://outlook.office.com/mail.readwrite.shared");
                scopes.add("https://outlook.office.com/mail.read");
                scopes.add("https://outlook.office.com/mail.send");

                StringJoiner scope = new StringJoiner("%20");
                for (CharSequence c : scopes) {
                    scope.add(c);
                }
                String forwardURL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" + "?client_id=" + URLEncoder.encode(client_id, String.valueOf(StandardCharsets.UTF_8));
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
            System.out.println("error: "+e.getMessage());
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

    public static String mailReadEndpoint = null;
    public static List<EmailMessages> emailMessagesList = new ArrayList<>();

    public static void readEmails(String folderName, Integer retryCount) throws IOException {
        try {
            System.out.println("System going to read user mail : ");

            //If found nextLink then need to use that to retrieve rest of the emails
            mailReadEndpoint = mailReadEndpoint != null ? mailReadEndpoint : "https://outlook.office.com/api/v2.0/me/MailFolders/" + folderName + "/messages?$top=10&$orderby=receivedDateTime%20DESC";

            //Scanner sc = new Scanner(System.in);
            //System.out.print("Enter Subject to filer the email: ");
            String subject = null; //sc.nextLine();
            String from = null; //sc.nextLine();

            if(subject != null){
                mailReadEndpoint +="&?$search=subject:"+URLEncoder.encode(subject, String.valueOf(StandardCharsets.UTF_8));
            }
            if(from != null){
                mailReadEndpoint +="&?$search=from:"+URLEncoder.encode(from, String.valueOf(StandardCharsets.UTF_8));
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

            if(result.get("@odata.nextLink") != null){
                mailReadEndpoint = (String) result.get("@odata.nextLink");
            }

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
                        System.out.println("2attachment ContentBytes : " + attachment.get("ContentBytes"));*/
                            attachmentObj.setName((String) attachment.get("Name"));
                            attachmentObj.setContentType((String) attachment.get("ContentType"));
                            //attachmentObj.setContentBytes((byte[]) attachment.get("ContentBytes"));

                           File file = new File(appRootPath+"/resources/"+content.get("ConversationIndex"));
                           if(!file.exists()){
                               file.mkdir();
                           }

                            try (FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"/"+attachmentObj.getName())) {
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
            if(result.get("@odata.nextLink") != null){
                readEmails(folderName, 0);
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


    public static ArrayList<LinkedHashMap> getAttachments(String messageId, Integer retryCount) throws IOException {
        try{
            String mailReadEndpoint = "https://outlook.office.com/api/v2.0/me/messages/"+messageId+"/attachments";
            URL url = new URL(mailReadEndpoint);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //get the access_token from saved file
            conn.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJub25jZSI6IjFWUzFoWjJod3FWOG84MXVselVidzI5YV9MQmpveW00UEdLdGxveVdvQUUiLCJhbGciOiJSUzI1NiIsIng1dCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSIsImtpZCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSJ9.eyJhdWQiOiJodHRwczovL291dGxvb2sub2ZmaWNlLmNvbSIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0L2I4OWUzMjk0LWU4ZTEtNDc3OS05MmJhLTRiMzFmYTk2NGRkNS8iLCJpYXQiOjE2NTkyODM1NjYsIm5iZiI6MTY1OTI4MzU2NiwiZXhwIjoxNjU5Mjg3NDY2LCJhY2N0IjowLCJhY3IiOiIxIiwiYWlvIjoiQVRRQXkvOFRBQUFBY2tFeEh4TnRFMHpvclIwU3Qwb2k1aUxPOXRPZ3J6UHpoR1pTM1B3YlZvWjkzaGFNN3NhTUNsU0c2RHo5eGMzMiIsImFtciI6WyJwd2QiXSwiYXBwX2Rpc3BsYXluYW1lIjoiSE9MSVNUSUNfQ1JNIiwiYXBwaWQiOiI1NTU0YjkxZC1kNGZiLTQ4MmItYmNkMi0yYmIxMTg5Mjc2NjEiLCJhcHBpZGFjciI6IjEiLCJlbmZwb2xpZHMiOltdLCJmYW1pbHlfbmFtZSI6IlZpdGV0emFraXMiLCJnaXZlbl9uYW1lIjoiTWFub2xpcyIsImlwYWRkciI6Ijg1LjczLjUxLjMiLCJuYW1lIjoiTWFub2xpcyBWaXRldHpha2lzIiwib2lkIjoiZmE4YWJjMDgtZmMyZS00NDMzLTg4YmEtY2ViM2MxZTk2YzU1IiwicHVpZCI6IjEwMDMyMDAxREM5MUM4QjMiLCJyaCI6IjAuQVlJQWxES2V1T0hvZVVlU3Vrc3gtcFpOMVFJQUFBQUFBUEVQemdBQUFBQUFBQUNDQURnLiIsInNjcCI6Ik1haWwuUmVhZCBNYWlsLlJlYWRXcml0ZSBNYWlsLlJlYWRXcml0ZS5TaGFyZWQgTWFpbC5TZW5kIiwic2lkIjoiY2Y0YzY0NmUtNmZmMS00YjEwLTgyZmUtODA4ZDZkMGYzY2U3Iiwic3ViIjoiRWJrRXFUY2RUZm5RSHFsNm5mUGFaUHpSb1ZUWlp3alNRT2NxSjJLdkJNNCIsInRpZCI6ImI4OWUzMjk0LWU4ZTEtNDc3OS05MmJhLTRiMzFmYTk2NGRkNSIsInVuaXF1ZV9uYW1lIjoidml0ZXR6YWtpcy5tYW5vbGlzQGhvbGlzdGljLWNzLmdyIiwidXBuIjoidml0ZXR6YWtpcy5tYW5vbGlzQGhvbGlzdGljLWNzLmdyIiwidXRpIjoiZjBaSUdDY2U2RXFYWHgtUFRGcXhBQSIsInZlciI6IjEuMCIsIndpZHMiOlsiYjc5ZmJmNGQtM2VmOS00Njg5LTgxNDMtNzZiMTk0ZTg1NTA5Il19.YAMfrwLmd9kei-WDtsrg7uQjUrSdRu36F-GZL2iWhpWryHPWnAQC81fpF9Hliy9bNsArf3Er9JInrmY_iNGjG5mnFXoeqZvSNxbYbMPjRyrqejaIqFHLuzT9-T-EfQbbrHE9qPMMbKgmxHezVPPLgEQ56EZ0-JgeeUJVkUUr-qNyV9UvRU-NsrB763Ywu-BxaqwkxgPD8ZEupNitI8RNBrkcXdThFNJHs0J6PWiizFwQ53wZ0tNguJkPWgweo9wAUXZhbODYVSBWGlWiYo28gqxjsctVRLgB1lv80VUQX-JTW1H3Jo19CWjmP7aAcyRO7xij-grsihkXK5porLdz2w");
            conn.setDoInput(true);
            conn.setUseCaches(false);

            String responseText = getResponseText(conn);


            Map<String, Object> result = new ObjectMapper().readValue(responseText, HashMap.class);
            ArrayList<LinkedHashMap> attachments = (ArrayList<LinkedHashMap>) result.get("value");
            System.out.println(attachments.size()+", Attachment(s) found!");
            File file = new File(appRootPath+"/resources/"+System.currentTimeMillis()+"/");
            for (LinkedHashMap attachment : attachments) {
                Attachment attachmentObj = new Attachment();
                attachmentObj.setName((String) attachment.get("Name"));
                attachmentObj.setContentType((String) attachment.get("ContentType"));
                if(!file.exists()){
                    file.mkdir();
                }
                try (FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"/"+attachmentObj.getName())) {
                    stream.write(Base64.getDecoder().decode(attachment.get("ContentBytes").toString()));
                } catch (IOException exception) {
                    System.out.println("Error: "+exception.getMessage());
                }
            }
        }catch (Exception e){
            System.out.println("Error: "+e.getMessage());
            if (retryCount == 0) {
                refreshToken();
                getAttachments(folderName, 1);
            }
        }
        return null;
    }



    public static void allowMethods(String methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
            methodsField.setAccessible(true);
            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);
            methodsField.set(null, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void updateIsRead(String messageId, Boolean isRead, Integer retryCount) throws IOException {
        try {
            String requestURL = "https://outlook.office.com/api/v2.0/me/messages/" + messageId;
            String post = "{IsRead:" + isRead + "}";

            String responseText  = "";
            allowMethods("PATCH");
            HttpURLConnection conn = (HttpURLConnection) new URL(requestURL).openConnection();
            conn.setRequestMethod("PATCH");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + getToken("access_token"));

            byte[] out = post.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = conn.getOutputStream();
            stream.write(out);
            Integer responseCode = conn.getResponseCode();
            BufferedReader br;
            String line = "";
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    responseText += line + "\n";
                }
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    responseText += line + "\n";
                }
            }
            br.close();
            conn.disconnect();
            System.out.println("Response: " + responseText);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (retryCount == 0) {
                refreshToken();
                updateIsRead(messageId, isRead, 1);
            }
        }
    }

    public static void deleteMail(String messageId, Integer retryCount) throws IOException {
        try {
            String requestURL = "https://outlook.office.com/api/v2.0/me/messages/" + messageId;
            HttpURLConnection conn = (HttpURLConnection) new URL(requestURL).openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + getToken("access_token"));
            String responseText = getResponseText(conn);
            System.out.println("Response: " + responseText);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (retryCount == 0) {
                refreshToken();
                deleteMail(messageId, 1);
            }
        }
    }


    public static void createDraft() throws IOException {

        String post = "{\n" +
                "  \"Subject\": \"Did you see last night's game?\",\n" +
                "  \"Importance\": \"Low\",\n" +
                "  \"Body\": {\n" +
                "    \"ContentType\": \"HTML\",\n" +
                "    \"Content\": \"They were <b>awesome</b>!\"\n" +
                "  },\n" +
                "  \"ToRecipients\": [\n" +
                "    {\n" +
                "      \"EmailAddress\": {\n" +
                "        \"Address\": \"mahfuzcmt@gmail.com\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String requestURL = "https://outlook.office.com/api/v2.0/me/MailFolders/drafts/messages";

        URL url = new URL(requestURL);
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getToken("access_token"));
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(post);
        writer.flush();
        writer.close();
        String responseText = getResponseText(conn);
        System.out.print(responseText);
        System.out.println("successfully draft!");
    }


};
