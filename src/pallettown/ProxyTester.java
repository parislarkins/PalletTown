package pallettown;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static pallettown.GUI.Log;

/**
 * Created by Owner on 3/02/2017.
 */
class ProxyTester {

    private static final String TEST_URL = "https://pgorelease.nianticlabs.com/plfe/rpc";

    public static void main(String[] args) {
        testProxy("23.239.219.67:21260","IP");
    }


    public static boolean testProxy(String proxy, String auth) {

        boolean valid = false;

        int sepIndex = proxy.indexOf(":");

        String ip = proxy.substring(0,sepIndex);
        int port = Integer.parseInt(proxy.substring(sepIndex+1));

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {

            HttpHost proxyHost;

            if(auth.equals("IP")){
                proxyHost = new HttpHost(ip, port, "http");
            }else{
                proxyHost = new HttpHost(auth + "@" + ip,port,"http");
            }

            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxyHost)
                    .build();
            HttpPost request = new HttpPost(TEST_URL);
            request.setConfig(config);

            Log("Trying to connect to  " + TEST_URL + " via " + proxyHost);

            String responseLine;

            try (CloseableHttpResponse response = httpclient.execute(request)) {
//                System.out.println("----------------------------------------");
                responseLine = response.getStatusLine().toString();
//                System.out.println(responseLine);
                EntityUtils.consume(response.getEntity());
            } catch (HttpHostConnectException e){
                Log("Failed to establish proxy connection");
                return false;
            }

            int startIndex = responseLine.indexOf(" ") + 1;
            int endIndex = responseLine.lastIndexOf(" ");

            int responseCode = Integer.parseInt(responseLine.substring(startIndex,endIndex));

            switch(responseCode){
                case 200:
                    Log("Proxy " + proxy + " ok!");
                    valid = true;
                    break;
                case 403:
                    Log("Proxy " + proxy + " is banned, status code: " + responseLine);
                    break;
                default:
                    Log("Proxy " + proxy + " gave unexpected response, status code: " + responseLine);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return valid;
    }

}
