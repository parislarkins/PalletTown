package pallettown;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pallettown.GUI.Log;

/**
 * Created by Paris on 20/01/2017.
 */
public class PalletTown implements Runnable {

    public static final double VERSION = 1.5;

    public static String plusMail;
    public static String userName;
    public static String password;
    public static Integer startNum;
    public static int count;
    public static String captchaKey;
    public static boolean autoVerify;
    public static String avMail;
    public static String avPass;
    public static boolean acceptTos;
    public static File outputFile;
    public static File proxyFile;
    public static boolean debug = false;
    public static int threads = 5;
    public static int delay = 5000;
    public static int resetTime = 960000;
    public static OutputFormat outputFormat = OutputFormat.RocketMap;
    public static boolean useNullProxy = true;
    public static boolean privateDomain = false;
    private static final File settingsFile = new File("pallettown.config");

    public static boolean changedProxies = true;

    private static void Start(){
        parseArgs();

        saveSettings();

        AccountCreator.success = 0;

        // Disabled as it doesn't work due to running on a seperate thread
//        if(!captchaKey.equals("")){
//            Platform.runLater(() -> {
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("2Captcha Balance");
//                alert.setHeaderText(null);
//
//                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
//                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
//
//                alert.getButtonTypes().setAll(yes, no);
//                alert.setContentText("Your 2Captcha balance is: " + checkBalance() + ".\n" +
//                                      "This run will cost approximately: " + (double)Math.round((count * 0.0009) * 1000d) / 1000d +
//                                      ".\nDo you wish to proceed?");
//                alert.showAndWait().ifPresent(rs -> {
//                    if (rs == no) {
//                        Log("cancel");
//                        return;
//                    }
//                });
//            });
//        }

        String verify = verifySettings();

        if(!verify.equals("valid")){
            Platform.runLater(() -> GUI.showAlert(Alert.AlertType.WARNING,"Input Error", null, verify));
            Log("aborting...");
            return;
        }

//        if(autoVerify && outputFile != null){
//            outputAppend("\nThe following accounts use the email address: " + plusMail + "\n");
//        }

        Log("Starting");

        AccountCreator.createAccounts(userName,password,plusMail,captchaKey);

        Log(AccountCreator.success + "/" + count + " successes");

        if(AccountCreator.success == 0)
            return;

        if(autoVerify && !avMail.equals("") && !avPass.equals("")){
            Log("Account Creation done");

            Log("Waiting 4 minutes to verify emails");
            try {
                Thread.sleep(240000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            EmailVerifier.delayedVerify(avMail, avPass, AccountCreator.success,5);
            EmailVerifier.verify(avMail, avPass,AccountCreator.success,5);
            Log("Done!");
        }

    }

    synchronized
    static void outputAppend(String s) {
        // append to end of file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile,true))) {
            if(!newLineExists(outputFile)) bw.newLine();
            bw.write(s);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean newLineExists(File file) throws IOException {
        RandomAccessFile fileHandler = new RandomAccessFile(file, "r");
        long fileLength = fileHandler.length() - 1;
        if (fileLength < 0) {
            fileHandler.close();
            return true;
        }
        fileHandler.seek(fileLength);
        byte readByte = fileHandler.readByte();
        fileHandler.close();

        return readByte == 0xA || readByte == 0xD;
    }

    private static String verifySettings() {

        if(plusMail.equals("") || !plusMail.contains("@") || plusMail.contains("gmail.com"))
            return "Check email is correct and not a gmail address and try again";

//        if(userName.equals(""))
//            return "Please enter a username";
//
        if(password != null && !validatePass(password))
            return "Invalid password.\nPassword must contain a symbol, number, and capital letter";

        if(count < 1)
            return "Please set count to at least 1";

        if((startNum == null || startNum == 0) && count > 1 && userName != null)
            return "To create more than 1 account, specify a start number";

        if(autoVerify && (avMail.equals("") || avPass.equals("") || (!avMail.contains("@gmail.com") && !avMail.contains("@hotmail."))))
            return "Check auto verify account/password are correct (Use hotmail or gmail)";

        return "valid";
    }

    private static boolean validatePass(String password) {
        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
}

    //Checks 2captcha balance
    public static float checkBalance(){
        String balance = "Failed";

        while (balance.equals("Failed")){
            balance = UrlUtil.openUrl("http://2captcha.com/res.php?key=" + captchaKey + "&action=getbalance");
        }

        return Float.valueOf(balance);
    }

    private static void parseArgs() {
        VBox vb = (VBox) GUI.controls.getChildren().get(0);

        HBox pm = (HBox) vb.getChildren().get(0);
        TextField pmt = (TextField) pm.getChildren().get(1);
        plusMail = pmt.getText();

        HBox user = (HBox) vb.getChildren().get(1);
        TextField uname = (TextField) user.getChildren().get(1);
        userName = uname.getText().equals("") ? null : uname.getText();

        HBox pass = (HBox) vb.getChildren().get(2);
        TextField pw = (TextField) pass.getChildren().get(1);
        password = pw.getText().equals("") ? null : pw.getText();

        HBox sn = (HBox) vb.getChildren().get(4);
        TextField s = (TextField) sn.getChildren().get(1);
        startNum = s.getText().equals("") ? null : Integer.parseInt(s.getText());

        HBox c = (HBox) vb.getChildren().get(3);
        TextField cn = (TextField) c.getChildren().get(1);
        count = cn.getText().equals("") ? 0 : Integer.parseInt(cn.getText());

        HBox captcha = (HBox) vb.getChildren().get(5);
        TextField cap = (TextField) captcha.getChildren().get(1);
        captchaKey = cap.getText();

        CheckBox autoV = (CheckBox) vb.getChildren().get(6);
        autoVerify = autoV.isSelected();

        HBox avM = (HBox) vb.getChildren().get(7);
        TextField avMText = (TextField) avM.getChildren().get(1);
        avMail = avMText.getText();

        HBox avP = (HBox) vb.getChildren().get(8);
        TextField avPText = (TextField) avP.getChildren().get(1);
        avPass = avPText.getText();

        CheckBox tos = (CheckBox) vb.getChildren().get(9);
        acceptTos = tos.isSelected();

        HBox output = (HBox) vb.getChildren().get(10);
        TextField path = (TextField) output.getChildren().get(1);
        outputFile = path.getText().equals("") ? null : new File(path.getText());

        HBox proxy = (HBox) vb.getChildren().get(11);
        TextField proxyPath = (TextField) proxy.getChildren().get(1);
        proxyFile = proxyPath.getText().equals("") ? null : new File(proxyPath.getText());

        VBox advancedVb = (VBox) GUI.advancedRoot.getChildren().get(0);

        HBox threadbox = (HBox) advancedVb.getChildren().get(0);
        TextField threadNum = (TextField) threadbox.getChildren().get(1);
        threads = threadNum.getText().equals("") ? 5 : Integer.parseInt(threadNum.getText());

        HBox delayBox = (HBox) advancedVb.getChildren().get(1);
        TextField delayNum = (TextField) delayBox.getChildren().get(1);
        delay = delayNum.getText().equals("") ? 500 : Integer.parseInt(delayNum.getText());

        HBox resetBox = (HBox) advancedVb.getChildren().get(2);
        TextField resetNum = (TextField) resetBox.getChildren().get(1);
        resetTime = resetNum.getText().equals("") ? 72000 : Integer.parseInt(resetNum.getText());

        ComboBox<OutputFormat> outputFormatBox = (ComboBox<OutputFormat>) advancedVb.getChildren().get(3);
        outputFormat = outputFormatBox.getValue();

        CheckBox useMyIP = (CheckBox) advancedVb.getChildren().get(4);
        useNullProxy = useMyIP.isSelected();

        CheckBox debugMode = (CheckBox) advancedVb.getChildren().get(5);
        debug = debugMode.isSelected();

        CheckBox usePrivateDomain = (CheckBox) advancedVb.getChildren().get(6);
        privateDomain = usePrivateDomain.isSelected();
    }

    public static void loadSettings(){

        boolean outdated = false;

        if(!settingsFile.exists()){
            System.out.println("no settings file exists");
            return;
        }

        try {
            Scanner in = new Scanner(settingsFile);

            while(in.hasNext()){
                String line = in.nextLine();

                String argName = line.substring(0,line.indexOf(":"));
                String value = line.substring(line.indexOf(":") + 1);

                if(!outdated && argName.equals("version") && Double.valueOf(value) != VERSION){
                    Log("Config file out of date, updating");

                    outdated = true;
                }

                switch (argName){
                    case "plusMail":
                        plusMail = value.equals("null") ? "" : value;
                        break;
                    case "userName":
                        userName = value.equals("null") ? "" : value;
                        break;
                    case "password":
                        password = value.equals("null") ? "" : value;
                        break;
                    case "startNum":
                        startNum = value.equals("null") ? null : Integer.parseInt(value);
                        break;
                    case "count":
                        count = value.equals("null") ? 1 : Integer.parseInt(value);
                        break;
                    case "captchaKey":
                        captchaKey = value.equals("null") ? "" : value;
                        break;
                    case "autoVerify":
                        autoVerify = Boolean.parseBoolean(value);
                        break;
                    case "avMail":
                        avMail = value.equals("null") ? "" : value;
                        break;
                    case "avPass":
                        avPass = value.equals("null") ? "" : value;
                        break;
                    case "acceptTos":
                        acceptTos = Boolean.parseBoolean(value);
                        break;
                    case "outputFile":
                        outputFile = value.equals("null") ? null : new File(value);
                        break;
                    case "proxyFile":
                        proxyFile = value.equals("null") ? null : new File(value);
                        break;
                    case "debug":
                        debug = Boolean.parseBoolean(value);
                        break;
                    case "threads":
                        threads = value.equals("null") ? 5 : Integer.parseInt(value);
                        break;
                    case "delay":
                        delay = value.equals("null") ? 500 : Integer.parseInt(value);
                        break;
                    case "resetTime":
                        resetTime = value.equals("null") ? 78000 : Integer.parseInt(value);
                        break;
                    case "outputFormat":
                        outputFormat = OutputFormat.valueOf(value);
                        break;
                    case "useNullProxy":
                        useNullProxy = Boolean.parseBoolean(value);
                        break;
                    case "privateDomain":
                        privateDomain = Boolean.parseBoolean(value);
                        break;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(outdated) saveSettings();
    }

    private static void saveSettings(){

        if(!settingsFile.exists()){
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(settingsFile))) {
            bw.write("version:"+VERSION);
            bw.newLine();
            bw.write("plusMail:"+plusMail);
            bw.newLine();
            bw.write("userName:"+userName);
            bw.newLine();
            bw.write("password:"+password);
            bw.newLine();
            bw.write("startNum:"+startNum);
            bw.newLine();
            bw.write("count:"+count);
            bw.newLine();
            bw.write("captchaKey:"+captchaKey);
            bw.newLine();
            bw.write("autoVerify:"+autoVerify);
            bw.newLine();
            bw.write("avMail:"+avMail);
            bw.newLine();
            bw.write("avPass:"+avPass);
            bw.newLine();
            bw.write("acceptTos:"+acceptTos);
            bw.newLine();
            bw.write("outputFile:"+outputFile);
            bw.newLine();
            bw.write("proxyFile:"+proxyFile);
            bw.newLine();
            bw.write("debug:"+debug);
            bw.newLine();
            bw.write("threads:"+threads);
            bw.newLine();
            bw.write("delay:"+delay);
            bw.newLine();
            bw.write("resetTime:"+resetTime);
            bw.newLine();
            bw.write("outputFormat:"+outputFormat);
            bw.newLine();
            bw.write("useNullProxy:"+useNullProxy);
            bw.newLine();
            bw.write("privateDomain:"+privateDomain);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String millisToTime(long millis){
        return String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    @Override
    public void run() {
        Start();
    }

    public enum OutputFormat{
        RocketMap,
        PokeAlert,
        Standard
    }
}
