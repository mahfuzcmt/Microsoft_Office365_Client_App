package hello;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.*;

public class ReviewImporter {

    public static void main(String[] args) throws IOException {


        Scanner sc = new Scanner(System.in);
        System.out.print("Enter productUrl");
        String productUrl = sc.nextLine();

        Scanner sc2 = new Scanner(System.in);
        System.out.print("Enter productId");
        String productId = sc2.nextLine();

        Scanner sc3 = new Scanner(System.in);
        System.out.print("Enter sellerId");
        String sellerId = sc3.nextLine();

        scrapReviews(productUrl, productId, sellerId, 1);
    }


    private static void println(String message){
        System.out.println(message);
    }

    static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";

    static Map<String, String> cookies = null;
    static Integer totalPages = 0;
    static Integer pageNo = 0;
    static Integer total = 0;

    private static void scrapReviews(String productUrl, String productId, String sellerId, Integer pageNo) throws IOException {

        println("=========Current review page no: ${params.pageNo}");
        println("=========Current review page no: ${params.pageNo}================");

        if(cookies == null){
            Connection.Response response = Jsoup.connect(productUrl)
                    .method(Connection.Method.GET)
                    .execute();
            cookies = response.cookies();
        }

        String feedBackUrl = "https://feedback.aliexpress.com/display/productEvaluation.htm?v=2&productId="+productId+"&ownerMemberId="+sellerId+"&memberType=seller&startValidDate=&i18n=true&page="+pageNo+"";
        println("=====================feedBackUrl: "+feedBackUrl+"==========================");
        Document document = Jsoup.connect(feedBackUrl).userAgent(userAgent).timeout(30 * 1000).cookies(cookies).get();
        ArrayList<Element> ratings = document.getElementsByClass("star-view");
        ArrayList<Element> buyerReviews = document.getElementsByClass("buyer-review");

        total = total != 0 ? total : Integer.valueOf(document.select(".fb-star-selector em").get(0).text());
        totalPages = Math.toIntExact(totalPages != 0 ? totalPages : Math.round(total / 10 + 0.4));

        println("=========ratings found : "+ratings.size()+"============!");
        println("=========buyerReviews found : "+buyerReviews.size()+"============!");


        if (totalPages > pageNo) {
            pageNo += 1;
            scrapReviews( productUrl, productId, sellerId, pageNo);
        }
    }


    /*void importReviewsFromAliexpress() {
        try {
            String url = "";
            Document productDetail = Jsoup.connect(url).userAgent(userAgent).timeout(30 * 1000).get();
            String productId = null, sellerId = null;
            try {
               // String text = productDetail.getElementsByTag("script")[0].childNodes[0];
                List<Node> nodes = (List<Node>) productDetail.getElementsByTag("script").get(0);
                String text = nodes.get(0);
                productId = text.substring(text.indexOf("\"productId\":"), text.indexOf("\",\"sellerId\"")).split(":")[1].replace("\"", "");
                try {
                    sellerId = text.substring(text.indexOf("\"sellerId\":"), text.indexOf(",\"status\":")).split(":")[1];
                } catch (Exception e) {
                    sellerId = "1";
                }
            } catch (StringIndexOutOfBoundsException e) {
                sellerId = "1";
            } catch (Exception e) {
                productId = productDetail.toString().substring(productDetail.toString().indexOf("productId="), productDetail.toString().indexOf("productId=") + 30).replaceAll("[^0-9]", "");
                sellerId = productDetail.toString().substring(productDetail.toString().indexOf("sellerId"), productDetail.toString().indexOf("sellerId") + 30).replaceAll("[^0-9]", "");
            }
            if (productId == null) {
                try {
                    if (productId == null) {
                        productId = url.split("/item/")[1].split(".html")[0];
                    }
                } catch (Exception e1) {
                    println("While getting productId: ${e1.message}");
                }
            }

            println("=====================productId: ${productId}==========================");
            println("=====================sellerId: ${sellerId}==========================");

            scrapReviews(url, productId, sellerId, 1)
        } catch (SSLException e) {
            println("Network Error!, Please try again later."+e.getMessage());
        } catch (Exception e) {
            println("=====================Exception: ${e.message}==========================");
            println("While pulling reviews from aliexpress Error: "+e.getMessage());
        }
    }*/
}
