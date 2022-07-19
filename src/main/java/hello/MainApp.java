package hello;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainApp {

    public static String redirectURI = "http://localhost:1301/authCallback/officeAuth";

    public static void main(String[] args) {
        System.out.println("Hey");
        authInfoUrlForOffice();
    }

    public static void authInfoUrlForOffice() {
        String url = null, state = null;
        String providerName = "OFFICE365";
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

            String forwardURL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" + "?client_id=" + URLEncoder.encode("b4d441dd-bcca-4caa-89df-649d4cbad895", StandardCharsets.UTF_8);
            forwardURL += "&redirect_uri=" + redirectURI + "&response_type=code";
            forwardURL += "&scope=" + scope + "&state=" + redirectURI;
            url = forwardURL;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Click on the link to get authorize from Microsoft >> " + url);
        try {
           //getTokenByAuthCode();
            readEmails();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

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

    public static void getTokenByAuthCode() throws IOException {
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter the code when you will get back from Microsoft:");
        String authCode = sc.nextLine();

        System.out.println("Your authCode: "+authCode);



        String post = "grant_type=authorization_code";
        post += "&code="+authCode;
        post += "&redirect_uri="+redirectURI;
        post += "&client_id="+"b4d441dd-bcca-4caa-89df-649d4cbad895";
        post += "&client_secret="+"FaJhEg1FeW2apA4~9GhGw00-_8f1eKx_-t";

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

        Map<String,Object> result = new ObjectMapper().readValue(responseText, HashMap.class);

        System.out.println("Res: "+result);

       /* if (result && result.get("access_token")) {
            System.out.println("Your Token: "+result);
            System.out.println("Your Refresh Token: "+result);
        }*/

    }


    public static void refreshToken(String refreshToken){
        System.out.println("Current refreshToken is : "+refreshToken);
        //TODO need to call API to get refreshed token
        System.out.println("Token successfully refreshed and updated local DB");
    }

    public static void readEmails() throws IOException {
        System.out.println("System going to read user mail : ");
        String folderName = "inbox";

        //Use the token here
        String access_token = "EwBQA+l3BAAUnQP8Jfa2FYxR0AX7HsEZwOdWa28AAd6FLXu/o9YFRzbTxYrFrieGPVYduCl0rL7GQMPcTu4GNMTwz3KQqLRCP0r/bn/Fn5XtpcNTirxz/6I0phJ77jiHM9Z3R38k9Sam3ti9F6K2u41lq6tJRxxMgEHFeldOlXMwkQDRGKRmsS5mzA3ULe9dNH8AXQ9RKmCka1C96TZeXqJvXx7ARRd19JPCweadpTi67U7+Lbb4JAmOFvl2S9U+X3zgLdCgFzm2RVOfR6KeCP50OVwYls0w3YaEGCqyKzLN7xXHWeUtTRcm9SMROQTO05dUIRjZdwh5hXZaaWh7JsLAXNitVhTjGhg1ghqPjM761l7gPlyF6U+bzin/W7kDZgAACOhjeEvhyymtIAK984ofkzEHtMFFLCHw9waQNyKcVuYG81T6dGc+L36qBqoxl/cSjAfru5xJUmRU2k6AdDaznm/GcbAvCF2Iff084hDyPJtJB/HbMTupCjXRHNWYHMm9dEL5rSnqUnjvPcXw8AbewLJhQsydduFMH/KiJKNwnFus4i2RjOYNQMoRm8KYGP/lz/9AloXIo8U73yl/IxsHJ86DQIUPhF5oCWoqYMRXjrdxOaqDp256FF+KHuVrZm5R8/oWnbaHEBkN3W+nLUWkPKwa/yh3dmOl5I5B4QHUdjM0govB2ai+24LKMbA//OrmnZiWZpkL96coq56jpFNSsp6DOtjvmYsNXMWRj5Re0Q4t1bSR80nKwEW8ksWPM58nSU7WNeggChMoHNv5N3Bd0qFGAJJ7fqxaLI3VVpf81vakdC+bfypUbdxpRyBpwOyblAoXcXdSQgTxdWdElqg8rcJD82eOu0nSOdoIb+bAfxwOy6SUOL1FjmFL9ubCGjiskhXkwadhszUOXDDm2WdmmvHcAz14ZED4hU5aS/5KA6xEu0sHqFFifoeplAXziImHU3GYONrTd5vxQlIGPc6QJmPJ4CHbSY9xlBIxJVjZqYjl3nP+xMPzkpPLHo6WbFAqySPXQv2clIPvRtyPnLhQ4uNRx7iZhCcaqqF3vF4dEhjrsUIQZ5UW38NvgpM+UNedRj/b2xk5LJLSakZGWNCchDdhKIa0f17ed1xpSgI=";

        String mailReadEndpoint = "https://outlook.office.com/api/v2.0/me/MailFolders/"+folderName+"/messages";

        URL url = new URL(mailReadEndpoint);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer "+access_token);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        String responseText = getResponseText(conn);
        System.out.print(responseText);

        Map<String,Object> result = new ObjectMapper().readValue(responseText, HashMap.class);
        ArrayList<LinkedHashMap> emails = (ArrayList<LinkedHashMap>) result.get("value");

        for(LinkedHashMap content: emails){
            LinkedHashMap body = (LinkedHashMap) content.get("Body");
            System.out.println("BodyPreview: "+content.get("BodyPreview"));
            System.out.println("Body"+body.get("Content"));
        }

        System.out.println("Done!");
    }

};
