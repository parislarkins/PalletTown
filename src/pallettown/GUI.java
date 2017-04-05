package pallettown;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static pallettown.PalletTown.*;

/**
 * Created by Paris on 20/01/2017.
 */
public class GUI extends Application{

    private static final ObservableList<AccountThread> accountThreads = FXCollections.observableArrayList();

    private final Group mainRoot = new Group();
    static final Group controls = new Group();
    private Stage primaryStage;
    
    static final Group advancedRoot = new Group();
    private Stage advancedStage = null;
    private Scene advancedScene;
    private static TableView table;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private static Label statusLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PalletTown v"+VERSION);
        Scene scene = new Scene(mainRoot);
        primaryStage.setResizable(false);

        // load the image
        Image background = new Image("pallet-town.jpg");

        // simple displays ImageView the image as is
        ImageView viewBG = new ImageView();
        viewBG.setImage(background);

        mainRoot.getChildren().add(controls);

        loadSettings();

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
                    "from cgi import escape\n" +
                    "user_agent = (\n" +
                    "    \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) \" + \"AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36\")\n" +
                    "\n" +
                    "BASE_URL = \"https://club.pokemon.com/us/pokemon-trainer-club\"\n" +
                    "\n" +
                    "# endpoints taken from PTCAccount\n" +
                    "SUCCESS_URLS = (\n" +
                    "    'https://club.pokemon.com/us/pokemon-trainer-club/parents/email'\n" +
                    "    # This initially seemed to be the proper success redirect\n" +
                    "    #'https://club.pokemon.com/us/pokemon-trainer-club/sign-up/',\n" +
                    "    #'https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up'\n" +
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
                    "    proxyType = ''\n" +
                    "\n" +
                    "    if(proxy.startswith('https')):\n" +
                    "        proxyType = 'https'\n" +
                    "        proxy = proxy[8:]\n" +
                    "    elif(proxy.startswith('http')):\n" +
                    "        proxyType = 'http'\n" +
                    "        proxy = proxy[7:]\n" +
                    "    elif(proxy.startswith('socks5')):\n" +
                    "        proxyType = 'socks5'\n" +
                    "        proxy = proxy[9:]\n" +
                    "    elif(proxy.startswith('socks4')):\n" +
                    "        proxy = proxy[9:]\n" +
                    "        proxyType = 'socks4'\n" +
                    "\n" +
                    "    log(threadname,\"Proxy type: \" + proxyType)\n" +
                    "\n" +
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
                    "                    '--proxy=' + proxy,\n" +
                    "                    '--proxy-type=' + proxyType,\n" +
                    "                ]\n" +
                    "            else:\n" +
                    "                serv_args = [\n" +
                    "                    '--proxy=' + proxy,\n" +
                    "                    '--proxy-type=' + proxyType,\n" +
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
                    "        log(threadname, driver.current_url)\n" +
                    "        driver.close()\n" +
                    "        driver.quit()\n" +
                    "        raise e\n" +
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
                    "#         # Now to automatically handle captcha\n" +
                    "        log(threadname,\"Starting autosolve recaptcha\")\n" +
                    "        html_source = driver.page_source\n" +
                    "\n" +
                    "        gkey_index = html_source.find(\"https://www.google.com/recaptcha/api2/anchor?k=\") + 47\n" +
                    "        gkey = html_source[gkey_index:gkey_index + 40]\n" +
                    "        recaptcharesponse = \"Failed\"\n" +
                    "        url=escape(driver.current_url)\n" +
                    "        while (recaptcharesponse == \"Failed\"):\n" +
                    "            recaptcharesponse = openurl(\"http://2captcha.com/in.php?key={}&method=userrecaptcha&googlekey={}&pageurl={}\".format(captchakey2,gkey,url))\n" +
                    "        captchaid = recaptcharesponse[3:]\n" +
                    "        recaptcharesponse = \"CAPCHA_NOT_READY\"\n" +
                    "        elem = driver.find_element_by_class_name(\"g-recaptcha\")\n" +
                    "\n" +
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
                    "    except PTCRateLimitExceededException:\n" +
                    "        log(threadname,\"Failed to create user: {}\".format(username) + \"exiting...\")\n" +
                    "        driver.close()\n" +
                    "        driver.quit()\n" +
                    "        log(threadname, \"IP rate limit exceeded, account failed.\")\n" +
                    "        return False\n" +
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
                    "create_account(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],sys.argv[6],sys.argv[7],sys.argv[8],300)";

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

