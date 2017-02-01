package pallettown;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Paris on 20/01/2017.
 */
public class GUI extends Application{

    private static final int VIEWER_WIDTH = 500;
    private static final int VIEWER_HEIGHT = 500;

    private Group mainRoot = new Group();
    static Group controls = new Group();
    private Stage primaryStage;
    
    static Group advancedRoot = new Group();
    static Group advancedControls = new Group();
    private Stage advancedStage = null;
    private Scene advancedScene;
    private TextArea textArea;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PalletTown");
        Scene scene = new Scene(mainRoot);
        primaryStage.setResizable(false);

        // load the image
        Image background = new Image("pallet-town.jpg");

        // simple displays ImageView the image as is
        ImageView viewBG = new ImageView();
        viewBG.setImage(background);

        mainRoot.getChildren().add(controls);

        makeControls();

        makeAdvancedControls();

        primaryStage.setScene(scene);
        primaryStage.show();

        viewBG.setFitHeight(scene.getHeight());
        viewBG.setFitWidth(scene.getWidth());

        mainRoot.getChildren().add(0,viewBG);
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
                    "import platform\n" +
                    "\n" +
                    "from selenium import webdriver\n" +
                    "from selenium.webdriver.support.ui import WebDriverWait\n" +
                    "from selenium.webdriver.support import expected_conditions as EC\n" +
                    "from selenium.webdriver.common.by import By\n" +
                    "from selenium.common.exceptions import StaleElementReferenceException, TimeoutException\n" +
                    "from selenium.webdriver.common.desired_capabilities import DesiredCapabilities\n" +
                    "# from pikaptcha.jibber import *\n" +
                    "# from pikaptcha.ptcexceptions import *\n" +
                    "# from pikaptcha.url import *\n" +
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
                    "    'https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up'\n" +
                    "    # but experimentally it now seems to return to the sign-up, but still registers\n" +
                    ")\n" +
                    "\n" +
                    "# As both seem to work, we'll check against both success destinations until I have I better idea for how to check success\n" +
                    "DUPE_EMAIL_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/forgot-password?msg=users.email.exists'\n" +
                    "BAD_DATA_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up'\n" +
                    "RATE_LIMIT_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/sign-up/?rate_limit_exceeded=True'\n" +
                    "\n" +
                    "logfile = \"pallettown.log\"\n" +
                    "\n" +
                    "def log(name,str):\n" +
                    "    prnt = \"[\" + name + \"]: \" + str\n" +
                    "    print(prnt)\n" +
                    "    file = open(logfile,\"a\")\n" +
                    "    file.write(prnt + \"\\n\")\n" +
                    "    file.close\n" +
                    "\n" +
                    "__all__ = [\n" +
                    "    'PTCException',\n" +
                    "    'PTCInvalidStatusCodeException',\n" +
                    "    'PTCInvalidNameException',\n" +
                    "    'PTCInvalidEmailException',\n" +
                    "    'PTCInvalidPasswordException',\n" +
                    "    'PTCInvalidBirthdayException',\n" +
                    "    'PTCRateLimitExceededException',\n" +
                    "    'PTCTwocaptchaException'\n" +
                    "]\n" +
                    "\n" +
                    "class PTCException(Exception):\n" +
                    "    \"\"\"Base exception for all PTC Account exceptions\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "\n" +
                    "class PTCInvalidStatusCodeException(Exception):\n" +
                    "    \"\"\"Base exception for all PTC Account exceptions\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "\n" +
                    "class PTCInvalidNameException(PTCException):\n" +
                    "    \"\"\"Username already in use\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "\n" +
                    "class PTCInvalidEmailException(PTCException):\n" +
                    "    \"\"\"Email invalid or already in use\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "\n" +
                    "class PTCInvalidPasswordException(PTCException):\n" +
                    "    \"\"\"Password invalid\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "\n" +
                    "class PTCInvalidBirthdayException(PTCException):\n" +
                    "    \"\"\"Birthday invalid\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "class PTCRateLimitExceededException(PTCException):\n" +
                    "    \"\"\"5 accounts per IP per 10 minutes limit exceeded\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "class PTCTwocaptchaException(PTCException):\n" +
                    "    \"\"\"2captcha unable to provide service\"\"\"\n" +
                    "    pass\n" +
                    "\n" +
                    "def openurl(address):\n" +
                    "    try:\n" +
                    "        urlresponse = urllib2.urlopen(address).read()\n" +
                    "        return urlresponse        \n" +
                    "    except urllib2.HTTPError, e:\n" +
                    "        print(\"HTTPError = \" + str(e.code))\n" +
                    "    except urllib2.URLError, e:\n" +
                    "        print(\"URLError = \" + str(e.args))\n" +
                    "    except Exception:\n" +
                    "        import traceback\n" +
                    "        print(\"Generic Exception: \" + traceback.format_exc())\n" +
                    "    print(\"Request to \" + address + \"failed.\")    \n" +
                    "    return \"Failed\"\n" +
                    "\n" +
                    "def activateurl(address):\n" +
                    "    try:\n" +
                    "        urlresponse = urllib2.urlopen(address)\n" +
                    "        return urlresponse\n" +
                    "    except urllib2.HTTPError, e:\n" +
                    "        print(\"HTTPError = \" + str(e.code))\n" +
                    "    except urllib2.URLError, e:\n" +
                    "        print(\"URLError = \" + str(e.args))\n" +
                    "    except Exception:\n" +
                    "        import traceback\n" +
                    "        print(\"Generic Exception: \" + traceback.format_exc())\n" +
                    "    print(\"Request to \" + address + \"failed.\")    \n" +
                    "    return \"Failed\"\n" +
                    "\n" +
                    "def _validate_response(driver):\n" +
                    "    url = driver.current_url\n" +
                    "    log(\"RESPONSE_VALIDATOR\",url)\n" +
                    "    if url in SUCCESS_URLS:\n" +
                    "        return True\n" +
                    "    elif url == DUPE_EMAIL_URL:\n" +
                    "        log (\"RESPONSE_VALIDATOR\",\"Email already in use\")\n" +
                    "        raise PTCInvalidEmailException(\"Email already in use.\")\n" +
                    "    elif url == BAD_DATA_URL:\n" +
                    "        if \"Enter a valid email address.\" in driver.page_source:\n" +
                    "            log (\"RESPONSE_VALIDATOR\",\"Invalid Email used\")\n" +
                    "            raise PTCInvalidEmailException(\"Invalid email.\")\n" +
                    "        else:\n" +
                    "            log (\"RESPONSE_VALIDATOR\",\"Username already in use\")\n" +
                    "            raise PTCInvalidNameException(\"Username already in use.\")\n" +
                    "    elif url == RATE_LIMIT_URL:\n" +
                    "        log(\"RESPONSE_VALIDATOR\",\"Account creation IP limit exceeded\")\n" +
                    "        raise PTCRateLimitExceededException(\"Account creation IP limit exceeded\")\n" +
                    "    else:\n" +
                    "        log (\"RESPONSE_VALIDATOR\",\"Some other error returned by Niantic\")\n" +
                    "        raise PTCException(\"Generic failure. User was not created.\")\n" +
                    "\n" +
                    "def create_account(username, password, email, birthday, captchakey2, threadname, proxy, auth, captchatimeout):\n" +
                    "\n" +
                    "    log(threadname,\" initializing..\")\n" +
                    "    log(threadname,\"Attempting to create user {user}:{pw}. Opening browser...\".format(user=username, pw=password))\n" +
                    "    \n" +
                    "    if(captchakey2 == \"null\"):\n" +
                    "        captchakey2 = None\n" +
                    "    if(proxy == \"null\"):\n" +
                    "        proxy = None\n" +
                    "\n" +
                    "    if captchakey2 != None:\n" +
                    "        log(threadname,\"2captcha key\")\n" +
                    "        dcap = dict(DesiredCapabilities.PHANTOMJS)\n" +
                    "        dcap[\"phantomjs.page.settings.userAgent\"] = user_agent\n" +
                    "\n" +
                    "        print(proxy)\n" +
                    "        if proxy != None:\n" +
                    "            if(auth == 'IP'):\n" +
                    "                serv_args = [\n" +
                    "                    '--proxy=https://' + proxy,\n" +
                    "                    '--proxy-type=https',\n" +
                    "                ]\n" +
                    "            else:\n" +
                    "                serv_args = [\n" +
                    "                    '--proxy=https://' + proxy,\n" +
                    "                    '--proxy-type=https',\n" +
                    "                    '--proxy-auth=' + auth\n" +
                    "                ]\n" +
                    "            driver = webdriver.PhantomJS(desired_capabilities=dcap,service_args=serv_args)\n" +
                    "            #driver.get(\"http://whatismyip.org/\")\n" +
                    "            #log(threadname,\"proxy: \" + proxy)\n" +
                    "            #log(threadname, driver.current_url)\n" +
                    "            #elem = driver.find_element_by_tag_name(\"span\")\n" +
                    "            #log(threadname, \"span text: \" + elem.text)\n" +
                    "            #if(elem.text == \"180.200.145.4\"):\n" +
                    "            #    return True\n" +
                    "            # return True\n" +
                    "        else:\n" +
                    "            driver = webdriver.PhantomJS(desired_capabilities=dcap)\n" +
                    "        # driver = webdriver.Chrome()\n" +
                    "    else:\n" +
                    "        log(threadname,\"No 2captcha key\")\n" +
                    "\n" +
                    "        if(platform.system() == \"Windows\" or platform.system() == \"Darwin\"):\n" +
                    "            if(proxy != None):\n" +
                    "                chrome_options = webdriver.ChromeOptions()\n" +
                    "                chrome_options.add_argument('--proxy-server=%s' % proxy)\n" +
                    "                driver = webdriver.Chrome(chrome_options=chrome_options)\n" +
                    "            else:\n" +
                    "                driver = webdriver.Chrome()\n" +
                    "        else:\n" +
                    "            driver = webdriver.Firefox()\n" +
                    "\n" +
                    "    driver.set_window_size(600, 600)\n" +
                    "\n" +
                    "    try:\n" +
                    "        # Input age: 1992-01-08\n" +
                    "        print(\"Step 1: Verifying age using birthday: {}\".format(birthday))\n" +
                    "        driver.get(\"{}/sign-up/\".format(BASE_URL))\n" +
                    "        assert driver.current_url == \"{}/sign-up/\".format(BASE_URL)\n" +
                    "        elem = driver.find_element_by_name(\"dob\")\n" +
                    "\n" +
                    "    except Exception as e:\n" +
                    "        log(threadname, \"unknown Error verifying age, terminating\")\n" +
                    "        driver.close()\n" +
                    "        driver.quit()\n" +
                    "        return False\n" +
                    "\n" +
                    "    if driver.current_url != \"{}/sign-up/\".format(BASE_URL):\n" +
                    "        log(threadname,\"Driver url wrong, exiting...\")\n" +
                    "        driver.close()\n" +
                    "        driver.quit()\n" +
                    "        return False\n" +
                    "        \n" +
                    "    elem = driver.find_element_by_name(\"dob\")\n" +
                    "\n" +
                    "    log(threadname,\"trying to execute workaround script\")\n" +
                    "    # Workaround for different region not having the same input type\n" +
                    "    driver.execute_script(\n" +
                    "        \"var input = document.createElement('input'); input.type='text'; input.setAttribute('name', 'dob'); arguments[0].parentNode.replaceChild(input, arguments[0])\",\n" +
                    "        elem)\n" +
                    "    log(threadname,\"done executing workaround script, submitting dob\")\n" +
                    "\n" +
                    "    elem = driver.find_element_by_name(\"dob\")\n" +
                    "    elem.send_keys(birthday)\n" +
                    "    elem.submit()\n" +
                    "\n" +
                    "    log(threadname,\"dob submitted\")\n" +
                    "    # Todo: ensure valid birthday\n" +
                    "\n" +
                    "    # Create account page\n" +
                    "    log(threadname,\"Step 2: Entering account details\")\n" +
                    "    #assert driver.current_url == \"{}/parents/sign-up\".format(BASE_URL)\n" +
                    "    log(threadname,\"{}/parents/sign-up\".format(BASE_URL))\n" +
                    "    log(threadname,driver.current_url)\n" +
                    "\n" +
                    "    driver.implicitly_wait(10)\n" +
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
                    "        log(threadname,\"You did not pass a 2captcha key. Please solve the captcha manually.\")\n" +
                    "        elem = driver.find_element_by_class_name(\"g-recaptcha\")\n" +
                    "        driver.execute_script(\"arguments[0].scrollIntoView(true);\", elem)\n" +
                    "        # Waits 1 minute for you to input captcha\n" +
                    "        try:\n" +
                    "            WebDriverWait(driver, 60).until(\n" +
                    "                EC.text_to_be_present_in_element_value((By.NAME, \"g-recaptcha-response\"), \"\"))\n" +
                    "            log(threadname,\"Waiting on captcha\")\n" +
                    "            log(threadname,\"Captcha successful. Sleeping for 1 second...\")\n" +
                    "            time.sleep(1)\n" +
                    "        except TimeoutException, err:\n" +
                    "            log(threadname,\"Timed out while manually solving captcha\")\n" +
                    "            driver.close()\n" +
                    "            driver.quit()\n" +
                    "            return False\n" +
                    "    else:\n" +
                    "        # Now to automatically handle captcha\n" +
                    "        log(threadname,\"Starting autosolve recaptcha\")\n" +
                    "        html_source = driver.page_source\n" +
                    "\n" +
                    "        gkey_index = html_source.find(\"https://www.google.com/recaptcha/api2/anchor?k=\") + 47\n" +
                    "        gkey = html_source[gkey_index:gkey_index + 40]\n" +
                    "        recaptcharesponse = \"Failed\"\n" +
                    "        url=\"http://club.pokemon.com\"\n" +
                    "        while (recaptcharesponse == \"Failed\"):\n" +
                    "            recaptcharesponse = openurl(\"http://2captcha.com/in.php?key=\" + captchakey2 + \"&method=userrecaptcha&googlekey=\" + gkey)\n" +
                    "            # \"http://2captcha.com/in.php?key={}&method=userrecaptcha&googlekey={}&pageurl={}\".format(captchakey2,gkey,url)\n" +
                    "        captchaid = recaptcharesponse[3:]\n" +
                    "        recaptcharesponse = \"CAPCHA_NOT_READY\"\n" +
                    "        elem = driver.find_element_by_class_name(\"g-recaptcha\")\n" +
                    "        log(threadname,\"We will wait 10 seconds for captcha to be solved by 2captcha\")\n" +
                    "        start_time = int(time.time())\n" +
                    "        timedout = False\n" +
                    "        while recaptcharesponse == \"CAPCHA_NOT_READY\":\n" +
                    "            time.sleep(10)\n" +
                    "            elapsedtime = int(time.time()) - start_time\n" +
                    "            if elapsedtime > captchatimeout:\n" +
                    "                log(threadname,\"Captcha timeout reached. Exiting.\")\n" +
                    "                driver.close()\n" +
                    "                driver.quit()\n" +
                    "                timedout = True\n" +
                    "                return True\n" +
                    "            log (threadname,\"Captcha still not solved, waiting another 10 seconds.\")\n" +
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
                    "            log (threadname,\"Solved captcha\")\n" +
                    "    try:\n" +
                    "        log (threadname,\"trying to submit\")\n" +
                    "        user.submit()\n" +
                    "        log (threadname,\"submitted\")\n" +
                    "    except StaleElementReferenceException:\n" +
                    "        log(threadname,\"Error StaleElementReferenceException!\")\n" +
                    "        driver.close()\n" +
                    "        driver.quit()\n" +
                    "        return False\n" +
                    "\n" +
                    "    try:\n" +
                    "        log (threadname,\"trying to validate response\")\n" +
                    "        _validate_response(driver)\n" +
                    "        log (threadname,\"validated response\")\n" +
                    "    except PTCException:\n" +
                    "        log(threadname,\"Failed to create user: {}\".format(username) + \"exiting...\")\n" +
                    "        driver.close()\n" +
                    "        driver.quit()\n" +
                    "        log(threadname, \"threw failed to create user exception, terminate\")\n" +
                    "        return False\n" +
                    "\n" +
                    "    driver.close()\n" +
                    "    driver.quit()\n" +
                    "    log(threadname,\"Closed driver\")\n" +
                    "    log(threadname,\"Account \" + username + \":\" + password + \" successfully created.\\n \\n\")\n" +
                    "    return True\n" +
                    "\n" +
                    "create_account(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],sys.argv[6],sys.argv[7],sys.argv[8],180)";

