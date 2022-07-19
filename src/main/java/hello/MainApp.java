package hello;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
            getTokenByAuthCode();
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

    public static String doGetRequest(String server) throws IOException {
        URL url = new URL(server);
        URLConnection conn = url.openConnection();
        //conn.setRequestProperty(it.key, it.value);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        return getResponseText(conn);
    }

    public static URLConnection getPostConnection(String server, String data) throws IOException {
        URL url = new URL(server);
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();
        return conn;
    }

    public static String doPostRequest(String server, String data) throws IOException {
        String responseText = "", errorMessage = "";
        Throwable throwable;
        try {
            URLConnection connection = getPostConnection(server, data);
            responseText = getResponseText(connection);
        } catch (Throwable t) {

        }
        return responseText;
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
        String res = doPostRequest(requestURL, post);
        System.out.print(res);

        Map<String,Object> result = new ObjectMapper().readValue(res, HashMap.class);

        System.out.println("Res: "+result);

       /* if (result && result.get("access_token")) {
            System.out.println("Your Token: "+result);
            System.out.println("Your Refresh Token: "+result);
        }*/

    }


    public static void getRefreshToken(String refreshToken){
        System.out.println("Current refreshToken is : "+refreshToken);
        //TODO need to call API to get refreshed token
        System.out.println("Token successfully refreshed and updated local DB");
    }

    public static void readEmails(String refreshToken){
        System.out.println("System going to read user mail : ");
        //TODO need to call API to get refreshed token
        System.out.println("Processing mail data to save in database");
        System.out.println("Done!");
    }

};