        final TextField plusMailText = new TextField(plusMail == null ? "" : plusMail);
        plusMailText.setPromptText("Enter plusmail compatible email (no gmail)");
        plusMailText.setPrefWidth(350);

        HBox mail = new HBox();
        mail.setAlignment(Pos.CENTER_RIGHT);
        mail.getChildren().addAll(plusMaillabel, plusMailText);
        mail.setSpacing(10);

        mainVb.getChildren().add(mail);

        Label userLabel = new Label("Username:");

        final TextField userNameText = new TextField(userName == null ? "" : userName);
        userNameText.setPromptText("Enter account username");
        userNameText.setPrefWidth(350);
        HBox user = new HBox();
        user.setAlignment(Pos.CENTER_RIGHT);
        user.getChildren().addAll(userLabel, userNameText);
        user.setSpacing(10);

        mainVb.getChildren().add(user);

        Label passLabel = new Label("Password:");

        final TextField passwordText = new TextField(password == null ? "" : password);
        passwordText.setPrefWidth(350);
        passwordText.setPromptText("Enter account password to use");
        HBox pass = new HBox();
        pass.setAlignment(Pos.CENTER_RIGHT);
        pass.getChildren().addAll(passLabel,passwordText);
        pass.setSpacing(10);

        mainVb.getChildren().add(pass);

        Label numLabel = new Label("Number of accounts:");

        final TextField numAccounts = new TextField(Integer.toString(count));
        numAccounts.setPrefWidth(350);
        numAccounts.setPromptText("Number of accounts to create.");
        HBox num = new HBox();
        num.setAlignment(Pos.CENTER_RIGHT);
        num.getChildren().addAll(numLabel,numAccounts);
        num.setSpacing(10);

        mainVb.getChildren().add(num);

        Label startLabel = new Label("Start number:");

        final TextField startNumText;

        if(startNum != null)
            startNumText= new TextField(Integer.toString(startNum));
        else
            startNumText = new TextField();

        startNumText.setPrefWidth(350);
        startNumText.setPromptText("Starting number");
        HBox start = new HBox();
        start.setAlignment(Pos.CENTER_RIGHT);
        start.getChildren().addAll(startLabel,startNumText);
        start.setSpacing(10);

        mainVb.getChildren().add(start);

        Label captchaLabel = new Label("2Captcha Key:");
        final TextField captchaKeyText = new TextField(captchaKey);
        captchaKeyText.setPrefWidth(350);
        captchaKeyText.setPromptText("Enter 2Captcha Key");
        HBox captcha = new HBox();
        captcha.setAlignment(Pos.CENTER_RIGHT);
        captcha.getChildren().addAll(captchaLabel,captchaKeyText);
        captcha.setSpacing(10);

        mainVb.getChildren().add(captcha);

        CheckBox autoVerifyBox = new CheckBox("Auto Verify Accounts");
        autoVerifyBox.setSelected(autoVerify);
        mainVb.getChildren().add(autoVerifyBox);

        Label avMailLabel = new Label("Email account for auto verification:");

        final TextField avMailText = new TextField(avMail);
        avMailText.setPromptText("Email account for auto verification");
        avMailText.setPrefWidth(350);
        avMailText.setOnKeyTyped(event -> autoVerifyBox.setSelected(true));

        HBox av = new HBox();
        av.setAlignment(Pos.CENTER_RIGHT);
        av.getChildren().addAll(avMailLabel, avMailText);
        av.setSpacing(10);

        mainVb.getChildren().add(av);

        Label avPassLabel = new Label("Email password for auto verification:");

        final TextField avPassText = new TextField(avPass);
        avPassText.setPromptText("Email account password for auto verification");
        avPassText.setPrefWidth(350);
        avPassText.setOnKeyTyped(event -> autoVerifyBox.setSelected(true));

        HBox avPw = new HBox();
        avPw.setAlignment(Pos.CENTER_RIGHT);
        avPw.getChildren().addAll(avPassLabel, avPassText);
        avPw.setSpacing(10);

        mainVb.getChildren().add(avPw);

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

        TextField outputFileText;

        if(outputFile != null)
            outputFileText = new TextField(outputFile.getAbsolutePath());
        else
            outputFileText = new TextField();

        outputFileText.setPrefWidth(350);

        Button clearOutput = new Button("Clear");
        clearOutput.setOnAction(event -> outputFileText.clear());

        HBox output = new HBox();
        output.setAlignment(Pos.CENTER_RIGHT);
        output.getChildren().addAll(outputLabel, outputFileText,clearOutput);
        output.setSpacing(10);

        mainVb.getChildren().add(output);

        File[] file = new File[1];

