package pallettown;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pallettown.GUI.Log;

/**
 * Created by Owner on 3/02/2017.
 */
public class ProxyTester {

    public static WebDriver driver;

    public static void main(String[] args) {
    }

    public static void testProxies(ArrayList<PTCProxy> proxies){

        Logger shutUp = Logger.getLogger("");
        shutUp.setLevel(Level.OFF);

        for (PTCProxy proxy : proxies) {


            ArrayList<String> cliArgsCap = new ArrayList<String>();
            cliArgsCap.add("--proxy=https://" + proxy.IP());
            cliArgsCap.add("--proxy-type=https");
            if(!proxy.auth.equals("IP"))
                cliArgsCap.add("--proxy-auth=" + proxy.auth);
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();

            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,cliArgsCap);

            driver = new PhantomJSDriver(capabilities);



            Log("Testing proxy: " + proxy.IP());

            Log("Trying to connect to google.com");
            driver.get("https://www.google.com");

//            System.out.println(driver.getCurrentUrl());
            if(driver.getCurrentUrl().contains("www.google.com"))
                Log("Valid proxy, connected to www.google.com");
            else {
                Log("Could not connect to www.google.com, invalid proxy?");
                driver.close();
                driver.quit();
                continue;
            }

            Log("Trying to connect to PTC");
            driver.get("https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up");

//            System.out.println(driver.getCurrentUrl());
            if(driver.getCurrentUrl().equals("about:blank"))
                Log("Could not connect to PTC website, proxy banned?");
            else
                Log("Connected to PTC website!");

            Log(driver.getCurrentUrl());

            driver.close();
            driver.quit();
        }

    }

}
