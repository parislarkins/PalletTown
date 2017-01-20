import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Paris on 20/01/2017.
 */
public class UrlUtil {

    public static String openUrl(String address, boolean checkResponse) {
        String response = "";
        URL url;
        try {
            url = new URL(address);
            try {
                URLConnection conn = url.openConnection();

                if(!checkResponse)
                    return "";

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                response = br.readLine();
//                System.out.println(response);
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
//                    System.out.println(inputLine);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return response == null ? "" : response;
    }
}
