package pallettown;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
    private static String captchaKey = "";

    public static int WORK_ITEMS;

    int accNum = 0;

    static int success = 0;

    private static ArrayList<PTCProxy> proxies = new ArrayList<>();

    private static long startTime = 0;
    private static long endTime = 0;


    public static boolean createAccounts(String user, String pass, String plus, String captcha) {

        //5 accounts per IP per 10 minutes
        username = user;
        password = pass;
        plusMail = plus;
        captchaKey = captcha;

        WORK_ITEMS = PalletTown.count;

        startTime = System.currentTimeMillis();

        if(PalletTown.captchaKey.equals("")){
            System.out.println("manual captcha");
            for (int i = 0; i < PalletTown.count; i++) {
//                PTCProxy proxy = getProxy();
                createAccount(i, Thread.currentThread().getName(), "");
//                proxy.Use();
            }
        }else{

            loadProxies();

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
                    threads[i].join(360000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("done");

        return true;
    }

    synchronized
    private static PTCProxy getProxy() {
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
                proxy.ReserveUse();
                return proxy;
            }

            if(proxy.Usable()){
                System.out.println("    proxy usable, using...");
                proxy.ReserveUse();
                return proxy;
            }else{
                System.out.println("    proxy unusable");
                if(proxy.WaitTime() == 0){
                    System.out.println("    proxy ready to be reset, updating queue and using...");
                    proxy.UpdateQueue();
                    proxy.ReserveUse();
                    return proxy;
                }
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
        shortestWait.ReserveUse();
        return shortestWait;
//        System.out.println("getting proxy for " + Thread.currentThread().getName());
//
//        PTCProxy shortestWait = null;
//
//        for (int i = 0; i < proxies.size(); i++) {
//            PTCProxy proxy = proxies.get(i);
//
//            System.out.println("    trying proxy " + i + ": " + proxy.IP());
//            if(shortestWait == null){
//                shortestWait = proxy;
//            }
//
//            if(!proxy.Started()){
//                System.out.println("    proxy unstarted, using..");
//                proxy.StartUsing();
//                return proxy.IP();
//            }
//
//            if(proxy.Usable()){
//                proxy.Use();
//                System.out.println("    proxy usable, using...");
//                return proxy.IP();
//            }else{
////                if(proxy.WaitTime() == 0){
////                    System.out.println("    proxy ready to be reset, updating queue and using...");
////                    proxy.UpdateQueue();
////                    proxy.Use();
////                    return proxy.IP();
////                }
//                System.out.println("    proxy unusable");
////                if(proxy.WaitTime() < shortestWait.WaitTime()){
////                    System.out.println("    proxy new shortest delay");
////                    shortestWait = proxy;
////                }
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//
//        long millis = endTime - startTime;
//        String time = String.format("%02d min, %02d sec",
//                TimeUnit.MILLISECONDS.toMinutes(millis),
//                TimeUnit.MILLISECONDS.toSeconds(millis) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
//        );
//
//        System.out.println("    All proxies unavailable. It took " + time + " to use all proxies.");
//
////        millis = PTCProxy.RESET_TIME - millis + 60000;
//
//        millis = PTCProxy.RESET_TIME + 30000;
//        time = String.format("%02d min, %02d sec",
//                TimeUnit.MILLISECONDS.toMinutes(millis),
//                TimeUnit.MILLISECONDS.toSeconds(millis) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
//        );
//        System.out.println("    Waiting " + time + " before resuming account creation");
//
//        String proxy = "";
//        try {
//            Thread.sleep(millis);
//
//            startTime = System.currentTimeMillis();
//            System.out.println("    Done waiting, resetting all proxies");
//            for (PTCProxy px : proxies) {
//                px.Reset();
//            }
//
//            proxy = getProxy();
//
//            System.out.println("    Done, getting new proxy: " + proxy);
//
//            return proxy;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
////        System.out.println("    no available proxies, waiting for next available proxy...");
////        try {
////            System.out.println("    shortest wait time: " + shortestWait.WaitTime());
////            Thread.sleep(shortestWait.WaitTime());
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        shortestWait.UpdateQueue();
////        shortestWait.Use();
//        return proxy;
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

            PTCProxy proxy = getProxy();
            createAccount(accNum,Thread.currentThread().getName(), proxy.IP());
            System.out.println(Thread.currentThread().getName() + "done making account " + accNum + " sleeping for 500ms");
            proxy.Use();
            mytaskcount++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

        if(username == null){
//            System.out.println("no username specified, generating one");
            accUser = RandomDetails.randomUsername();
        }else{
            if(PalletTown.count > 1 && PalletTown.startNum == null)
                accUser = PalletTown.userName + (accNum+1);
            else if (PalletTown.count >= 1 && PalletTown.startNum != null)
                accUser = PalletTown.userName + (PalletTown.startNum + accNum);
            else
                accUser = PalletTown.userName;
        }

        String accPw;
        if(password == null){
//            System.out.println("no password specified, generating one");
            accPw = RandomDetails.randomPassword();
        }else{
            accPw = password;
        }

        String accMail = plusMail.replace("@","+" + accUser + "@");

        System.out.println("  Username: " + accUser);
        System.out.println("  Password: " + accPw);
        System.out.println("  Email   : " + accMail);

        boolean createAcc = createAccPy(accUser,accPw,accMail,birthday,captchaKey,name, proxy);

        System.out.println(createAcc ? "Account " + accNum + " created succesfully" : "Account " + accNum + " failed");

        if(!createAcc)
            return;

        incSuccess();
        if(PalletTown.outputFile != null)
            PalletTown.outputAppend("ptc," + accUser+","+accPw);

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

            if(captchaKey.isEmpty()) captchaKey = "null";

            if(proxy.isEmpty()) proxy = "null";

            String[] commands = new String[] {
                    "python",
                    "accountcreate.py",
                    username,
                    password,
                    email,
                    dob,
                    captchaKey,
                    name,
                    proxy
            };

//            for (String command : commands) {
//                System.out.println(command);
//            }

//            ProcessBuilder pb = new ProcessBuilder("python", "accountcreate.py " + username + " " + password + " " + email +
//                    " " + dob + " \"\" " + name + " " + " \"\" ");
            ProcessBuilder pb = new ProcessBuilder(commands);

            pb.redirectErrorStream(true);

            Process p = pb.start();

            if(!p.waitFor(6, TimeUnit.MINUTES)){
                System.out.println(Thread.currentThread().getName() + " python process timed out, terminating...");
                p.destroy();
                Thread.sleep(1000);
                if(p.isAlive()){
                    p.destroyForcibly();
                }
                return false;
            }

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