        outputFileText.setOnMouseClicked(event -> {
            file[0] = fileChooser.showOpenDialog(primaryStage);
            if (file[0] != null) {
                outputFileText.setText(file[0].getAbsolutePath());
            }
        });

        Label proxyLabel = new Label("Proxy File");

        TextField proxyFileText;

        if(proxyFile != null)
            proxyFileText = new TextField(proxyFile.getAbsolutePath());
        else
            proxyFileText = new TextField();

        proxyFileText.setPrefWidth(350);

        Button clearProxy = new Button("Clear");
        clearProxy.setOnAction(event -> proxyFileText.clear());

        HBox proxy = new HBox();
        proxy.setAlignment(Pos.CENTER_RIGHT);
        proxy.getChildren().addAll(proxyLabel, proxyFileText,clearProxy);
        proxy.setSpacing(10);

        mainVb.getChildren().add(proxy);

        File[] pFile = new File[1];

        proxyFileText.setOnMouseClicked(event -> {
            fileChooser.setTitle("Select proxy file");
            fileChooser.getExtensionFilters().removeAll();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text File","*.txt")
            );
            fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(1));

            pFile[0] = fileChooser.showOpenDialog(primaryStage);
            if (pFile[0] != null) {
                proxyFileText.setText(pFile[0].getAbsolutePath());
            }

            PalletTown.changedProxies = true;
        });

        Button advanced = new Button("Advanced Settings");
        advanced.setOnAction(event -> showAdvanced());
        mainVb.getChildren().add(advanced);

        Button submit = new Button("Create accounts");
        submit.setOnAction(event -> {

            if((captchaKeyText.getText() != null) && !captchaKeyText.getText().isEmpty()){
                table.setVisible(true);
            }

            accountThreads.clear();
            table.refresh();

            PalletTown palletTown = new PalletTown();

            Thread thread = new Thread(palletTown, "Main Thread");
            thread.start();
//            PalletTown.Start();
        });
        mainVb.getChildren().add(submit);

        statusLabel = new Label("Status: Waiting to start");

        mainVb.getChildren().add(statusLabel);

        table = new TableView();
        table.setEditable(false);
        table.setMaxWidth(500);

        table.setMaxHeight(200);

        TableColumn threadNameCol = new TableColumn("Thread");
        threadNameCol.setCellValueFactory(
            new PropertyValueFactory<AccountThread,String>("threadName")
        );
        threadNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        threadNameCol.setResizable(false);

        TableColumn successCol = new TableColumn("Success");
        successCol.setCellValueFactory(
                new PropertyValueFactory<AccountThread,Integer>("successes")
        );
        successCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        threadNameCol.setResizable(false);

        TableColumn failureCol = new TableColumn("Fail");
        failureCol.setCellValueFactory(
                new PropertyValueFactory<AccountThread,Integer>("failures")
        );
        failureCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        failureCol.setResizable(false);

        TableColumn timeCol = new TableColumn("Time");
        timeCol.setCellValueFactory(
                new PropertyValueFactory<AccountThread,String>("latestTime")
        );
        timeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        timeCol.setResizable(false);

        TableColumn messageCol = new TableColumn("Message");
        messageCol.setCellValueFactory(
                new PropertyValueFactory<AccountThread,String>("latestMessage")
        );
        messageCol.prefWidthProperty().bind(table.widthProperty().multiply(0.343));
        messageCol.setResizable(false);


        table.setItems(accountThreads);
        table.getColumns().addAll(threadNameCol, successCol, failureCol,timeCol, messageCol);
        table.setVisible(false);

        mainVb.getChildren().add(table);
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

            advancedStage.show();
            advancedStage.setX(primaryStage.getX() + primaryStage.getWidth() + 5);
            advancedStage.setY(primaryStage.getY());
        }

        if(advancedStage.isShowing()){
            advancedStage.requestFocus();
        }else{
            advancedStage.show();
            advancedStage.setX(primaryStage.getX() + primaryStage.getWidth() + 5);
            advancedStage.setY(primaryStage.getY());
        }

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

        final TextField threadsText = new TextField(Integer.toString(threads));
        threadsText.setPrefWidth(50);

        HBox thrds = new HBox();
        thrds.setAlignment(Pos.CENTER);
        thrds.getChildren().addAll(threadsLabel,threadsText);
        thrds.setSpacing(10);

        vb.getChildren().add(thrds);

        Label delayLabel = new Label("Delay between accounts (ms):");

        final TextField delayText = new TextField(Integer.toString(delay));
        delayText.setPrefWidth(50);

        HBox del = new HBox();
        del.setAlignment(Pos.CENTER);
        del.getChildren().addAll(delayLabel,delayText);
        del.setSpacing(10);

        vb.getChildren().add(del);

        Label resetLabel = new Label("Proxy reset time (minutes):");

        final TextField resetText = new TextField(Integer.toString(resetTime));
        resetText.setPrefWidth(50);

        HBox reset = new HBox();
        reset.setAlignment(Pos.CENTER);
        reset.getChildren().addAll(resetLabel,resetText);
        reset.setSpacing(10);

        vb.getChildren().add(reset);

        Label trashLabel = new Label("Gmail trash folder name: ");

        final TextField trashNameField = new TextField(trashName);

        trashNameField.setPrefWidth(65);

        HBox trash = new HBox();
        trash.setAlignment(Pos.CENTER);
        trash.getChildren().addAll(trashLabel,trashNameField);
        trash.setSpacing(10);

        vb.getChildren().add(trash);

