package pallettown;

import com.twocaptcha.api.TwoCaptchaService;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Owner on 11/02/2017.
 */
public class HttpCreator {

    private static final String VERIFY_AGE_URL = "https://club.pokemon.com/us/pokemon-trainer-club/sign-up/";
    static final String SIGNUP_URL = "https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up";
    private static final String GO_SETTINGS = "https://club.pokemon.com/us/pokemon-trainer-club/go-settings";
    private static final String ACTIVATED = "https://club.pokemon.com/us/pokemon-trainer-club/activated";
    private static final String CAPTCHA_IN = "http://2captcha.com/in.php?";
    private static final String CAPTCHA_OUT = "http://2captcha.com/res.php?";
    static final String RECAPTCHA_SITEKEY = "6LdpuiYTAAAAAL6y9JNUZzJ7cF3F8MQGGKko1bCy";

    private static final HttpGet GET_VERIFY_AGE_URL = new HttpGet(VERIFY_AGE_URL);

//    private static final Regex RegexCsrf =
//            new Regex("<input type='hidden' name='csrfmiddlewaretoken' value='(\\w+)' />");

//    private static final String RegexCsrf = "<input type='hidden' name='csrfmiddlewaretoken' value='(\\w+)' />";

    private static final Pattern RegexCsrf = Pattern.compile("<input type='hidden' name='csrfmiddlewaretoken' value='(\\w+)' />");

    private static final Regex RegexLt =
            new Regex("<input type=\\\"hidden\\\" name=\\\"lt\\\" value=\\\"([A-Za-z0-9-]+)\\\" />");

    private static final Regex RegexExecution =
            new Regex("<input type=\\\"hidden\\\" name=\\\"execution\\\" value=\\\"(\\w+)\\\" />");

    private static final Regex RegexEventId =
            new Regex("<input type=\\\"hidden\\\" name=\\\"_eventId\\\" value=\\\"(\\w+)\\\" />");


    public static MethodResult GetCsrfTask(Account account, HttpClient client){
        MethodResult methodResult = new MethodResult();

//        new Thread(() -> {
            try {
                HttpResponse httpResponse = client.execute(GET_VERIFY_AGE_URL);

                if(httpResponse.getStatusLine().getStatusCode() == 200){
                    String result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
//                    System.out.println(result);

                    methodResult.Value = result;

                    Matcher matcher = RegexCsrf.matcher(result);

                    if(matcher.find()){
                        account.Csrf = matcher.group(1);
                    }

                    methodResult.Success = !account.Csrf.isEmpty();
                    methodResult.Value = result;
                }

            } catch (Exception e){
                methodResult.Error = e;
                methodResult.Success = false;
            }
//        }).start();

        return methodResult;
    }

    public static MethodResult AgeVerifyTask(Account account, HttpClient client){

        MethodResult methodResult = new MethodResult();

        try{

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("csrfmiddlewaretoken", account.Csrf));
            params.add(new BasicNameValuePair("dob",account.dob));
            params.add(new BasicNameValuePair("country", account.Country));
            params.add(new BasicNameValuePair("country", account.Country));

            HttpPost post = new HttpPost(VERIFY_AGE_URL);
            post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

            if(Arrays.binarySearch(post.getAllHeaders(),"Referer") != -1){
                post.addHeader("Referer",VERIFY_AGE_URL);
            }

            HttpResponse response = client.execute(post);

            methodResult.Success = response.getStatusLine().getStatusCode() == 200;

            methodResult.Value = EntityUtils.toString(response.getEntity(),"UTF-8");

        } catch (Exception e) {
            methodResult.Error = e;
            methodResult.Success = false;
        }

        return methodResult;
    }

