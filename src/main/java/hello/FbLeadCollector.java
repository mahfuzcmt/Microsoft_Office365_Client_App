package hello;


import com.facebook.ads.sdk.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;

public class FbLeadCollector {

    public static void collectLeads() throws IOException {

       System.out.println("Going to Collect Leads....");

        for (int i = 0; i < 10000; i++) {
            // Create a neat value object to hold the URL
            URL url = new URL("https://money-ah53y.autos/732050980621");

// Open a connection(?) on the URL(??) and cast the response(???)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

// Now it's "open", we can set the request method, headers etc.
            connection.setRequestProperty("accept", "application/json");

// This line makes the request
            InputStream responseStream = connection.getInputStream();

            System.out.println(responseStream);
        }





       /* String access_token = "EAAVjR03FEAMBADYEIRWeDsWHAUO1GhWEDczy6LZCa7cyx4H06RsshhkQPch0OFAN0foit9JRBVec34yr3RYN2PEWZBeqKLlAyz6E3NeMPHQx432Gt5S1hzd05L3kFaOWBVlZBHJks1Fs4jD1smSPsxBqvPAei1pAh8cwTXlQgjaJkx4OVo98zhBHUACIWAZD";
        String app_secret = "8f04926aea96d6778c5f2980467ea496";
        String app_id = "1516532782141443";
        String id = "762451361674454";
        APIContext context = new APIContext(access_token).enableDebug(true);
        try {
            Lead lead = new Lead(id, context).get() .execute();
            System.out.println(lead);

        } catch (APIException e) {
            e.printStackTrace();
        }*/

    }

}