            BufferedWriter out = new BufferedWriter(new FileWriter("accountcreate.py"));
            out.write(prg);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeControls() {

        VBox mainVb = new VBox();
        mainVb.setSpacing(10);
        mainVb.setPadding(new Insets(15,15,15,15));
        mainVb.setLayoutX(10);
        mainVb.setLayoutY(10);
        mainVb.setAlignment(Pos.CENTER);
        mainVb.setBackground(new Background(new BackgroundFill(Color.rgb(140,140,140,.5), CornerRadii.EMPTY, Insets.EMPTY)));

        Label plusMaillabel = new Label("Email:");

        final TextField plusMail = new TextField();
        plusMail.setPromptText("Enter plusmail compatible email (no gmail)");
        plusMail.setPrefWidth(350);

        HBox mail = new HBox();
        mail.setAlignment(Pos.CENTER_RIGHT);
        mail.getChildren().addAll(plusMaillabel, plusMail);
        mail.setSpacing(10);

        mainVb.getChildren().add(mail);

        Label userLabel = new Label("Username:");

        final TextField userName = new TextField();
        userName.setPromptText("Enter account username");
        userName.setPrefWidth(350);
        HBox user = new HBox();
        user.setAlignment(Pos.CENTER_RIGHT);
        user.getChildren().addAll(userLabel, userName);
        user.setSpacing(10);

        mainVb.getChildren().add(user);

        Label passLabel = new Label("Password:");

        final TextField password = new TextField();
        password.setPrefWidth(350);
        password.setPromptText("Enter account password to use");
        HBox pass = new HBox();
        pass.setAlignment(Pos.CENTER_RIGHT);
        pass.getChildren().addAll(passLabel,password);
        pass.setSpacing(10);

        mainVb.getChildren().add(pass);

        Label numLabel = new Label("Number of accounts:");
        final TextField numAccounts = new TextField();
        numAccounts.setPrefWidth(350);
        numAccounts.setPromptText("Number of accounts to create.");
        numAccounts.setText("1");
        HBox num = new HBox();
        num.setAlignment(Pos.CENTER_RIGHT);
        num.getChildren().addAll(numLabel,numAccounts);
        num.setSpacing(10);

        mainVb.getChildren().add(num);

        Label startLabel = new Label("Start number:");
        final TextField startNum = new TextField();
        startNum.setPrefWidth(350);
        startNum.setPromptText("Starting number");
        HBox start = new HBox();
        start.setAlignment(Pos.CENTER_RIGHT);
        start.getChildren().addAll(startLabel,startNum);
        start.setSpacing(10);

        mainVb.getChildren().add(start);

        Label captchaLabel = new Label("2Captcha Key:");
        final TextField captchaKey = new TextField();
        captchaKey.setPrefWidth(350);
        captchaKey.setPromptText("Enter 2Captcha Key");
        HBox captcha = new HBox();
        captcha.setAlignment(Pos.CENTER_RIGHT);
        captcha.getChildren().addAll(captchaLabel,captchaKey);
        captcha.setSpacing(10);

        mainVb.getChildren().add(captcha);

        CheckBox autoVerify = new CheckBox("Auto Verify Accounts");
        mainVb.getChildren().add(autoVerify);

        Label gmailLabel = new Label("Gmail Account:");

        final TextField gmail = new TextField();
        gmail.setPromptText("Gmail account for auto verification");
        gmail.setPrefWidth(350);

        HBox gm = new HBox();
        gm.setAlignment(Pos.CENTER_RIGHT);
        gm.getChildren().addAll(gmailLabel, gmail);
        gm.setSpacing(10);

        mainVb.getChildren().add(gm);

        Label gmPassLabel = new Label("Gmail Password:");

        final TextField gmailPass = new TextField();
        gmailPass.setPromptText("Gmail account password for auto verification");
        gmailPass.setPrefWidth(350);

        HBox gmPw = new HBox();
        gmPw.setAlignment(Pos.CENTER_RIGHT);
        gmPw.getChildren().addAll(gmPassLabel, gmailPass);
        gmPw.setSpacing(10);

        mainVb.getChildren().add(gmPw);

        CheckBox acceptTos = new CheckBox("Accept account TOS");
        acceptTos.setDisable(true);
        mainVb.getChildren().add(acceptTos);

        ArrayList<String> extensions = new ArrayList<>();
        extensions.add("*.txt");
        extensions.add("*.csv");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text File", extensions)
        );
        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