//    public static MethodResult StartSolveCaptchaTask(Account account, HttpClient client, String proxy, String proxyType){
//
//        MethodResult methodResult = new MethodResult();
//
//        String postData = String.format(
//                "key=%s&method=userrecaptcha&googlekey=%s&proxy=%s&proxytype=%s&pageurl=%s",
//                PalletTown.captchaKey,
//                RECAPTCHA_SITEKEY,
//                proxy,
//                proxyType,
//                SIGNUP_URL
//                );
//
//        try{
//
//            String result = SendRecaptchav2RequestTask(CAPTCHA_IN,postData);
//
//            methodResult.Value = result;
//
//            if(result.contains("OK|")){
//                account.CaptchaId = result.substring(3,result.length() - 3);
//                methodResult.Success = true;
//            }else{
//                methodResult.Error = new Exception(result);
//                methodResult.Success = false;
//            }
//        }catch (Exception e) {
//            methodResult.Error = e;
//            methodResult.Success = false;
//        }
//
//        return methodResult;
//    }

    public static String solveCaptcha(String apiKey, String siteKey, String pageUrl){

        TwoCaptchaService service = new TwoCaptchaService(apiKey, siteKey, pageUrl);

        try {
            String responseToken = service.solveCaptcha();
            System.out.println("The response token is: " + responseToken);

            return responseToken;
        } catch (InterruptedException e) {
            System.out.println("ERROR case 1");
            e.printStackTrace();
            return "error";
        } catch (IOException e) {
            System.out.println("ERROR case 2");
            e.printStackTrace();
            return "error";
        }
    }

