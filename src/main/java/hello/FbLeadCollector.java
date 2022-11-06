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






       String access_token = "EAAVjR03FEAMBAD5drOlScQP185xQftfXtqa4tBI17jHJTjJ2gtZC5ZBwUxtb2sl7BlwJ1QgpmUVB5NPK05JI2aOZAhKHPEEeE1yoYxkvFIhtn9LwNI6uarC1IuRYCtzhGjAA1kapGPj36ly5OeAx25Oxw05goC2CTFa1iKI6qJlSFeTQhS5w6zx7spWS7IZD";
        String app_secret = "8f04926aea96d6778c5f2980467ea496";
        String app_id = "1516532782141443";
        String id = "762451361674454";
        APIContext context = new APIContext(access_token).enableDebug(true);
        try {
            Lead lead = new Lead(id, context).get() .execute();
            System.out.println(lead);

        } catch (APIException e) {
            e.printStackTrace();
        }

    }

}


