package pallettown;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * Created by Paris on 20/01/2017.
 */
public class AccountCreator implements Runnable{

    static final String BASE_URL = "https://club.pokemon.com/us/pokemon-trainer-club";
    private static final long CAPTCHA_TIMEOUT = 6000;
    private static final int THREADS = 5;

    private static String username;
    private static String password;
    private static String plusMail;
    private static String captchaKey;

    public static int WORK_ITEMS;

    int accNum = 0;

    static int success = 0;

    public static boolean createAccounts(String user, String pass, String plus, String captcha) {

        username = user;
        password = pass;
        plusMail = plus;
        captchaKey = captcha;

        WORK_ITEMS = PalletTown.count;

        if(PalletTown.captchaKey.equals("")){
            System.out.println("manual captcha");
            for (int i = 0; i < PalletTown.count; i++) {
                createAccount(i);
            }
        }else{
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
        }

        System.out.println("done");

        return true;
    }


    private volatile boolean exit = false;

    @Override
    public void run() {
        while(!exit){
            int mytaskcount = 0;

            int accNum;
            while ((accNum = incAccNum()) < WORK_ITEMS) {
                System.out.println(Thread.currentThread().getName()+" making account "+ accNum);
                createAccount(accNum);
                System.out.println(Thread.currentThread().getName() + "done making account " + accNum);
                mytaskcount++;
            }

            System.out.println(Thread.currentThread().getName()+" did "+mytaskcount+ " tasks");
        }
    }

    public void Stop(){
        exit = true;
    }

    synchronized
    private int incAccNum() {
        return accNum++;
    }

    static synchronized
    private void incSuccess(){
        success++;
    }

    private static void createAccount(int accNum) {
        String birthday = randomBirthday();

        System.out.println("Making account #" + (accNum+1));

        String accUser;

        if(PalletTown.count > 1 && PalletTown.startNum == null)
            accUser = PalletTown.userName + (accNum+1);
        else if (PalletTown.count >= 1 && PalletTown.startNum != null)
            accUser = PalletTown.userName + (PalletTown.startNum + accNum);
        else
            accUser = PalletTown.userName;

        String accMail = plusMail.replace("@","+" + accUser + "@");

        System.out.println("  Username: " + accUser);
        System.out.println("  Password: " + password);
        System.out.println("  Email   : " + accMail);

        boolean createAcc = createAccPy(accUser,password,accMail,birthday, captchaKey);

        System.out.println(createAcc ? "Account " + accNum + " created succesfully" : "Account " + accNum + " failed");

        if(!createAcc)
            return;

        incSuccess();
        if(PalletTown.outputFile != null)
            PalletTown.outputAppend(accUser+":"+password);

        if(PalletTown.acceptTos)
            TOSAccept.acceptTos(accUser,password,accMail);
        else
            System.out.println("Skipping TOS acceptance");
    }

    public static void main(String[] args) {
        createAccPy("palletttrainer10","J@kolantern7","miminpari+pallettrainer10@hotmail.com","1957-1-1","5d579f38e793dc5b3d4905540a4215fa");
    }

    private static boolean createAccPy(String username, String password, String email, String dob, String captchaKey){
        try{

//                    String.format("create_account(\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",60)\n",username,password,email,dob,captchaKey);

//            StringWriter writer = new StringWriter(); //ouput will be stored here
//
//            ScriptEngineManager manager = new ScriptEngineManager();
//            ScriptContext context = new SimpleScriptContext();
//
//            context.setWriter(writer); //configures output redirection
//            ScriptEngine engine = manager.getEngineByName("python");
//
//
//            engine.eval(prg, context);
//            System.out.println(writer.toString());
//
//            PythonInterpreter interpreter = new PythonInterpreter();
//            interpreter.exec(prg);
//            // execute a function that takes a string and returns a string
//            PyObject someFunc = interpreter.get("create_account");
//            PyObject result = someFunc.__call__(new PyString[] {new PyString(username),new PyString(password), new PyString(email), new PyString(dob), new PyString(captchaKey)});
//            String realResult = (String) result.__tojava__(String.class);

            ProcessBuilder pb = new ProcessBuilder().command("python","accountcreate.py","\""+username + "\"",
                    "\""+password + "\"","\""+email + "\"","\""+dob + "\"","\""+captchaKey + "\"");

            pb.redirectErrorStream(true);

            Process p = pb.start();

            Scanner in = new Scanner(new InputStreamReader(p.getInputStream()));

            Timer timer = new Timer(120000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                }
            });
            timer.setRepeats(false); // Only execute once
            timer.start(); // Go go go!

            String line = "dud";
            while(in.hasNext()){
                line = in.nextLine();
            }


            System.out.println(line);
            if (line.equals("Account successfully created."))
                return true;
//
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
