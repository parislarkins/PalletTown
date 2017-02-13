package pallettown;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Owner on 11/02/2017.
 */
public class HttpCreatorTest{

    private static final ExecutorService threadpool = Executors.newFixedThreadPool(3);
    private static HttpClient client;

    public static void main(String[] args) {

        client = HttpClients.createDefault();

        Account account = new Account("5-6-1991","mynovskey","J@kolantern7","miminpari+mynovskey@hotmail.com","Australia");
        HttpCreator.GetCsrfTask(account,client);

        System.out.println(account.Csrf);

        System.out.println(HttpCreator.AgeVerifyTask(account,client).Success);


        PalletTown.captchaKey = "5d579f38e793dc5b3d4905540a4215fa";
        System.out.println(HttpCreator.StartSolveCaptchaTask(account,client, "138.128.66.121:21260", "http").Success);

        System.out.println(HttpCreator.GetSolvedCaptchaTask(account).Success);

        System.out.println(HttpCreator.ProfileSettingsTask(account,client).Success);


    }

}
