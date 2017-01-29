# Pallet Town

Pallet Town is a Java tool for creating Pokemon Trainer Club accounts.

## Prerequisites

- [Java JRE](https://java.com/en/download/)
- [Google Chrome](https://www.google.com.au/chrome/browser/)
- [Python 2.7.13](https://www.python.org/ftp/python/2.7.13/python-2.7.13.msi)
- [Selenium](http://selenium-python.readthedocs.io/installation.html)
- [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/downloads)
- [PhantomJS (For 2captcha creation)](http://selenium-python.readthedocs.io/installation.html)

## Windows 10

### Install prerequisites

#### Java JRE

Download and run the installer from https://java.com/en/download/.

#### Google Chrome

Download and run the installer from https://www.google.com.au/chrome/browser/.

#### Python 2.7.13

Download and run the installer from https://www.python.org/ftp/python/2.7.13/python-2.7.13.msi.

Make sure you select the "Add python.exe to path" option.

![Alt text](add-python-path.png)

Open CMD and run:

`python --version`

If you get

`'python' is not recognized as an internal or external command, operable program, or batch file.`

You did not select the "Add python.exe to path" option. If you re-run the installer and select change installation, you can select it.

#### Selenium

Open CMD and run:

`python -m pip install selenium`

#### ChromeDriver

Download the latest chromedriver.exe from https://sites.google.com/a/chromium.org/chromedriver/downloads.
Place chromedriver.exe in C:\Python27\Scripts (if you installed python to the default directory)

#### PhantomJS

Download the latest PhantomJS from http://phantomjs.org/download.html.

Open the zip. Inside the phantomjs folder, you'll find phantomjs.exe in the bin folder. Move phantomjs.exe to the same place as chromedriver.exe (C:\Python27\Scripts)

### Running Pallet Town

There a two ways of running pallet town. 

Starting it from CMD with:

`java -jar PalletTown.jar`

Or simple double clicking the .jar file in Windows Explorer.

Starting it from CMD will give you a log of what's happening in the program to let you know it's still running or if anything goes wrong.
Currently without running it from CMD the program will just freeze until it has completed account creation.