import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringJoiner;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Hey");
        authInfoUrlForOffice();
    }

    public static void authInfoUrlForOffice() {
        String url = null, state = null;
        String providerName = "OFFICE365";
        try {
            String redirectURI = "http://localhost:1301/authCallback/officeAuth";
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
        getTokenByAuthCode();
    };


    public static void getTokenByAuthCode(){
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter the code when you will get back from Microsoft:");
        String authCode = sc.nextLine();

        System.out.println("Your authCode: "+authCode);


        //TODO need to call API to validate the code and get Tokens
        System.out.println("Your Token: "+authCode);
        System.out.println("Your Refresh Token: "+authCode);

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
