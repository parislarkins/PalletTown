package pallettown;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Owner on 11/02/2017.
 */
public class HttpCreator {

    private static final String VERIFY_AGE_URL = "https://club.pokemon.com/us/pokemon-trainer-club/sign-up/";
    private static final String SIGNUP_URL = "https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up";
    private static final String GO_SETTINGS = "https://club.pokemon.com/us/pokemon-trainer-club/go-settings";
    private static final String ACTIVATED = "https://club.pokemon.com/us/pokemon-trainer-club/activated";
    private static final String CAPTCHA_IN = "http://2captcha.com/in.php?";
    private static final String CAPTCHA_OUT = "http://2captcha.com/res.php?";
    private static final String RECAPTCHA_SITEKEY = "6LdpuiYTAAAAAL6y9JNUZzJ7cF3F8MQGGKko1bCy";

    private static final HttpGet GET_VERIFY_AGE_URL = new HttpGet(VERIFY_AGE_URL);

//    private static final Regex RegexCsrf =
//            new Regex("<input type='hidden' name='csrfmiddlewaretoken' value='(\\w+)' />");

//    private static final String RegexCsrf = "<input type='hidden' name='csrfmiddlewaretoken' value='(\\w+)' />";

    private static final Pattern RegexCsrf = Pattern.compile("<input type='hidden' name='csrfmiddlewaretoken' value='(\\\\w+)' />");

    private static final Regex RegexLt =
            new Regex("<input type=\\\"hidden\\\" name=\\\"lt\\\" value=\\\"([A-Za-z0-9-]+)\\\" />");

    private static final Regex RegexExecution =
            new Regex("<input type=\\\"hidden\\\" name=\\\"execution\\\" value=\\\"(\\w+)\\\" />");

    private static final Regex RegexEventId =
            new Regex("<input type=\\\"hidden\\\" name=\\\"_eventId\\\" value=\\\"(\\w+)\\\" />");


    public static MethodResult GetCsrfTasl(Account account, HttpClient client){
        MethodResult methodResult = new MethodResult();

        new Thread(() -> {
            try {
                HttpResponse httpResponse = client.execute(GET_VERIFY_AGE_URL);

                if(httpResponse.getStatusLine().getStatusCode() == 200){
                    HttpEntity entity = httpResponse.getEntity();
                    String result = EntityUtils.toString(entity,"UTF-8");
                    System.out.println(result);

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
        }).start();

        return methodResult;
    }


}
