package hello;


import java.io.*;

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

    public static String appRootPath = new File("").getAbsolutePath();

    public static void writeHtmlIFile(String tempSourcePath, String htmlString) throws IOException {
        FileWriter myWriter = new FileWriter(tempSourcePath);
        myWriter.write(htmlString);
        myWriter.close();
    }

    public static void main(String[] args) throws IOException {

        String htmlContent = "<!doctype html>" +
                "<html lang=en>" +
                "<head>" +
                "    <meta charset=utf-8>" +
                "    <meta name=viewport content=width=device-width, initial-scale=1, shrink-to-fit=no>" +
                "" +
                "    <!-- Bootstrap CSS -->" +
                "    <link rel=stylesheet href=https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" +
                "          integrity=sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm crossorigin=anonymous>" +
                "" +
                "    <title>Hello, world!</title>" +
                "</head>" +
                "<body>" +
                "<div class='alert alert-success'>This is bootstrap 4 success!</div>" +
                "</body>" +
                "</html>";

        System.out.println("--------------Going to make PDF--------------");

        String tempHtmlFilePath = appRootPath +"\\invoice.html";
        String destination = appRootPath + "\\invoice.pdf";
        writeHtmlIFile(tempHtmlFilePath, htmlContent);

        //PDF settings >> https://wkhtmltopdf.org/libwkhtmltox/pagesettings.html

        String options = "-s A4 -B 10.0mm -T 10.0mm -L 10.0mm -R 10.0mm --zoom 1.23";
        //TODO put your exact location
        //String command =   "\"C:\\Program Files\\wkhtmltopdf\"\\bin" + File.separator + "wkhtmltopdf " + options + " " + tempHtmlFilePath + " " + destination;

        //TODO if linux server
        String command = "/usr/bin/xvfb-run /usr/local/bin/wkhtmltopdf "+ options+" " +tempHtmlFilePath+ " " +destination;

        exeCuteCommand(command);
        System.out.println("Invoice Successfully Generated!");
       /* File f = new File(tempHtmlFilePath);
        if(f.delete())
        {
            System.out.println("Temp file: "+f.getName() + " deleted");
        }*/

    }

}


