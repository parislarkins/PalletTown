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
from pikaptcha.jibber import *
from pikaptcha.ptcexceptions import *
from pikaptcha.url import *

from __future__ import print_function

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

log = open('log.txt', 'w+')

def create_account(username, password, email, birthday, captchakey2, captchatimeout):

    print("Attempting to create user {user}:{pw}. Opening browser...".format(user=username, pw=password),file=log)
   
    if(captchakey2 == ""):
        captchakey2 = None

    if captchakey2 != None:
        print("2captcha key",file=log)
        dcap = dict(DesiredCapabilities.PHANTOMJS)
        dcap["phantomjs.page.settings.userAgent"] = user_agent
        driver = webdriver.PhantomJS(desired_capabilities=dcap)
        # driver = webdriver.Chrome()
    else:
        print("No 2captcha key",file=log)
        driver = webdriver.Chrome()
        driver.set_window_size(600, 600)

    # Input age: 1992-01-08
    print("Step 1: Verifying age using birthday: {}".format(birthday),file=log)
    driver.get("{}/sign-up/".format(BASE_URL))
    assert driver.current_url == "{}/sign-up/".format(BASE_URL)
    elem = driver.find_element_by_name("dob")

    # Workaround for different region not having the same input type
    driver.execute_script(
        "var input = document.createElement('input'); input.type='text'; input.setAttribute('name', 'dob'); arguments[0].parentNode.replaceChild(input, arguments[0])",
        elem)

    elem = driver.find_element_by_name("dob")
    elem.send_keys(birthday)
    elem.submit()
    # Todo: ensure valid birthday

    # Create account page
    print("Step 2: Entering account details",file=log)
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
        print("You did not pass a 2captcha key. Please solve the captcha manually.",file=log)
        elem = driver.find_element_by_class_name("g-recaptcha")
        driver.execute_script("arguments[0].scrollIntoView(true);", elem)
        # Waits 1 minute for you to input captcha
        try:
            WebDriverWait(driver, 60).until(
                EC.text_to_be_present_in_element_value((By.NAME, "g-recaptcha-response"), ""))
            print("Waiting on captcha",file=log)
            print("Captcha successful. Sleeping for 1 second...",file=log)
            time.sleep(1)
        except TimeoutException, err:
            print("Timed out while manually solving captcha",file=log)
            return False
    else:
        # Now to automatically handle captcha
        print("Starting autosolve recaptcha",file=log)
        html_source = driver.page_source

        gkey_index = html_source.find("https://www.google.com/recaptcha/api2/anchor?k=") + 47
        gkey = html_source[gkey_index:gkey_index + 40]
        recaptcharesponse = "Failed"
        url="http://club.pokemon.com"
        while (recaptcharesponse == "Failed"):
            recaptcharesponse = openurl(
                "http://2captcha.com/in.php?key=" + captchakey2 + "&method=userrecaptcha&googlekey=" + gkey)
            "http://2captcha.com/in.php?key={}&method=userrecaptcha&googlekey={}&pageurl={}".format(captchakey2,gkey,url)
        captchaid = recaptcharesponse[3:]
        recaptcharesponse = "CAPCHA_NOT_READY"
        elem = driver.find_element_by_class_name("g-recaptcha")
        print("We will wait 10 seconds for captcha to be solved by 2captcha",file=log)
        start_time = int(time.time())
        timedout = False
        while recaptcharesponse == "CAPCHA_NOT_READY":
            time.sleep(10)
            elapsedtime = int(time.time()) - start_time
            if elapsedtime > captchatimeout:
                print("Captcha timeout reached. Exiting.",file=log)
                timedout = True
                break
            print ("Captcha still not solved, waiting another 10 seconds.",file=log)
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
            print ("Solved captcha",file=log)
    try:
        user.submit()
    except StaleElementReferenceException:
        print("Error StaleElementReferenceException!",file=log)

    try:
        _validate_response(driver)
    except:
        print("Failed to create user: {}".format(username),file=log)
        driver.quit()
        raise

    print("Account successfully created.",file=log)
    driver.quit()
    return True

def _validate_response(driver):
    url = driver.current_url
    if url in SUCCESS_URLS:
        return True
    elif url == DUPE_EMAIL_URL:
        print ("Email already in use",file=log)
        raise PTCInvalidEmailException("Email already in use.")
    elif url == BAD_DATA_URL:
        if "Enter a valid email address." in driver.page_source:
            print ("Invalid Email used",file=log)
            raise PTCInvalidEmailException("Invalid email.")
        else:
            print ("Username already in use",file=log)
            raise PTCInvalidNameException("Username already in use.")
    else:
        print ("Some other error returned by Niantic",file=log)
        raise PTCException("Generic failure. User was not created.")

create_account(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],60)
