import sys
import time
import string
import random
import datetime
import urllib2

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import StaleElementReferenceException, TimeoutException
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
# from pikaptcha.jibber import *
# from pikaptcha.ptcexceptions import *
# from pikaptcha.url import *

user_agent = (
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36")

BASE_URL = "https://club.pokemon.com/us/pokemon-trainer-club"

# endpoints taken from PTCAccount
SUCCESS_URLS = (
    'https://club.pokemon.com/us/pokemon-trainer-club/parents/email',
    # This initially seemed to be the proper success redirect
    'https://club.pokemon.com/us/pokemon-trainer-club/sign-up/',
    # but experimentally it now seems to return to the sign-up, but still registers
)

# As both seem to work, we'll check against both success destinations until I have I better idea for how to check success
DUPE_EMAIL_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/forgot-password?msg=users.email.exists'
BAD_DATA_URL = 'https://club.pokemon.com/us/pokemon-trainer-club/parents/sign-up'

logfile = "pallettown.log"

def log(name,str):
    prnt = "[" + name + "]: " + str
    print(prnt)
    file = open(logfile,"a")
    file.write(prnt + "\n")
    file.close

__all__ = [
    'PTCException',
    'PTCInvalidStatusCodeException',
    'PTCInvalidNameException',
    'PTCInvalidEmailException',
    'PTCInvalidPasswordException',
    'PTCInvalidBirthdayException',
    'PTCTwocaptchaException'
]

class PTCException(Exception):
    """Base exception for all PTC Account exceptions"""
    pass


class PTCInvalidStatusCodeException(Exception):
    """Base exception for all PTC Account exceptions"""
    pass


class PTCInvalidNameException(PTCException):
    """Username already in use"""
    pass


class PTCInvalidEmailException(PTCException):
    """Email invalid or already in use"""
    pass


class PTCInvalidPasswordException(PTCException):
    """Password invalid"""
    pass


class PTCInvalidBirthdayException(PTCException):
    """Birthday invalid"""
    pass

class PTCTwocaptchaException(PTCException):
    """2captcha unable to provide service"""
    pass

def openurl(address):
    try:
        urlresponse = urllib2.urlopen(address).read()
        return urlresponse        
    except urllib2.HTTPError, e:
        print("HTTPError = " + str(e.code))
    except urllib2.URLError, e:
        print("URLError = " + str(e.args))
    except Exception:
        import traceback
        print("Generic Exception: " + traceback.format_exc())
    print("Request to " + address + "failed.")    
    return "Failed"

def activateurl(address):
    try:
        urlresponse = urllib2.urlopen(address)
        return urlresponse
    except urllib2.HTTPError, e:
        print("HTTPError = " + str(e.code))
    except urllib2.URLError, e:
        print("URLError = " + str(e.args))
    except Exception:
        import traceback
        print("Generic Exception: " + traceback.format_exc())
    print("Request to " + address + "failed.")    
    return "Failed"

def _validate_response(driver):
    url = driver.current_url
    if url in SUCCESS_URLS:
        return True
    elif url == DUPE_EMAIL_URL:
        log (threadname,"Email already in use")
        raise PTCInvalidEmailException("Email already in use.")
    elif url == BAD_DATA_URL:
        if "Enter a valid email address." in driver.page_source:
            log (threadname,"Invalid Email used")
            raise PTCInvalidEmailException("Invalid email.")
        else:
            log (threadname,"Username already in use")
            raise PTCInvalidNameException("Username already in use.")
    else:
        log (threadname,"Some other error returned by Niantic")
        raise PTCException("Generic failure. User was not created.")

def create_account(username, password, email, birthday, captchakey2, threadname, proxy, captchatimeout):

    log(threadname," initializing..")
    log(threadname,"Attempting to create user {user}:{pw}. Opening browser...".format(user=username, pw=password))
    
    if(captchakey2 == ""):
        captchakey2 = None

    if captchakey2 != None:
        log(threadname,"2captcha key")
        dcap = dict(DesiredCapabilities.PHANTOMJS)
        dcap["phantomjs.page.settings.userAgent"] = user_agent

        print(proxy)
        if proxy != "":
            serv_args = [
                '--proxy=https://' + proxy,
                '--proxy-type=https',
            ]
            driver = webdriver.PhantomJS(desired_capabilities=dcap,service_args=serv_args)
            #driver.get("http://whatismyip.org/")
            #log(threadname,"proxy: " + proxy)
            #log(threadname, driver.current_url)
            #elem = driver.find_element_by_tag_name("span")
            #log(threadname, "span text: " + elem.text)
            #if(elem.text == "180.200.145.4"):
            #    return True
            # return True
        else:
            driver = webdriver.PhantomJS(desired_capabilities=dcap)
        # driver = webdriver.Chrome()
    else:
        log(threadname,"No 2captcha key")

        chrome_options = webdriver.ChromeOptions()
        chrome_options.add_argument('--proxy-server=' + proxy)

        driver = webdriver.Chrome(chrome_options=chrome_options)
        driver.set_window_size(600, 600)
        #testing
        driver.get("http://whatismyipaddress.com")
        log(threadname,"proxy: " + proxy)
        return True

    # Input age: 1992-01-08
    log(threadname,"Step 1: Verifying age using birthday: {}".format(birthday))
    try:
        driver.get("{}/sign-up/".format(BASE_URL))
        log(threadname,"Driver current url: " + driver.current_url)
    except Exception as e:
        log(threadname, "unkown Error verifying age, terminating")
        driver.quit()
        return False

    if driver.current_url != "{}/sign-up/".format(BASE_URL):
        log(threadname,"Driver url wrong, exiting...")
        driver.quit()
        return False
        
    elem = driver.find_element_by_name("dob")

    log(threadname,"trying to execute workaround script")
    # Workaround for different region not having the same input type
    driver.execute_script(
        "var input = document.createElement('input'); input.type='text'; input.setAttribute('name', 'dob'); arguments[0].parentNode.replaceChild(input, arguments[0])",
        elem)
    log(threadname,"done executing workaround script, submitting dob")

    elem = driver.find_element_by_name("dob")
    elem.send_keys(birthday)
    elem.submit()

    log(threadname,"dob submitted")
    # Todo: ensure valid birthday

    # Create account page
    log(threadname,"Step 2: Entering account details")
    assert driver.current_url == "{}/parents/sign-up".format(BASE_URL)

    user = driver.find_element_by_name("username")
    user.clear()
    user.send_keys(username)

    elem = driver.find_element_by_name("password")
    elem.clear()
    elem.send_keys(password)

    elem = driver.find_element_by_name("confirm_password")
    elem.clear()
    elem.send_keys(password)

    elem = driver.find_element_by_name("email")
    elem.clear()
    elem.send_keys(email)

    elem = driver.find_element_by_name("confirm_email")
    elem.clear()
    elem.send_keys(email)

    driver.find_element_by_id("id_public_profile_opt_in_1").click()
    driver.find_element_by_name("terms").click()

    if captchakey2 == None:
        # Do manual captcha entry
        log(threadname,"You did not pass a 2captcha key. Please solve the captcha manually.")
        elem = driver.find_element_by_class_name("g-recaptcha")
        driver.execute_script("arguments[0].scrollIntoView(true);", elem)
        # Waits 1 minute for you to input captcha
        try:
            WebDriverWait(driver, 60).until(
                EC.text_to_be_present_in_element_value((By.NAME, "g-recaptcha-response"), ""))
            log(threadname,"Waiting on captcha")
            log(threadname,"Captcha successful. Sleeping for 1 second...")
            time.sleep(1)
        except TimeoutException, err:
            log(threadname,"Timed out while manually solving captcha")
            driver.quit()
            return False
    else:
        # Now to automatically handle captcha
        log(threadname,"Starting autosolve recaptcha")
        html_source = driver.page_source

        gkey_index = html_source.find("https://www.google.com/recaptcha/api2/anchor?k=") + 47
        gkey = html_source[gkey_index:gkey_index + 40]
        recaptcharesponse = "Failed"
        url="http://club.pokemon.com"
        while (recaptcharesponse == "Failed"):
            recaptcharesponse = openurl("http://2captcha.com/in.php?key=" + captchakey2 + "&method=userrecaptcha&googlekey=" + gkey)
            # "http://2captcha.com/in.php?key={}&method=userrecaptcha&googlekey={}&pageurl={}".format(captchakey2,gkey,url)
        captchaid = recaptcharesponse[3:]
        recaptcharesponse = "CAPCHA_NOT_READY"
        elem = driver.find_element_by_class_name("g-recaptcha")
        log(threadname,"We will wait 10 seconds for captcha to be solved by 2captcha")
        start_time = int(time.time())
        timedout = False
        while recaptcharesponse == "CAPCHA_NOT_READY":
            time.sleep(10)
            elapsedtime = int(time.time()) - start_time
            if elapsedtime > captchatimeout:
                log(threadname,"Captcha timeout reached. Exiting.")
                driver.quit()
                timedout = True
                return True
            log (threadname,"Captcha still not solved, waiting another 10 seconds.")
            recaptcharesponse = "Failed"
            while (recaptcharesponse == "Failed"):
                recaptcharesponse = openurl(
                    "http://2captcha.com/res.php?key=" + captchakey2 + "&action=get&id=" + captchaid)
        if timedout == False:
            solvedcaptcha = recaptcharesponse[3:]
            captchalen = len(solvedcaptcha)
            elem = driver.find_element_by_name("g-recaptcha-response")
            elem = driver.execute_script("arguments[0].style.display = 'block'; return arguments[0];", elem)
            elem.send_keys(solvedcaptcha)
            log (threadname,"Solved captcha")
    try:
        log (threadname,"trying to submit")
        user.submit()
        log (threadname,"submitted")
    except StaleElementReferenceException:
        log(threadname,"Error StaleElementReferenceException!")
        driver.quit()
        return False

    try:
        log (threadname,"trying to validate response")
        _validate_response(driver)
        log (threadname,"validated response")
    except:
        log(threadname,"Failed to create user: {}".format(username) + "exiting...")
        driver.quit()
        raise PTCInvalidNameException("failed to create user")
        log(threadname, "threw failed to create user exception, terminate")
        return False

    driver.quit()
    log(threadname,"Closed driver")
    log(threadname,"Account successfully created.\n \n")
    return True

create_account(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],sys.argv[6],sys.argv[7],180)