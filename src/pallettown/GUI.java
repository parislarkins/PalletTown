package pallettown;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paris on 20/01/2017.
 */
public class GUI extends Application{

    private static final int VIEWER_WIDTH = 500;
    private static final int VIEWER_HEIGHT = 500;

    private Group root = new Group();

    static Group controls = new Group();
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PalletTown");
        Scene scene = new Scene(root);

        // load the image
        Image background = new Image("pallet-town.jpg");

        // simple displays ImageView the image as is
        ImageView viewBG = new ImageView();
        viewBG.setImage(background);

        root.getChildren().add(viewBG);

        root.getChildren().add(controls);

        makeControls();


        primaryStage.setScene(scene);
        primaryStage.show();

        viewBG.setFitHeight(scene.getHeight());

        makePython();
    }

    private void makePython() {

        try {
            String prg = "import sys\n" +
                "import time\n" +
                "import string\n" +
                "import random\n" +
                "import datetime\n" +
                "import urllib2\n" +
                "\n" +
                "from selenium import webdriver\n" +
                "from selenium.webdriver.support.ui import WebDriverWait\n" +
                "from selenium.webdriver.support import expected_conditions as EC\n" +
                "from selenium.webdriver.common.by import By\n" +
                "from selenium.common.exceptions import StaleElementReferenceException, TimeoutException\n" +
                "from selenium.webdriver.common.desired_capabilities import DesiredCapabilities\n" +
                "from pikaptcha.jibber import *\n" +
                "from pikaptcha.ptcexceptions import *\n" +
                "from pikaptcha.url import *\n" +
                "\n" +
                "user_agent = (\n" +
                "    \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) \" + \"AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36\")\n" +
                "\n" +
                "BASE_URL = \"https://club.pokemon.com/us/pokemon-trainer-club\"\n" +
                "\n" +
                "# endpoints taken from PTCAccount\n" +
                "SUCCESS_URLS = (\n" +
                "    'https://club.pokemon.com/us/pokemon-trainer-club/parents/email',\n" +
                "    # This initially seemed to be the proper success redirect\n" +
                "    'https://club.pokemon.com/us/pokemon-trainer-club/sign-up/',\n" +
                "    # but experimentally it now seems to return to the sign-up, but still registers\n" +
                ")\n" +
                "\n" +
                "# As both seem to work, we'll check against both success destinations until I have I better idea for how to check success\n" +
                "DUPE_EMAIL_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/forgot-password?msg=users.email.exists'\n" +
                "BAD_DATA_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up'\n" +
                "\n" +
                "def create_account(username, password, email, birthday, captchakey2, captchatimeout):\n" +
                "\n" +
                "    print(\"Attempting to create user {user}:{pw}. Opening browser...\".format(user=username, pw=password))\n" +
                "   \n" +
                "    if(captchakey2 == \"\"):\n" +
                "        captchakey2 = None\n" +
                "\n" +
                "    if captchakey2 != None:\n" +
                "        print(\"2captcha key\")\n" +
                "        dcap = dict(DesiredCapabilities.PHANTOMJS)\n" +
                "        dcap[\"phantomjs.page.settings.userAgent\"] = user_agent\n" +
                "        driver = webdriver.PhantomJS(desired_capabilities=dcap)\n" +
                "        # driver = webdriver.Chrome()\n" +
                "    else:\n" +
                "        print(\"No 2captcha key\")\n" +
                "        driver = webdriver.Chrome()\n" +
                "        driver.set_window_size(600, 600)\n" +
                "\n" +
                "    # Input age: 1992-01-08\n" +
                "    print(\"Step 1: Verifying age using birthday: {}\".format(birthday))\n" +
                "    driver.get(\"{}/sign-up/\".format(BASE_URL))\n" +
                "    assert driver.current_url == \"{}/sign-up/\".format(BASE_URL)\n" +
                "    elem = driver.find_element_by_name(\"dob\")\n" +
                "\n" +
                "    # Workaround for different region not having the same input type\n" +
                "    driver.execute_script(\n" +
                "        \"var input = document.createElement('input'); input.type='text'; input.setAttribute('name', 'dob'); arguments[0].parentNode.replaceChild(input, arguments[0])\",\n" +
                "        elem)\n" +
                "\n" +
                "    elem = driver.find_element_by_name(\"dob\")\n" +
                "    elem.send_keys(birthday)\n" +
                "    elem.submit()\n" +
                "    # Todo: ensure valid birthday\n" +
                "\n" +
                "    # Create account page\n" +
                "    print(\"Step 2: Entering account details\")\n" +
                "    assert driver.current_url == \"{}/parents/sign-up\".format(BASE_URL)\n" +
                "\n" +
                "    user = driver.find_element_by_name(\"username\")\n" +
                "    user.clear()\n" +
                "    user.send_keys(username)\n" +
                "\n" +
                "    elem = driver.find_element_by_name(\"password\")\n" +
                "    elem.clear()\n" +
                "    elem.send_keys(password)\n" +
                "\n" +
                "    elem = driver.find_element_by_name(\"confirm_password\")\n" +
                "    elem.clear()\n" +
                "    elem.send_keys(password)\n" +
                "\n" +
                "    elem = driver.find_element_by_name(\"email\")\n" +
                "    elem.clear()\n" +
                "    elem.send_keys(email)\n" +
                "\n" +
                "    elem = driver.find_element_by_name(\"confirm_email\")\n" +
                "    elem.clear()\n" +
                "    elem.send_keys(email)\n" +
                "\n" +
                "    driver.find_element_by_id(\"id_public_profile_opt_in_1\").click()\n" +
                "    driver.find_element_by_name(\"terms\").click()\n" +
                "\n" +
                "    if captchakey2 == None:\n" +
                "        # Do manual captcha entry\n" +
                "        print(\"You did not pass a 2captcha key. Please solve the captcha manually.\")\n" +
                "        elem = driver.find_element_by_class_name(\"g-recaptcha\")\n" +
                "        driver.execute_script(\"arguments[0].scrollIntoView(true);\", elem)\n" +
                "        # Waits 1 minute for you to input captcha\n" +
                "        try:\n" +
                "            WebDriverWait(driver, 60).until(\n" +
                "                EC.text_to_be_present_in_element_value((By.NAME, \"g-recaptcha-response\"), \"\"))\n" +
                "            print(\"Waiting on captcha\")\n" +
                "            print(\"Captcha successful. Sleeping for 1 second...\")\n" +
                "            time.sleep(1)\n" +
                "        except TimeoutException, err:\n" +
                "            print(\"Timed out while manually solving captcha\")\n" +
                "            return False\n" +
                "    else:\n" +
                "        # Now to automatically handle captcha\n" +
                "        print(\"Starting autosolve recaptcha\")\n" +
                "        html_source = driver.page_source\n" +
                "\n" +
                "        gkey_index = html_source.find(\"https://www.google.com/recaptcha/api2/anchor?k=\") + 47\n" +
                "        gkey = html_source[gkey_index:gkey_index + 40]\n" +
                "        recaptcharesponse = \"Failed\"\n" +
                "        url=\"http://club.pokemon.com\"\n" +
                "        while (recaptcharesponse == \"Failed\"):\n" +
                "            recaptcharesponse = openurl(\n" +
                "                \"http://2captcha.com/in.php?key=\" + captchakey2 + \"&method=userrecaptcha&googlekey=\" + gkey)\n" +
                "            \"http://2captcha.com/in.php?key={}&method=userrecaptcha&googlekey={}&pageurl={}\".format(captchakey2,gkey,url)\n" +
                "        captchaid = recaptcharesponse[3:]\n" +
                "        recaptcharesponse = \"CAPCHA_NOT_READY\"\n" +
                "        elem = driver.find_element_by_class_name(\"g-recaptcha\")\n" +
                "        print\"We will wait 10 seconds for captcha to be solved by 2captcha\"\n" +
                "        start_time = int(time.time())\n" +
                "        timedout = False\n" +
                "        while recaptcharesponse == \"CAPCHA_NOT_READY\":\n" +
                "            time.sleep(10)\n" +
                "            elapsedtime = int(time.time()) - start_time\n" +
                "            if elapsedtime > captchatimeout:\n" +
                "                print(\"Captcha timeout reached. Exiting.\")\n" +
                "                timedout = True\n" +
                "                break\n" +
                "            print \"Captcha still not solved, waiting another 10 seconds.\"\n" +
                "            recaptcharesponse = \"Failed\"\n" +
                "            while (recaptcharesponse == \"Failed\"):\n" +
                "                recaptcharesponse = openurl(\n" +
                "                    \"http://2captcha.com/res.php?key=\" + captchakey2 + \"&action=get&id=\" + captchaid)\n" +
                "        if timedout == False:\n" +
                "            solvedcaptcha = recaptcharesponse[3:]\n" +
                "            captchalen = len(solvedcaptcha)\n" +
                "            elem = driver.find_element_by_name(\"g-recaptcha-response\")\n" +
                "            elem = driver.execute_script(\"arguments[0].style.display = 'block'; return arguments[0];\", elem)\n" +
                "            elem.send_keys(solvedcaptcha)\n" +
                "            print \"Solved captcha\"\n" +
                "    try:\n" +
                "        user.submit()\n" +
                "    except StaleElementReferenceException:\n" +
                "        print(\"Error StaleElementReferenceException!\")\n" +
                "\n" +
                "    try:\n" +
                "        _validate_response(driver)\n" +
                "    except:\n" +
                "        print(\"Failed to create user: {}\".format(username))\n" +
                "        driver.quit()\n" +
                "        raise\n" +
                "\n" +
                "    print(\"Account successfully created.\")\n" +
                "    driver.quit()\n" +
                "    return True\n" +
                "\n" +
                "def _validate_response(driver):\n" +
                "    url = driver.current_url\n" +
                "    if url in SUCCESS_URLS:\n" +
                "        return True\n" +
                "    elif url == DUPE_EMAIL_URL:\n" +
                "        print (\"Email already in use\")\n" +
                "        raise PTCInvalidEmailException(\"Email already in use.\")\n" +
                "    elif url == BAD_DATA_URL:\n" +
                "        if \"Enter a valid email address.\" in driver.page_source:\n" +
                "            print (\"Invalid Email used\")\n" +
                "            raise PTCInvalidEmailException(\"Invalid email.\")\n" +
                "        else:\n" +
                "            print (\"Username already in use\")\n" +
                "            raise PTCInvalidNameException(\"Username already in use.\")\n" +
                "    else:\n" +
                "        print (\"Some other error returned by Niantic\")\n" +
                "        raise PTCException(\"Generic failure. User was not created.\")\n" +
                "\n" +
                "create_account(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],60)\n";
            BufferedWriter out = new BufferedWriter(new FileWriter("accountcreate.py"));
            out.write(prg);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeControls() {

        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setPadding(new Insets(15,15,15,15));
        vb.setAlignment(Pos.CENTER);

        Label plusMaillabel = new Label("Email:");

        final TextField plusMail = new TextField();
        plusMail.setPromptText("Enter plusmail compatible email (no gmail)");
        plusMail.setPrefWidth(350);

        HBox mail = new HBox();
        mail.setAlignment(Pos.CENTER_RIGHT);
        mail.getChildren().addAll(plusMaillabel, plusMail);
        mail.setSpacing(10);

        vb.getChildren().add(mail);

        Label userLabel = new Label("Username:");

        final TextField userName = new TextField();
        userName.setPromptText("Enter account username");
        userName.setPrefWidth(350);
        HBox user = new HBox();
        user.setAlignment(Pos.CENTER_RIGHT);
        user.getChildren().addAll(userLabel, userName);
        user.setSpacing(10);

        vb.getChildren().add(user);

        Label passLabel = new Label("Password:");

        final TextField password = new TextField();
        password.setPrefWidth(350);
        password.setPromptText("Enter account password to use");
        HBox pass = new HBox();
        pass.setAlignment(Pos.CENTER_RIGHT);
        pass.getChildren().addAll(passLabel,password);
        pass.setSpacing(10);

        vb.getChildren().add(pass);

        Label numLabel = new Label("Number of accounts:");
        final TextField numAccounts = new TextField();
        numAccounts.setPrefWidth(350);
        numAccounts.setPromptText("Number of accounts to create.");
        numAccounts.setText("1");
        HBox num = new HBox();
        num.setAlignment(Pos.CENTER_RIGHT);
        num.getChildren().addAll(numLabel,numAccounts);
        num.setSpacing(10);

        vb.getChildren().add(num);

        Label startLabel = new Label("Start number:");
        final TextField startNum = new TextField();
        startNum.setPrefWidth(350);
        startNum.setPromptText("Starting number");
        HBox start = new HBox();
        start.setAlignment(Pos.CENTER_RIGHT);
        start.getChildren().addAll(startLabel,startNum);
        start.setSpacing(10);

        vb.getChildren().add(start);

        Label captchaLabel = new Label("2Captcha Key:");
        final TextField captchaKey = new TextField();
        captchaKey.setPrefWidth(350);
        captchaKey.setPromptText("Enter 2Captcha Key");
        HBox captcha = new HBox();
        captcha.setAlignment(Pos.CENTER_RIGHT);
        captcha.getChildren().addAll(captchaLabel,captchaKey);
        captcha.setSpacing(10);

        vb.getChildren().add(captcha);

        CheckBox autoVerify = new CheckBox("Auto Verify Accounts");
        vb.getChildren().add(autoVerify);

        Label gmailLabel = new Label("Gmail Account:");

        final TextField gmail = new TextField();
        gmail.setPromptText("Gmail account for auto verification");
        gmail.setPrefWidth(350);

        HBox gm = new HBox();
        gm.setAlignment(Pos.CENTER_RIGHT);
        gm.getChildren().addAll(gmailLabel, gmail);
        gm.setSpacing(10);

        vb.getChildren().add(gm);

        Label gmPassLabel = new Label("Gmail Password:");

        final TextField gmailPass = new TextField();
        gmailPass.setPromptText("Gmail account password for auto verification");
        gmailPass.setPrefWidth(350);

        HBox gmPw = new HBox();
        gmPw.setAlignment(Pos.CENTER_RIGHT);
        gmPw.getChildren().addAll(gmPassLabel, gmailPass);
        gmPw.setSpacing(10);

        vb.getChildren().add(gmPw);

        CheckBox acceptTos = new CheckBox("Accept account TOS");
        vb.getChildren().add(acceptTos);

        ArrayList<String> extentions = new ArrayList<>();
        extentions.add("*.txt");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text File", extentions)
        );
        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

        Label outputLabel = new Label("Output File:");

        final TextField outputFile = new TextField();
        outputFile.setPrefWidth(350);

        HBox output = new HBox();
        output.setAlignment(Pos.CENTER_RIGHT);
        output.getChildren().addAll(outputLabel, outputFile);
        output.setSpacing(10);

        vb.getChildren().add(output);

        File[] file = new File[1];

        outputFile.setOnMouseClicked(event -> {
            file[0] = fileChooser.showOpenDialog(primaryStage);
            if (file[0] != null) {
                outputFile.setText(file[0].getAbsolutePath());
            }
        });

        Button submit = new Button("Create accounts");
        submit.setOnAction(event -> PalletTown.Start());
        vb.getChildren().add(submit);

        CheckBox debug = new CheckBox("Debug Mode");
        vb.getChildren().add(debug);

        controls.getChildren().add(vb);
    }
}
