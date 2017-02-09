package pallettown;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static pallettown.GUI.Log;

/**
 * Created by Owner on 3/02/2017.
 */
class ProxyTester implements Runnable{


    private static int THREADS = 5;
    private static final String TEST_URL = "https://pgorelease.nianticlabs.com/plfe/rpc";

    private static ArrayList<String[]> proxies;

    private static ArrayList<String[]> validProxies;

    private static int proxyNum = 1;


    public static void main(String[] args) {
        testProxy("23.239.219.67:21260","IP");
    }

    public static ArrayList<String[]> testProxies(ArrayList<String[]> proxyList){
        validProxies = new ArrayList<>();

        proxies = proxyList;

        ProxyTester proxyTester = new ProxyTester();

//        THREADS = Math.max(5,proxies.size() / 2);
//        THREADS = 10;
        THREADS = proxies.size();

        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(proxyTester,"Worker " + i);
            threads[i].start();
        }

        Log("[" + Thread.currentThread().getName() + "]"+ " is twiddling its thumbs");
        try {
            for (int i = 0; i < THREADS; i++)
                threads[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log("Finished testing proxies. " + validProxies.size() + "/" + proxies.size() + " successes.");

        return validProxies;
    }

    synchronized
    private int incProxyNum() {
        return proxyNum++;
    }

    synchronized
    private void addSuccess(String ip, String auth){
        validProxies.add(new String[] {ip,auth});
    }

    @Override
    public void run() {
        int mytaskcount = 0;

        int proxyNum;
        while ((proxyNum = incProxyNum()) <= proxies.size()) {
            Log("[" + Thread.currentThread().getName() + "]"+" testing proxy: "+ proxyNum);

            String[] proxyInfo = proxies.get(proxyNum-1);

            if(testProxy(proxyInfo[0],proxyInfo[1])){
                addSuccess(proxyInfo[0],proxyInfo[1]);
            }

//            Log("Progress: " + proxyNum + "/" + proxies.size());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mytaskcount++;
        }

        Log("[" + Thread.currentThread().getName() + "]"+" did "+mytaskcount+ " tasks");
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
                    .setConnectTimeout(3000)
                    .setSocketTimeout(3000)
                    .setConnectionRequestTimeout(3000)
                    .build();
            HttpPost request = new HttpPost(TEST_URL);
            request.setConfig(config);

            Log("[" + Thread.currentThread().getName() + "]" + "Trying to connect to  " + TEST_URL + " via " + proxyHost);

            String responseLine;

            try (CloseableHttpResponse response = httpclient.execute(request)) {
//                System.out.println("----------------------------------------");
                responseLine = response.getStatusLine().toString();
//                System.out.println(responseLine);
                EntityUtils.consume(response.getEntity());

                httpclient.close();
            } catch (HttpHostConnectException e) {
                Log("[" + Thread.currentThread().getName() + "]" + "Failed to establish proxy connection");
                httpclient.close();
                return false;
            } catch (SSLHandshakeException e){
                Log("[" + Thread.currentThread().getName() + "]" + "SSH handshake error");
                httpclient.close();
                return false;
            } catch (ConnectTimeoutException e){
                Log("[" + Thread.currentThread().getName() + "]" + "Connection timed out");
                httpclient.close();
                return false;
            } catch (SocketException e){
                Log("[" + Thread.currentThread().getName() + "]" + "Connection reset by the server, failed to connect");
                httpclient.close();
                return false;
            } catch (SocketTimeoutException e){
                Log("[" + Thread.currentThread().getName() + "]" + "Socked connection timed out");
                httpclient.close();
                return false;
            } catch (SSLException e){
                Log("[" + Thread.currentThread().getName() + "]" + "Unrecognized SSL message, plaintext connection?");
                httpclient.close();
                return false;
            }


            String respCode = responseLine.substring(9);

            if(respCode.contains("200 ")){
                Log("[" + Thread.currentThread().getName() + "]" + "Proxy " + proxy + " ok!");
                valid = true;
            }else if(respCode.contains("403 ")){
                Log("[" + Thread.currentThread().getName() + "]" + "Proxy " + proxy + " is banned, status code: " + respCode);
            }else{
                Log("[" + Thread.currentThread().getName() + "]" + "Proxy " + proxy + " gave unexpected response, status code: " + respCode);
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