        Label outputLabel = new Label("Output File:");

        TextField outputFile = new TextField();
        outputFile.setPrefWidth(350);

        Button clearOutput = new Button("Clear");
        clearOutput.setOnAction(event -> {
            outputFile.clear();
        });

        HBox output = new HBox();
        output.setAlignment(Pos.CENTER_RIGHT);
        output.getChildren().addAll(outputLabel, outputFile,clearOutput);
        output.setSpacing(10);

        mainVb.getChildren().add(output);

        File[] file = new File[1];

        outputFile.setOnMouseClicked(event -> {
            file[0] = fileChooser.showOpenDialog(primaryStage);
            if (file[0] != null) {
                outputFile.setText(file[0].getAbsolutePath());
            }
        });

        Label proxyLabel = new Label("Proxy File");

        TextField proxyFile = new TextField();
        proxyFile.setPrefWidth(350);

        Button clearProxy = new Button("Clear");
        clearProxy.setOnAction(event -> {
            proxyFile.clear();
        });

        HBox proxy = new HBox();
        proxy.setAlignment(Pos.CENTER_RIGHT);
        proxy.getChildren().addAll(proxyLabel, proxyFile,clearProxy);
        proxy.setSpacing(10);

        mainVb.getChildren().add(proxy);

        File[] pFile = new File[1];

