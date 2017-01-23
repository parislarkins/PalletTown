package pallettown;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Paris on 20/01/2017.
 */
public class PalletTown {

    public static String plusMail;
    public static String userName;
    public static String password;
    public static Integer startNum;
    public static int count;
    public static String captchaKey;
    public static boolean autoVerify;
    public static String gmail;
    public static String gmailPass;
    public static boolean acceptTos;
    public static File outputFile;
    public static boolean debug;

    public static void Start(){
        parseArgs();

        AccountCreator.success = 0;

        if(!captchaKey.equals("")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("2Captcha Balance");
            alert.setHeaderText(null);

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(yes, no);
            alert.setContentText("Your 2Captcha balance is: " + checkBalance() + ".\n" +
                                  "This run will cost approximately: " + (double)Math.round((count * 0.0009) * 1000d) / 1000d +
                                  ".\nDo you wish to proceed?");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == no) {
                    System.out.println("cancel");
                }
            });
        }

        String verify = verifySettings();

        if(!verify.equals("valid")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText(verify);
            alert.show();
            System.out.println("aborting...");
            return;
        }

        if(autoVerify && outputFile != null){
//            outputAppend("\nThe following accounts use the email address: " + plusMail + "\n");
        }

        System.out.println("Starting");

        AccountCreator.createAccounts(userName,password,plusMail,captchaKey);

        System.out.println(AccountCreator.success + "/" + count + " successes");

        if(AccountCreator.success == 0)
            return;

        if(autoVerify && !gmail.equals("") && !gmailPass.equals("")){
            System.out.println("Account Creation done");
            System.out.println("Waiting 4 minutes for forwarded emails to arrive");

            try {
                Thread.sleep(24000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Verifying accounts...");
            GmailVerifier.verify(gmail,gmailPass,AccountCreator.success);
            System.out.println("Done!");
        }

    }

    static void outputAppend(String s) {
        // append to end of file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile,true))) {
            bw.write(s);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String verifySettings() {

        if(plusMail.equals("") || !plusMail.contains("@") || plusMail.contains("gmail.com"))
            return "Check email is correct and not a gmail address and try again";

        if(userName.equals(""))
            return "Please enter a username";

        if(!validatePass(password))
            return "Invalid password.\nPassword must contain a symbol, number, and capital letter";

        if(count < 1)
            return "Please set count to at least 1";

        if((startNum == null || startNum == 0) && count > 1)
            return "To create more than 1 account, specify a start number";

        if(autoVerify && (gmail.equals("") || gmailPass.equals("") || !gmail.contains("@gmail.com")))
            return "Check gmail account/password are correct";

        return "valid";
    }

    private static boolean validatePass(String password) {
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!^~`&*(){}@#$%]).{6,20})");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
}

    //Checks 2captcha balance
    public static float checkBalance(){
        String balance = "Failed";

        while (balance.equals("Failed")){
            balance = UrlUtil.openUrl("http://2captcha.com/res.php?key=" + captchaKey + "&action=getbalance", true);
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
        userName = uname.getText();

        HBox pass = (HBox) vb.getChildren().get(2);
        TextField pw = (TextField) pass.getChildren().get(1);
        password = pw.getText();

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

        HBox gm = (HBox) vb.getChildren().get(7);
        TextField gma = (TextField) gm.getChildren().get(1);
        gmail = gma.getText();

        HBox gp = (HBox) vb.getChildren().get(8);
        TextField gmpw = (TextField) gp.getChildren().get(1);
        gmailPass = gmpw.getText();

        CheckBox tos = (CheckBox) vb.getChildren().get(9);
        acceptTos = tos.isSelected();

        HBox output = (HBox) vb.getChildren().get(10);
        TextField path = (TextField) output.getChildren().get(1);
        outputFile = path.getText().equals("") ? null : new File(path.getText());

        CheckBox debugMode = (CheckBox) vb.getChildren().get(12);
        debug = debugMode.isSelected();

    }

}
