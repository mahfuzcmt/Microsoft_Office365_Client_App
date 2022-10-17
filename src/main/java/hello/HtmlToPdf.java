package hello;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlToPdf {


    public static Boolean exeCuteCommand(String command) {
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }
            Process process = builder.start();
            process.waitFor();
            System.out.println("Executed Command: " + command);
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

         String appRootPath = new File("").getAbsolutePath();

        System.out.println("--------------Going to make PDF--------------");

        String source = appRootPath +"\\bootstrap4.html";
        String destination = appRootPath + "\\bootstrap4.pdf";

        //PDF settings >> https://wkhtmltopdf.org/libwkhtmltox/pagesettings.html

        String options = "-s A4 -B 10.0mm -T 10.0mm -L 10.0mm -R 10.0mm --zoom 1.23";
        String command =  "\"C:\\Program Files\\wkhtmltopdf\"\\bin" + File.separator + "wkhtmltopdf " + options + " " + source + " " + destination;

        //TODO if linux server
        //command = "/usr/bin/xvfb-run /usr/local/bin/wkhtmltopdf" then parameters

        exeCuteCommand(command);

    }

}


