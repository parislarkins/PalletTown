package pallettown;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    private static ArrayList<PTCProxy> proxies = new ArrayList<>();

    public static boolean createAccounts(String user, String pass, String plus, String captcha) {

        loadProxies();
        //5 accounts per IP per 10 minutes
        username = user;
        password = pass;
        plusMail = plus;
        captchaKey = captcha;

        WORK_ITEMS = PalletTown.count;

        if(PalletTown.captchaKey.equals("")){
            System.out.println("manual captcha");
            for (int i = 0; i < PalletTown.count; i++) {
                createAccount(i, Thread.currentThread().getName(), getProxy());
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

    synchronized
    private static String getProxy() {
        System.out.println("getting proxy for " + Thread.currentThread().getName());

        PTCProxy shortestWait = null;

        for (int i = 0; i < proxies.size(); i++) {
            PTCProxy proxy = proxies.get(i);

            System.out.println("    trying proxy " + i + ": " + proxy.IP());
            if(shortestWait == null){
                shortestWait = proxy;
            }

            if(!proxy.Started()){
                System.out.println("    proxy unstarted, using..");
                proxy.StartUsing();
                return proxy.IP();
            }

            if(proxy.Usable()){
                proxy.Use();
                System.out.println("    proxy usable, using...");
                return proxy.IP();
            }else{
                if(proxy.WaitTime() == 0){
                    System.out.println("    proxy ready to be reset, updating queue and using...");
                    proxy.UpdateQueue();
                    proxy.Use();
                    return proxy.IP();
                }
                System.out.println("    proxy unusable");
                if(proxy.WaitTime() < shortestWait.WaitTime()){
                    System.out.println("    proxy new shortest delay");
                    shortestWait = proxy;
                }
            }
        }

        System.out.println("    no available proxies, waiting for next available proxy...");
        try {
            System.out.println("    shortest wait time: " + shortestWait.WaitTime());
            Thread.sleep(shortestWait.WaitTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shortestWait.UpdateQueue();
        shortestWait.Use();
        return shortestWait.IP();
    }

    private static void loadProxies() {

        try {
            Scanner in = new Scanner(PalletTown.proxyFile);

            while(in.hasNext()){
                String proxy = in.nextLine();
                proxies.add(new PTCProxy(proxy));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int mytaskcount = 0;

        int accNum;
        while ((accNum = incAccNum()) < WORK_ITEMS) {
            System.out.println(Thread.currentThread().getName()+" making account "+ accNum);
            createAccount(accNum,Thread.currentThread().getName(), getProxy());
            System.out.println(Thread.currentThread().getName() + "done making account " + accNum);
            mytaskcount++;
        }

        System.out.println(Thread.currentThread().getName()+" did "+mytaskcount+ " tasks");
    }

    synchronized
    private int incAccNum() {
        return accNum++;
    }

    static synchronized
    private void incSuccess(){
        success++;
    }

    private static void createAccount(int accNum, String name, String proxy) {
        String birthday = RandomDetails.randomBirthday();

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

        boolean createAcc = createAccPy(accUser,password,accMail,birthday,captchaKey,name, proxy);

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

//    public static void main(String[] args) {
//        createAccPy("palletttrainer10","J@kolantern7","miminpari+pallettrainer10@hotmail.com","1957-1-1","5d579f38e793dc5b3d4905540a4215fa", name);
//    }

    private static boolean createAccPy(String username, String password, String email, String dob, String captchaKey, String name, String proxy){
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

            String[] commands = new String[] {
                    "python",
                    "accountcreate.py",
                    "\""+username + "\"",
                    "\""+password + "\"",
                    "\""+email + "\"",
                    "\""+dob + "\"",
                    "\""+captchaKey + "\"",
                    "\"" + name + "\"",
                    "\"" + proxy + "\""
            };

            ProcessBuilder pb = new ProcessBuilder(commands);

            pb.redirectErrorStream(true);

            Process p = pb.start();

            Scanner in = new Scanner(new InputStreamReader(p.getInputStream()));

//            Timer timer = new Timer(120000, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent arg0) {
//                }
//            });
//            timer.setRepeats(false); // Only execute once
//            timer.start(); // Go go go!

            String line = "dud";
            while(in.hasNext()){
                line = in.nextLine();
                if(PalletTown.debug)
                    System.out.println("    [DEBUG]:" + line);
            }


            System.out.println(line);
            if (line.contains("Account successfully created."))
                return true;
//
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

}