//    private static String SendRecaptchav2RequestTask(String address, String post) {
//
//        final String[] responseString = new String[1];
//
////        new Thread(() -> {
//            HttpURLConnection connection = null;
//
//            try {
//                URL url = new URL(address);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setDoOutput(true);
//
//                byte[] data = post.getBytes(StandardCharsets.US_ASCII);
//                connection.setFixedLengthStreamingMode(data.length);
//
//                DataOutputStream stream = new DataOutputStream (
//                        connection.getOutputStream());
//                stream.writeBytes(post);
//                stream.close();
//
//                InputStream is = connection.getInputStream();
//                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    response.append(line);
//                    response.append('\r');
//                }
//                rd.close();
//
//                responseString[0] = response.toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                if(connection != null){
//                    connection.disconnect();
//                }
//            }
////        });
//
//        return responseString[0];
//    }
//
//    public static MethodResult GetSolvedCaptchaTask(Account account){
//        MethodResult methodResult = new MethodResult();
//
//        String postData = String.format("key=%s&action=get&id=%s",
//                PalletTown.captchaKey,
//                account.CaptchaId
//                );
//
//        try{
//            String result = SendRecaptchav2RequestTask(CAPTCHA_OUT, postData);
//
//            while(result.contains("CAPTCHA_NOT_READY")){
//                Thread.sleep(3000);
//                result = SendRecaptchav2RequestTask(CAPTCHA_OUT, postData);
//            }
//
//            methodResult.Value = result;
//
//            if(result.contains("OK|")){
//                account.CaptchaResponse = result.substring(3,result.length()-3);
//                methodResult.Success = true;
//            }else{
//                methodResult.Error = new Exception(result);
//                methodResult.Success = false;
//            }
//        }catch (Exception e){
//            methodResult.Error = e;
//            methodResult.Success = false;
//        }
//
//        return methodResult;
//    }

    public static MethodResult ProfileSettingsTask(Account account, HttpClient client){

        MethodResult methodResult = new MethodResult();
//
//        HttpURLConnection connection = null;
//
//        List<NameValuePair> params = new ArrayList<>(10);
//            params.add(new BasicNameValuePair("csrfmiddlewaretoken", account.Csrf));
//            params.add(new BasicNameValuePair("username",account.user));
//            params.add(new BasicNameValuePair("password", account.pass));
//            params.add(new BasicNameValuePair("confirm_password", account.pass));
//            params.add(new BasicNameValuePair("email", account.email));
//            params.add(new BasicNameValuePair("confirm_email", account.email));
////            params.add(new BasicNameValuePair("public_profile_opt_in", account..toString()));
//            params.add(new BasicNameValuePair("public_profile_opt_in", "off"));
//            params.add(new BasicNameValuePair("screen_name", account.user));
//            params.add(new BasicNameValuePair("terms", "on"));
//            params.add(new BasicNameValuePair("g-recaptcha-response", account.CaptchaResponse));
//
//        String post = null;
//        try {
//            post = EntityUtils.toString(new UrlEncodedFormEntity(params,"UTF-8"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            URL url = new URL(SIGNUP_URL);
//            connection = (HttpURLConnection) url.openConnection();
////            connection.connect();
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
//            connection.setRequestProperty( "charset", "utf-8");
//            connection.pro
//
//            byte[] data = post.getBytes(StandardCharsets.US_ASCII);
////            connection.setFixedLengthStreamingMode(data.length);
//            connection.setRequestProperty( "Content-Length", Integer.toString(data.length));
//            connection.setUseCaches(false);
//
//            DataOutputStream stream = new DataOutputStream (
//                    connection.getOutputStream());
//            stream.writeBytes(post);
//            stream.close();
//
//            InputStream is = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//            String line;
//            while ((line = rd.readLine()) != null) {
//                response.append(line);
//                response.append('\r');
//            }
//            rd.close();
//
//            String result = response.toString();
//
//            if(connection.getResponseCode() == 200){
//
//                if(connection.getURL().toURI().toString().contains("exists")){
//                    methodResult.Error = new Exception("Account Already Exists");
//                    methodResult.Success = false;
//                }else if(connection.getURL().toURI().toString().contains("exceed")){
//                    methodResult.Error = new Exception("Rate Limit Exceeded");
//                    methodResult.Success = false;
//                }else if(connection.getURL().toURI().toString().contains("email")){
//                    methodResult.Success = true;
//                }else{
//                    methodResult.Error = new Exception("Unknown Error");
//                    methodResult.Success = false;
//                }
//            }
//
//            methodResult.Value = result;
//
//        } catch (Exception e){
//            methodResult.Error = e;
//            methodResult.Success = false;
//        }finally {
//            if(connection != null){
//                connection.disconnect();
//            }
//        }

        try {
            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<>(2);
            params.add(new BasicNameValuePair("csrfmiddlewaretoken", account.Csrf));
            params.add(new BasicNameValuePair("username",account.user));
            params.add(new BasicNameValuePair("password", account.pass));
            params.add(new BasicNameValuePair("confirm_password", account.pass));
            params.add(new BasicNameValuePair("email", account.email));
            params.add(new BasicNameValuePair("confirm_email", account.email));
//            params.add(new BasicNameValuePair("public_profile_opt_in", account.publicProfileOptIn.toString()));
            params.add(new BasicNameValuePair("public_profile_opt_in", "off"));
            params.add(new BasicNameValuePair("screen_name", account.user));
            params.add(new BasicNameValuePair("terms", "on"));
            params.add(new BasicNameValuePair("g-recaptcha-response", account.CaptchaResponse));

            HttpPost post = new HttpPost(SIGNUP_URL);
            post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

//            HttpHost proxy = new HttpHost("138.128.66.121",21260);
//            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//            client = HttpClients.custom()
//                    .setRoutePlanner(routePlanner)
//                    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//                    .build();

            HttpResponse response = client.execute(post);

            String result = EntityUtils.toString(response.getEntity(),"UTF-8");
            if(response.getStatusLine().getStatusCode() == 200){

                if(result.contains("exists")){
                    methodResult.Error = new Exception("Account Already Exists");
                    methodResult.Success = false;
                }else if(result.contains("exceed")){
                    methodResult.Error = new Exception("Rate Limit Exceeded");
                    methodResult.Success = false;
                }else if(result.contains("email")){
                    methodResult.Success = true;
                }else{
                    methodResult.Error = new Exception("Unknown Error");
                    methodResult.Success = false;
                }
            }

            methodResult.Value = result;

        } catch (Exception e){
            methodResult.Error = e;
            methodResult.Success = false;
        }

        return methodResult;
    }


}
