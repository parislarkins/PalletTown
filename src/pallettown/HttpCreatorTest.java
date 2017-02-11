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

        HttpCreator.GetCsrfTasl(new Account("5-6-1991","mynovskey","J@kolantern7"),client);
    }

}