//        CheckBox outputFormat = new CheckBox("RocketMap output formatting");
//        outputFormat.setSelected();
        ComboBox<OutputFormat> outputFormatComboBox = new ComboBox<>(FXCollections.observableArrayList(OutputFormat.values()));
        outputFormatComboBox.setValue(PalletTown.outputFormat);
        vb.getChildren().add(outputFormatComboBox);

        CheckBox useMyIP = new CheckBox("Use my IP as well as proxies");
        useMyIP.setSelected(useNullProxy);
        vb.getChildren().add(useMyIP);

        CheckBox debugBox = new CheckBox("Debug Mode");
        debugBox.setSelected(debug);
        vb.getChildren().add(debugBox);

        CheckBox usePrivateDomain = new CheckBox("Use private email domain (username@domain.com)");
        usePrivateDomain.setSelected(privateDomain);
        vb.getChildren().addAll(usePrivateDomain);



        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(500);
        textArea.setPrefWidth(600);

        Console console = new Console(textArea);
        PrintStream ps = new PrintStream(console,true);
        System.setOut(ps);
        System.setErr(ps);

        vb.getChildren().add(textArea);
    }

    public static void setStatus(String status){
        Platform.runLater(() -> statusLabel.setText("Status: " + status));
    }

    synchronized
    public static void addThread(AccountThread accountThread) {
        accountThreads.add(accountThread);
    }

    public static void showAlert(Alert.AlertType warning, String title, String headerText, String verify) {
        Alert alert = new Alert(warning);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(verify);
        alert.show();
    }

    private static String currentTime(){
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());
    }

    synchronized
    public static void Log(String s){
        System.out.println("[" + currentTime() + "] " + s);
    }

    public static class Console extends OutputStream {

        private final TextArea output;

        public Console(TextArea ta) {
            this.output = ta;
        }

        @Override
        public void write(int i) throws IOException {
            Platform.runLater(() -> output.appendText(String.valueOf((char) i)));
        }
    }

    public static class AccountThread {

        private final SimpleStringProperty threadName;
        private SimpleStringProperty latestTime;
        private SimpleStringProperty latestMessage;
        private final SimpleIntegerProperty successes = new SimpleIntegerProperty(0);
        private final SimpleIntegerProperty failures = new SimpleIntegerProperty(0);
        private final ArrayList<Pair<String, String>> messages = new ArrayList<>();

        public AccountThread(String name){
            threadName = new SimpleStringProperty(name);
        }

        public void LogMessage(String message){
            String time = currentTime();
            messages.add(new Pair<>(time, message));

            latestTime = new SimpleStringProperty(time);
            latestMessage = new SimpleStringProperty(message);

            table.refresh();
        }

        public void Success(){
            successes.setValue(successes.get()+1);
            table.refresh();
        }

        public void Failure(){
            failures.setValue(failures.get()+1);
            table.refresh();
        }

        public Pair<String,String> LatestMessage(){
            return messages.get(messages.size()-1);
        }

        public String getThreadName(){
            return threadName.get();
        }

        public String getLatestTime(){
            return latestTime.get();
        }

        public String getLatestMessage(){
            return latestMessage.get();
        }

        public Integer getFailuresProperty(){ return failures.get();}

        public Integer getSuccessesProperty() { return successes.get();}

        public StringProperty threadNameProperty(){
            return threadName;
        }

        public StringProperty latestMessageProperty(){
            return latestMessage;
        }

        public StringProperty latestTimeProperty(){
            return latestTime;
        }

        public IntegerProperty successesProperty(){ return successes;}

        public IntegerProperty failuresProperty(){return failures;}
    }

}
