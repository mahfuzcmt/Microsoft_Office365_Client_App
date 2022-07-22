# Microsoft_Office365_Client_App

Find ms-smtp directory to get some screenshot about how to register an application in azure portal.


System.out.println("Id: " + content.get("Id"));
System.out.println("CreatedDateTime: " + content.get("CreatedDateTime"));
System.out.println("ReceivedDateTime: " + content.get("ReceivedDateTime"));
Boolean hasAttachments = (Boolean) content.get("HasAttachments");
System.out.println("HasAttachments: " + hasAttachments);

####How to get some important data from email:

                System.out.println("BodyPreview: " + content.get("BodyPreview"));
                System.out.println("Body: " + body.get("Content"));
                System.out.println("ToRecipients: " + content.get("ToRecipients"));
                System.out.println("CcRecipients: " + content.get("CcRecipients"));
                System.out.println("BccRecipients: " + content.get("BccRecipients"));
                System.out.println("ReplyTo: " + content.get("ReplyTo"));
                System.out.println("Attachments: " + content.get("Attachments"));
                if(hasAttachments){
                    //If there is attachment(s) then will print the sourceUrl
                    for (LinkedHashMap attachment : (ArrayList<LinkedHashMap>) content.get("Attachments")) {
                        System.out.println("attachment URL : " + attachment.get("SourceUrl"));
                    }
                }
                System.out.println("=======================================");
                System.out.println("Full Content: "+content.toString());
                System.out.println("=================================================");

### usage of the various methods of the application
#### authInfoUrlForOffice()
    1. Will check if system already has access token by any specific email (currently in this application there is no DB for email mapping)
    2. If no token is found then generate a token by calling getTokenByAuthCode()
    3. getTokenByAuthCode() will generate a URL first user should browse the URL and login with his/her email outlook account and allow 
       the application to access some sopces (read/write)
    4. After allowing Microsoft will redirect the user to our redirected url (which is used both in app registration and request)
    5. Just copy and input the code value then system will generate the access_token, refresh_token, emailAddress, displayName etc
    6. This application only save the token info in a JSON file by calling saveCredential() method. for future use, If you have a database you should save the token info along with emailAddress
        If you want to get access/read emails by proving email address in next time.
    7. If you already have access_token system will call to read emails and attachements.
    8. If the access_token is already expired system will call refreshToken() to regenrate access_token and will call the readEmails() method onces agani.



If you need any assistance ## Contact with [Mahfuz Ahmed](https://www.fb.com/mahfuzcmt)