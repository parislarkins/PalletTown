# Pallet Town

Pallet Town is a Java tool for creating Pokemon Trainer Club accounts.

You can download the latest release [here](https://github.com/Pallet-Town/PalletTown/releases) (Just download the .jar file) 


1. [Windows Installation](#windows-install)
2. [Linux Installation](#linux-install)
3. [Mac Installation](#mac-install)

## Prerequisites

- [Java JRE](https://java.com/en/download/)
- [Google Chrome (Windows, Mac)](https://www.google.com.au/chrome/browser/)
- [Firefox (Linux)](https://www.mozilla.org/en-US/firefox/new/)
- [Python 2.7.12+](https://www.python.org/ftp/python/2.7.13/python-2.7.13.msi)
- [Selenium](http://selenium-python.readthedocs.io/installation.html)
- [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/downloads)
- [PhantomJS (For 2captcha creation)](http://selenium-python.readthedocs.io/installation.html)

## <a name="windows-install"></a>Windows

### Install prerequisites

#### Java JRE

Download and run the installer from https://java.com/en/download/.

#### Google Chrome

Download and run the installer from https://www.google.com.au/chrome/browser/.

#### Python

Download and run the installer from https://www.python.org/ftp/python/2.7.13/python-2.7.13.msi.

Make sure you select the "Add python.exe to path" option.

![Add python to path](add-python-path.png)

Click the start button and search for cmd.exe. Open it and paste this command:

`python --version`

If you get

`'python' is not recognized as an internal or external command, operable program, or batch file.`

You did not select the "Add python.exe to path" option. If you re-run the installer and select change installation, you can select it.

#### Selenium

Open cmd.exe and run:

`python -m pip install selenium`

#### ChromeDriver

Download the latest chromedriver.exe from https://sites.google.com/a/chromium.org/chromedriver/downloads.
Place chromedriver.exe in C:\Python27\Scripts (if you installed python to the default directory)

#### PhantomJS

Download the latest PhantomJS from http://phantomjs.org/download.html.

Extract the zip. Inside the phantomjs folder, you'll find phantomjs.exe in the bin folder. Move phantomjs.exe to the same place as chromedriver.exe (C:\Python27\Scripts)

### Running Pallet Town

There a two ways of running pallet town. 

Starting it from cmd.exe with:

`java -jar PalletTown.jar`

Or simply double clicking the .jar file in Windows Explorer.

Starting it from cmd.exe will give you a log of what's happening in the program to let you know it's still running or if anything goes wrong.
Currently without running it from cmd.exe the program will just freeze until it has completed account creation.

##<a name="linux-install"></a> Linux

### Install prerequisites

#### Java JRE

Install Java8 through terminal with

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

#### Firefox

Follow the instructions [here](https://support.mozilla.org/en-US/kb/install-firefox-linux) to install Firefox.

#### Python 2.7.12

Install python through terminal with

`sudo apt install python2.7`

Now install pip (A python package manager)

`sudo apt install python-pip`

#### Selenium

Install selenium through terminal with

`sudo python -m pip install selenium`

#### GeckoDriver

Download the latest geckodriver for your operating system from https://github.com/mozilla/geckodriver/releases.

Extract the tarball and place geckodriver in your path, such as /usr/bin.
(Your file names may differ from example)

```
tar -xvzf geckodriver-v0.13.0-linux32.tar.gz
sudo cp geckodriver /usr/bin/
```

#### PhantomJS

Download the latest PhantomJS for your operating system from http://phantomjs.org/download.html.

Extract the .tar.bz2 file and copy phantomjs/bin/phantomjs to /usr/bin.
(Your file names may differ from example)

```
tar -xvfj phantomjs-2.1.1-linux-i686.tar.bz2
sudo cp phantomjs-2.1.1-linux-i686/bin/phantomjs /usr/bin/
```
### Running Pallet Town

There a two ways of running pallet town. 

Starting it from terminal with:

`java -jar PalletTown.jar`

Or simply double clicking the .jar file in a file manager.

Starting it from terminal will give you a log of what's happening in the program to let you know it's still running or if anything goes wrong.
Currently without running it from terminal the program will just freeze until it has completed account creation.
