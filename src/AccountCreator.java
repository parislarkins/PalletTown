import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Paris on 20/01/2017.
 */
public class AccountCreator implements Runnable{

    static final String BASE_URL = "https://club.pokemon.com/us/pokemon-trainer-club";
    private static final long CAPTCHA_TIMEOUT = 6000;
    private static final int THREADS = 2;

    private static String username;
    private static String password;
    private static String plusMail;
    private static String captchaKey;

    public static int WORK_ITEMS;

    int accNum = 0;

    public static boolean createAccounts(String user, String pass, String plus, String captcha) {

        username = user;
        password = pass;
        plusMail = plus;
        captchaKey = captcha;

        WORK_ITEMS = Pikaptcha.count;

        AccountCreator accCreator = new AccountCreator();
        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(accCreator,"Worker " + i);
        }

        for (int i = 0; i < THREADS; i++) {
            threads[i].start();
        }

        System.out.println(Thread.currentThread().getName()+ " is twiddling its thumbs");
        try {
            for (int i = 0; i < THREADS; i++)
                threads[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("done");


        return true;
    }

    @Override
    public void run() {

        int mytaskcount = 0;

        int accNum;
        while ((accNum = incAccNum()) < WORK_ITEMS) {
            createAccount(accNum);
            mytaskcount++;
        }

        System.out.println(Thread.currentThread().getName()+" did "+mytaskcount+ " tasks");
    }

    synchronized
    private int incAccNum() {
        return accNum++;
    }

    private void createAccount(int accNum) {
        String birthday = randomBirthday();

        System.out.println("Making account #" + (accNum+1));

        String accUser;

        if(Pikaptcha.count > 1 && Pikaptcha.startNum == null)
            accUser = Pikaptcha.userName + (accNum+1);
        else if (Pikaptcha.count >= 1 && Pikaptcha.startNum != null)
            accUser = Pikaptcha.userName + (Pikaptcha.startNum + accNum);
        else
            accUser = Pikaptcha.userName;

        String accMail = plusMail.replace("@","+" + accUser + "@");

        System.out.println("  Username: " + accUser);
        Pikaptcha.outputAppend(accUser+":"+password);
        System.out.println("  Password: " + password);
        System.out.println("  Email   : " + accMail);

        if(Pikaptcha.acceptTos)
            TOSAccept.acceptTos(accUser,password,accMail);
        else
            System.out.println("Skipping TOS acceptance");

        createAccPy(accUser,password,accMail,birthday, captchaKey);
    }

    private static boolean createAccPy(String username, String password, String email, String dob, String captchaKey){
        try{
            ProcessBuilder pb = new ProcessBuilder("python","src/accountcreate.py","\""+username + "\"",
                    "\""+password + "\"","\""+email + "\"","\""+dob + "\"","\""+captchaKey + "\"");

            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = in.readLine()) != null){
//                System.out.println(line);
                if(line.equals("Account succesfully created"))
                    System.out.println("account succesfully created");
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
