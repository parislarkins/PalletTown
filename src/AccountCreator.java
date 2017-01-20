import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Paris on 20/01/2017.
 */
public class AccountCreator {

    static final String BASE_URL = "https://club.pokemon.com/us/pokemon-trainer-club";
    private static final long CAPTCHA_TIMEOUT = 6000;

    public static boolean createAccount(String username, String password, String plusMail, String captchaKey) {

//        System.out.println("Attempting to create user " + username + ":"+password +". Opening browser...");
        String birthday = randomBirthday();

        //String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36";
        //
        //  String userAgent = "Mozilla/5.0 AppleWebKit/537.36 Safari/537.36" + "Safari/537.36";
//
//
//        ChromeOptions co = new ChromeOptions();
//        co.addExtensions(new File("src\\chrome\\extension_2_1_1.crx"));
//        co.addArguments("--user-agent=" + userAgent);
//        DesiredCapabilities cap = DesiredCapabilities.chrome();
//
//        ChromeDriver driver = new ChromeDriver(cap);
//
//// set the context on the extension so the localStorage can be accessed
//        driver.get("chrome-extension://idgpnmonknjnojddfkpgkljpfnnfcklj/icon.png");
//
//        // setup ModHeader with two headers (token1 and token2)
//        driver.executeScript(
//                "localStorage.setItem('profiles', JSON.stringify([{                " +
//                        "  title: 'Selenium', hideComment: true, appendMode: '',           " +
//                        "  headers: [                                                      " +
//                        "    {enabled: true, name: 'token1', value: '01234', comment: ''}, " +
//                        "    {enabled: true, name: 'token2', value: '56789', comment: ''}  " +
//                        "  ],                                                              " +
//                        "  respHeaders: [],                                                " +
//                        "  filters: []                                                     " +
//                        "}]));                                                             " );
//
//        driver.manage().window().setSize(new Dimension(600,600));
//
//        System.out.println("Step 1: Verifying age using birthday: " + birthday);
//
//        driver.get(BASE_URL + "/sign-up/");
//
//        WebElement element = driver.findElement(By.name("dob"));
//
////        driver
//        driver.executeScript("var input = document.createElement('input'); input.type='text'; input.setAttribute('name', 'dob'); arguments[0].parentNode.replaceChild(input, arguments[0])",element);
//
//        element = driver.findElement(By.name("dob"));
//        element.sendKeys(birthday);
//        element.submit();
//
//        System.out.println("Step 2: Entering account details");
//        assert driver.getCurrentUrl().equals(BASE_URL + "/parents/sign-up");
//
//        WebElement user = driver.findElement(By.name("username"));
//        user.clear();
//        user.sendKeys(username);
//
//        WebElement elem = driver.findElement(By.name("password"));
//        elem.clear();
//        elem.sendKeys(password);
//
//        elem = driver.findElement(By.name("confirm_password"));
//        elem.clear();
//        elem.sendKeys(password);
//
//        elem = driver.findElement(By.name("email"));
//        elem.clear();
//        elem.sendKeys(plusMail);
//
//        elem = driver.findElement(By.name("confirm_email"));
//        elem.clear();
//        elem.sendKeys(plusMail);
//
//        driver.findElement(By.id("id_public_profile_opt_in_1")).click();
//        driver.findElement(By.name("terms")).click();
//
//        if (captchaKey.equals("")){
//            //Manually solve captcha
//            System.out.println("No 2captcha key passed. Please solve captcha manually");
//
//            elem = driver.findElement(By.className("g-recaptcha"));
//            driver.executeScript("arguments[0].scrollIntoView(true);", elem);
//
//            File file = driver.getScreenshotAs(OutputType.FILE);
//
//            try {
//                FileUtils.copyFile(file, new File("C:\\Users\\Paris\\Documents\\screenshot.png"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            driver.switchTo().frame("undefined");
//
//            WebElement captchaSpan = new WebDriverWait(driver,5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@id='recaptcha-anchor']")));
//            try {
//                WebDriverWait wait = new WebDriverWait(driver, 60);
//                wait.until(ExpectedConditions.attributeToBe(captchaSpan,"aria-checked","true"));
//            } catch (TimeoutException e){
//                System.out.println("Timed out while solving captcha");
//            }
//        }else{
//            //Automatically solve captcha
//
//            System.out.println("Starting autosolve captcha");
//
//            String html = driver.getPageSource();
//
//            int gkey_index = html.indexOf("https://www.google.com/recaptcha/api2/anchor?k=") + 47;
//            String gkey = html.substring(gkey_index,gkey_index+40);
//
//            String recaptchaResponse = "Failed";
//
//            while(recaptchaResponse.equals("Failed")){
//                recaptchaResponse = UrlUtil.openUrl("http://2captcha.com/in.php?key=" + captchaKey + "&method=userrecaptcha&googlekey=" + gkey, true);
//            }
//
//            String captchaId = recaptchaResponse.substring(3);
//
//            recaptchaResponse = "CAPTCHA_NOT_READY";
//
//            elem = driver.findElement(By.className("g-recaptcha"));
//            System.out.println("Waiting 10 seconds for captcha to be solved");
//
//            long startTime = System.currentTimeMillis();
//            boolean timedout = false;
//
//            while (recaptchaResponse.equals("CAPTCHA_NOT_READY")){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                long elapsedTime = System.currentTimeMillis() - startTime;
//
//                if(elapsedTime > CAPTCHA_TIMEOUT){
//                    System.out.println("Captcha timed out");
//                    timedout = true;
//                    break;
//                }
//
//                System.out.println("Captcha still not solved, waiting 10 seconds");
//                recaptchaResponse = "Failed";
//
//                while(recaptchaResponse.equals("Failed")){
//                    recaptchaResponse = UrlUtil.openUrl("http://2captcha.com/res.php?key=" + captchaKey + "&action=get&id=" + captchaId, true);
//
//                }
//            }
//
//            if(!timedout){
//                String solveCaptcha = recaptchaResponse.substring(3);
//                int length = solveCaptcha.length();
//
//                elem = driver.findElement(By.name("g-recaptcha-response"));
//                elem = (WebElement) driver.executeScript("arguments[0].style.display = 'block'; return arguments[0];", elem);
//                elem.sendKeys(solveCaptcha);
//
//                System.out.println("Solved Captcha");
//            }
//        }
//
//        System.out.println("Account succesfully created.");
//
//        driver.switchTo().defaultContent();
//
//        driver.findElement(By.xpath("//input[@value=' Continue']"));
//
//        driver.close();*/

        createAccPy(username,password,plusMail,birthday, captchaKey);

        return true;
    }

    private static boolean createAccPy(String username, String password, String email, String dob, String captchaKey){
        try{
            ProcessBuilder pb = new ProcessBuilder("python","src/accountcreate.py","\""+username + "\"",
                    "\""+password + "\"","\""+email + "\"","\""+dob + "\"","\""+captchaKey + "\"");

            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = in.readLine()) != null){
                System.out.println(line);
                if(line.equals("Account succesfully created"))
                    return true;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private static String randomBirthday() {
        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(1900, 2000);

        gc.set(Calendar.YEAR, year);

        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return (gc.get(Calendar.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}
