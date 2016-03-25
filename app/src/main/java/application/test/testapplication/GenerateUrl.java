package application.test.testapplication;

/**
 * Created by mikarantakeisu on 25/03/16.
 */
public class GenerateUrl {

    static String baseUrl = "https://api.foursquare.com/v2/venues/search ?";

   public static String clientId(String CLIENT_ID, String secred, String version, String location, String searchWord){
      String url = baseUrl+"client_id="+CLIENT_ID+"&client_secret="+secred+"&v="+version+"&ll="+location+ "&query="+searchWord;

       /*https://api.foursquare.com/v2/venues/search
       ?client_id=CLIENT_ID
               &client_secret=CLIENT_SECRET
               &v=20130815
               &ll=40.7,-74
               &query=sushi*/

       return url;

   }
}