        proxyFile.setOnMouseClicked(event -> {
            fileChooser.setTitle("Select proxy file");
            fileChooser.getExtensionFilters().removeAll();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text File","*.txt")
            );
            fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(1));
            pFile[0] = fileChooser.showOpenDialog(primaryStage);
            if (pFile[0] != null) {
                proxyFile.setText(pFile[0].getAbsolutePath());
            }
        });

        Button advanced = new Button("Advanced Settings");
        advanced.setOnAction(event -> showAdvanced());
        mainVb.getChildren().add(advanced);

        Button submit = new Button("Create accounts");
        submit.setOnAction(event -> PalletTown.Start());
        mainVb.getChildren().add(submit);

        controls.getChildren().add(mainVb);
    }

    private void showAdvanced() {
        //If the helpScene hasnt been created yet, create it
        if (advancedScene == null) {

//            makeAdvancedControls();

            advancedScene = new Scene(advancedRoot);

            advancedStage = new Stage();
            advancedStage.setTitle("Advanced Settings");
            advancedStage.setScene(advancedScene);
        }

        advancedStage.show();
    }

    private void makeAdvancedControls() {

        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setPadding(new Insets(15,15,15,15));
        vb.setLayoutX(10);
        vb.setLayoutY(10);
        vb.setAlignment(Pos.CENTER);

        advancedRoot.getChildren().addAll(vb);

        Label threadsLabel = new Label("Threads:");

        final TextField threads = new TextField("5");
        threads.setPrefWidth(50);

        HBox thrds = new HBox();
        thrds.setAlignment(Pos.CENTER_RIGHT);
        thrds.getChildren().addAll(threadsLabel,threads);
        thrds.setSpacing(10);

        vb.getChildren().add(thrds);

        Label delayLabel = new Label("Delay between accounts (ms):");

        final TextField delay = new TextField("500");
        delay.setPrefWidth(80);

        HBox del = new HBox();
        del.setAlignment(Pos.CENTER_RIGHT);
        del.getChildren().addAll(delayLabel,delay);
        del.setSpacing(10);

        vb.getChildren().add(del);

        CheckBox rocketMap = new CheckBox("RocketMap output formatting");
        rocketMap.setSelected(true);
        vb.getChildren().add(rocketMap);

        CheckBox useMyIP = new CheckBox("Use my IP as well as proxies");
        useMyIP.setSelected(true);
        vb.getChildren().add(useMyIP);

        CheckBox debug = new CheckBox("Debug Mode");
        vb.getChildren().add(debug);

//        textArea = new TextArea();
//        textArea.setEditable(false);

//        redirectSystemStreams();
//        vb.getChildren().add(textArea);
    }

    private void updateTextArea(final String text) {
        Platform.runLater(() -> textArea.appendText(text));
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));

        System.out.println("test");
    }

}
