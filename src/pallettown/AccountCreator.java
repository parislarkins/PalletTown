package pallettown;

import javafx.application.Platform;
import pallettown.GUI.AccountThread;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static pallettown.GUI.Log;
import static pallettown.GUI.setStatus;

/**
 * Created by Paris on 20/01/2017.
 */
class AccountCreator implements Runnable{

    // --Commented out by Inspection (6/02/2017 10:54 AM):static final String BASE_URL = "https://club.pokemon.com/us/pokemon-trainer-club";
    // --Commented out by Inspection (6/02/2017 10:54 AM):private static final long CAPTCHA_TIMEOUT = 6000;
    // --Commented out by Inspection (6/02/2017 10:54 AM):private static final int THREADS = 5;

    private static String username;
    private static String password;
    private static String plusMail;
    private static String captchaKey = "";

    private static int WORK_ITEMS;

    private int accNum = 0;

    static int success = 0;

    private static ArrayList<PTCProxy> proxies = null;

    public static void createAccounts(String user, String pass, String plus, String captcha) {

        //Dont reset proxies, should instead have a flag to see if proxies need to be reloaded (eg file changed, else leave it)
        // If file needs to be changed, wipe and reload, else leave it

        if(PalletTown.changedProxies || proxies == null) {
            GUI.setStatus("Loading proxies...");

            proxies = new ArrayList<>();
            loadProxies();

//            seleniumTestProxies(proxies);

//            return false;

            PalletTown.changedProxies = false;
        }

        //5 accounts per IP per 10 minutes
        username = user;
        password = pass;
        plusMail = plus;
        captchaKey = captcha;

        WORK_ITEMS = PalletTown.count;


        GUI.setStatus("Starting account creation...");

        if(PalletTown.captchaKey.equals("")){
            Log("manual captcha");

            for (int i = 0; i < PalletTown.count; i++) {
                PTCProxy proxy = getProxy();
                createAccount(i, Thread.currentThread().getName(), proxy);
                proxy.Use();
            }
        }else{

            AccountCreator accCreator = new AccountCreator();
            Thread[] threads = new Thread[PalletTown.threads];

            for (int i = 0; i < PalletTown.threads; i++) {
                threads[i] = new Thread(accCreator,"Worker " + i);
            }

            for (int i = 0; i < PalletTown.threads; i++) {
                threads[i].start();
            }

            Log(Thread.currentThread().getName()+ " is twiddling its thumbs");
            try {
                for (int i = 0; i < PalletTown.threads; i++)
                    threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        GUI.setStatus("Finished account creation...");
        Log("done");

    }

    synchronized
    private static PTCProxy getProxy() {
        Log("getting proxy for " + Thread.currentThread().getName());

        PTCProxy shortestWait = null;

        for (int i = 0; i < proxies.size(); i++) {
            PTCProxy proxy = proxies.get(i);

            Log("    trying proxy " + i + ": " + proxy.IP());
            if(shortestWait == null){
                shortestWait = proxy;
            }

            if(proxy.NotStarted()){
                Log("    proxy unstarted, using..");
                proxy.ReserveUse();
                return proxy;
            }

            if(proxy.Usable()){
                Log("    proxy usable, using...");
                proxy.ReserveUse();
                return proxy;
            }else{
                Log("    proxy unusable");
                if(proxy.WaitTime() == 0){
                    Log("    proxy ready to be reset, updating queue and using...");
                    proxy.UpdateQueue();
                    proxy.ReserveUse();
                    return proxy;
                }
                if(proxy.WaitTime() < shortestWait.WaitTime()){
                    Log("    proxy new shortest delay");
                    shortestWait = proxy;
                }
            }
        }


        PTCProxy finalShortestWait = shortestWait;
        Platform.runLater(() -> {
            assert finalShortestWait != null;
            setStatus("Waiting for next available proxy...");
//            GUI.showAlert(Alert.AlertType.INFORMATION, "Waiting...", null, "Waiting " + PalletTown.millisToTime(finalShortestWait.WaitTime()) + " until IP restriction is lifted");
        });

        Log("    no available proxies, waiting for next available proxy...");
        try {
            assert shortestWait != null;
            Log("    shortest wait time: " + PalletTown.millisToTime(shortestWait.WaitTime()));
            Thread.currentThread().sleep(shortestWait.WaitTime());
            setStatus("Proxy available..");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shortestWait.UpdateQueue();
        shortestWait.ReserveUse();
        return shortestWait;
    }

    private static void loadProxies() {

        if(PalletTown.proxyFile == null){
            Log("no proxy file specified");
            proxies.add(new PTCProxy("null", "IP"));
            return;
        }

        try {
            Scanner in = new Scanner(PalletTown.proxyFile);

            while (in.hasNext()) {
                String proxy = in.nextLine();

                if(!proxy.startsWith("http") && !proxy.startsWith("socks5")){
                    proxy = "https://"+proxy;
                }


                String proxyIP = proxy.substring(proxy.indexOf("://") + 3);
                String proxyAuth = "IP";

                if(proxy.contains("@") && proxy.substring(0,proxy.indexOf("@")).contains(":")){
                    proxyIP = proxy.substring(proxy.indexOf("@") + 1);
                    proxyAuth = proxy.substring(0,proxy.indexOf("@"));
                    proxies.add(new PTCProxy(proxyIP, proxyAuth));
                }else{
                    if(ProxyTester.testProxy(proxyIP,proxyAuth)) {
                        Log("Adding " + proxy + " to list");
                        proxies.add(new PTCProxy(proxy, proxyAuth));
                    }
                    else
                        Log(proxy + " failed test, not adding");
                }
            }

            if(PalletTown.useNullProxy) proxies.add(new PTCProxy("null", "IP"));
        } catch (FileNotFoundException e) {
            Log("Invalid proxy file");
        }
    }


    @Override
    public void run() {
        int mytaskcount = 0;

        AccountThread accountThread = new AccountThread(Thread.currentThread().getName());

        GUI.addThread(accountThread);

        int accNum;
        while ((accNum = incAccNum()) < WORK_ITEMS) {
            Log(Thread.currentThread().getName()+" making account "+ accNum);
            accountThread.LogMessage("making account " + accNum);

            PTCProxy proxy = getProxy();
            boolean createAcc = createAccount(accNum,Thread.currentThread().getName(), proxy);
            Log(Thread.currentThread().getName() + "done making account " + accNum + " sleeping for " + PalletTown.delay+"ms");
            accountThread.LogMessage("done making account " + accNum + " sleeping for " + PalletTown.delay+"ms");
            if(createAcc){
                accountThread.Success();
            }else{
                accountThread.Failure();
            }
            proxy.Use();
            mytaskcount++;
            try {
                Thread.sleep(PalletTown.delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log(Thread.currentThread().getName()+" did "+mytaskcount+ " tasks");
        accountThread.LogMessage("did " + mytaskcount + " tasks");
    }

    synchronized
    private int incAccNum() {
        return accNum++;
    }

    static synchronized
    private void incSuccess(){
        success++;
    }

    private static boolean createAccount(int accNum, String name, PTCProxy proxy) {
        String birthday = RandomDetails.randomBirthday();

        Log("Making account #" + (accNum+1));

        String accUser;

        if(username == null){
//            Log("no username specified, generating one");
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
//            Log("no password specified, generating one");
            accPw = RandomDetails.randomPassword();
        }else{
            accPw = password;
        }

        String accMail = plusMail.replace("@","+" + accUser + "@");

        Log("  Username: " + accUser);
        Log("  Password: " + accPw);
        Log("  Email   : " + accMail);

        boolean createAcc = createAccPy(accUser,accPw,accMail,birthday,captchaKey,name, proxy.IP(), proxy.auth);

        Log(createAcc ? "Account " + accNum + " created succesfully" : "Account " + accNum + " failed");

        if(!createAcc)
            return false;

        incSuccess();
        if(PalletTown.outputFile != null)
            switch(PalletTown.outputFormat){
                case RocketMap:
                    PalletTown.outputAppend("ptc," + accUser+","+accPw);
                    break;
                case PokeAlert:
                    PalletTown.outputAppend(accUser + " " + password + " PTC None");
                    break;
                case Standard:
                    PalletTown.outputAppend(accUser+","+accPw);
                    break;
            }

        if(PalletTown.acceptTos)
            TOSAccept.acceptTos(accUser,password,accMail);
        else
            Log("Skipping TOS acceptance");

        return true;
    }

//    public static void main(String[] args) {
//        createAccPy("palletttrainer10","J@kolantern7","miminpari+pallettrainer10@hotmail.com","1957-1-1","5d579f38e793dc5b3d4905540a4215fa", name);
//    }

    private static boolean createAccPy(String username, String password, String email, String dob, String captchaKey, String name, String proxy, String authType){
        try{
            if(captchaKey == null || captchaKey.isEmpty()) captchaKey = "null";

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
                    proxy,
                    authType
            };

            ProcessBuilder pb = new ProcessBuilder(commands);

            pb.redirectErrorStream(true);

            Process p = pb.start();

            if(!p.waitFor(4, TimeUnit.MINUTES)){
                Log(Thread.currentThread().getName() + " python process timed out, terminating...");
                p.destroy();
                Thread.sleep(1000);
                if(p.isAlive()){
                    p.destroyForcibly();
                }
                return false;
            }

            Scanner in = new Scanner(new InputStreamReader(p.getInputStream()));

            String line = "dud";
            while(in.hasNext()){
                line = in.nextLine();
                if(PalletTown.debug)
                    Log("    [DEBUG]:" + line);
            }


            Log(line);
            if (line.contains(" successfully created."))
                return true;
//
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

}
