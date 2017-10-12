<h1 align="center">
  <br>
    <img width="175" src="https://github.com/PanagiotisDrakatos/T0rlib4Android/blob/master/Images/7b4.png">
  <br>
  T0rlib4Android Implementation
  <br>
</h1>

<h4 align="center">A minimal android controller library for Tor and instant messaging.</h4>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![](https://jitpack.io/v/PanagiotisDrakatos/T0rlib4Android.svg)](https://jitpack.io/#PanagiotisDrakatos/T0rlib4Android)
[![Build Status](https://travis-ci.org/PanagiotisDrakatos/T0rlib4Android.svg?branch=master)](https://travis-ci.org/PanagiotisDrakatos/T0rlib4Android)
[![GitHub Issues](https://img.shields.io/github/issues/PanagiotisDrakatos/T0rlib4Android.svg)](https://github.com/PanagiotisDrakatos/T0rlib4Android/issues)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/PanagiotisDrakatos/T0rlib4Android.svg)](https://travis-ci.org/PanagiotisDrakatos/T0rlib4Android/pull_requests)


# Basic Overview
T0rlib4Android is an android controller library for Tor. With it you can use Tor's control protocol to communicate against the Tor process, or build things such as arm. T0rlib4Android latest version is 2.0.1 (released October 12rd, 2017).

In addition, this library simulates and tends to look like a classic messaging app designed for activists, journalists, and anyone else who needs a safe, easy and robust way to communicate via tor network, without being monitored or blocked by their mobile internet service provider. To sum up the main goal is to protect users and their relationships from surveillance by having the ability to transparently torify all of the TCP traffic on your android app.


# Why Should i use it?
Well, to begin with, T0rlib4Android  have all you need to connect  to hiddenservices in order to communicate with 2 or more entities, all in one library. Tor makes it possible for users to hide their locations while offering various kinds of services, such as web publishing or an instant messaging server. Using Tor "rendezvous points," other Tor users can connect to these hidden services, each without knowing the other's network identity. This <a href="https://www.torproject.org/docs/tor-hidden-service.html.en">page</a> describes the technical details of how this rendezvous protocol works.

 Start using it right now, you can save more 100 hours of software  by writing code. if you  you need to use android  for your projects to connect to tor network this is the right place to start, for the follownig reasons.

 * It is really easy to use you. The only thing to do is to include the dependency and you are ready to write some sample code to get you started [How to Start](#Android).
 * The project will try to stay full updated with the latest tor patches <a href="https://www.torproject.org/download/download.html.en">tor-win32-0.3.1.7</a>.
 * It is free and open source and is intended to be used by any programmer who wants a messaging app via tor.

 
 # How do I use this library?
 
The only thing to do before you write your code is to add it in your root <b>build.gradle</b> at the end of repositories:

```gradle
allprojects {
      repositories {
		maven { url 'https://jitpack.io' }
		}
	}
```
Then go to dependencies in the module of your application and put the following dependency:
```gradle
dependencies {
        compile 'com.github.PanagiotisDrakatos:T0rlib4Android:v0.2-beta'
}
```

# Android

 If you want to run the library from the android studio emulator make sure that you will edit the AVD manager just like the printscreen below. An Android Virtual Device (AVD) is a configuration that defines the characteristics of an Android phone, tablet, Android Wear, or Android TV device that you want to simulate in the Android Emulator. The AVD Manager is an interface you can launch from Android Studio that helps you create and manage AVDs

<p align="center">
  <img width="600" height="351" src="https://github.com/PanagiotisDrakatos/T0rlib4Android/blob/master/Images/avd.PNG">
</p>

To open the AVD Manager, do one of the following:
 * Select <b>Tools > Android > AVD Manager</b>.
 * Click <b>AVD Manager</b> icon in the toolbar.
From the Your Virtual Devices page, you can perform the following operations on an existing AVD:
  * To edit an AVD, click <b>Edit</b>   and make your changes.
  * To delete an AVD, right-click an AVD and select <b>Delete</b>. Or click Menu   and select <b>Delete</b>.

This code represent the server side part.To be more specific
  * The HiddenServiceDir directive tells Tor where to look for the Hidden Service Directory containing the private key of the Hidden Service. Each Hidden Service Owns it's own directory. The directory needs to be created and having permissions of the Tor User.
 * The Hostname of your new Hidden Service will be available in the file hostname just created in your Hidden Service Directory
 * The tor directive tells in our Serversocket on which port to listen, to forward to which ip and to which port.
 
```java
import android.content.Context;


import net.sf.controller.network.AndroidTorRelay;
import net.sf.controller.network.TorServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

public class ServerSocketViaTor {
    private static final Logger LOG = LoggerFactory.getLogger(ServerSocketViaTor.class);
    private static final int hiddenservicedirport = 80;
    private static final int localport = 2096;
    private static CountDownLatch serverLatch = new CountDownLatch(2);
    private Context ctx;

    public ServerSocketViaTor(Context ctx) {
        this.ctx = ctx;
    }

    public void Init() throws IOException, InterruptedException, ClassNotFoundException, CloneNotSupportedException {
        if (ctx == null) {
            return;
        }
        String fileLocation = "torfiles";
        AndroidTorRelay node = new AndroidTorRelay(ctx, fileLocation);
        TorServerSocket torServerSocket = node.createHiddenService(localport, hiddenservicedirport);

        System.out.println("Hidden Service Binds to   " + torServerSocket.getHostname() + " ");
        System.out.println("Tor Service Listen to RemotePort  " + torServerSocket.getServicePort());
        System.out.println("Tor Service Listen to LocalPort  " + node.getSocksPort());
        ServerSocket ssocks = torServerSocket.getServerSocket();
        Server server = new Server(ssocks);
        new Thread(server).start();

        serverLatch.await();
    }

    private static class Server implements Runnable {
        private final ServerSocket socket;
        private int count = 0;
        private static final DateFormat df = new SimpleDateFormat("K:mm a, z");
        private String Date;

        private Server(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            System.out.println("Wating for incoming connections...");
            try {
                while (true) {

                    Socket sock = socket.accept();
                    this.Date =df.format(Calendar.getInstance().getTime());
                    System.out.println("Accepted Client " + (count++) + " at Address - " + sock.getRemoteSocketAddress()
                            + " on port " + sock.getLocalPort() + " at time " + this.Date);
                    ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                    System.out.println((String) in.readObject());
                    sock.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
```
## How do I connect to Tor?

Once you have Tor running and properly configured you have a few ways of connecting to it. The following is one of the most common method for getting a Controller instance, from the highest to lowest level which library supports.

  * <b>Socket Module:</b> You can skip the conveniences of a high level Controller and work directly with the raw components.  At T0rlib4j lowest level your connection with Tor is a socks4aSocketConnection-socks5aSocketConnection subclass and for the Server socket TorServerSocket subclass. This provides methods to send, receive, disconnect, and reconnect to Tor.
 
T0rlib4j offers two interfaces into the Tor network:
  * <b>SSOCKS 4A:</b> proxy 127.0.0.1:9050
  * <b>SSOCKS  5 :</b>  proxy 127.0.0.1:9050
   
Now for the sake of the completeness we will demonstrate how the Tor client behave. Here is some sample code to get you started.
  
   
```java
import android.content.Context;
import android.util.Log;

import net.sf.controller.network.AndroidTorRelay;
import net.sf.msopentech.thali.java.toronionproxy.Utilities;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TorClientSocks4 {
    private Context ctx = null;

    public TorClientSocks4() {
    }

    public TorClientSocks4(Context ctx) {
        
        this.ctx = ctx;
    }

    public void Init() throws IOException, InterruptedException {
        if(ctx==null){
            Log.e("TorTest", "Couldn't start Tor!");
            return;
        }
        String fileLocation = "torfiles";
        // Start the Tor Onion Proxy
        AndroidTorRelay node = new AndroidTorRelay(ctx,fileLocation);
        int hiddenServicePort = 80;
        int localPort = node.getSocksPort();
        String OnionAdress = "bla..bla.onion";
        String localhost="127.0.0.1";

        Socket clientSocket = Utilities.socks4aSocketConnection(OnionAdress, hiddenServicePort, "127.0.0.1", localPort);

        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();

        out.writeObject("i am workingg");
        out.flush();
    }
}
```

# Acknowledgements
A huge thanks to <a href="https://github.com/thaliproject/Tor_Onion_Proxy_Library"> thaliproject </a> an open source company. This project started by literally copying their code which handled things in Android and then expanding it to deal with Java. A massive admiration on theese people for the great effort

Another huge thanks to the Guardian folks for both writing JTorCtl and doing the voodoo to get the Tor OP running on Android.

And of course an endless amount of gratitude to the heroes of the Tor project for making this all possible in the first place and for their binaries which we are using for all our supported Java platforms.

# FAQ

## What is the maturity of the code in this project?
Well the release version is currently 2.0.1 so that should say something. This is an alpha. We have (literally) one test. Obviously we need a heck of a lot more coverage. But we have run that test and it does actually work which means that the Tor OP is being run and is available.


## Where does jtorctl-briar.jar come from?
This is a fork of jtorctl with some fixes from Briar. So we got it out of Briar's depot. The plan is that jtorctl is supposed to accept Briar's changes and then we will start to build jtorctl ourselves from the Tor depot directly.

## Where did the binaries for the Tor OP come from?
### Android
The ARM binary for Android came from the OrBot distribution available at https://guardianproject.info/releases/. I take the latest PIE release qualify APK, unzip it and go to res/raw and then decompress tor.mp3 and go into bin and copy out the tor executable file and put it into android/src/main/assets

### Windows
I download the Expert Bundle for Windows from https://www.torproject.org/download/download.html.en and took tor.exe, libeay32.dll, libevent-2-0-5.dll, libgcc_s_sjlj-1.dll and ssleay32.dll from the Tor directory. I then need to zip them all together into a file called tor.zip and stick them into java/src/main/resources/native/windows/x86.

## Where did the geoip and geoip6 files come from?
I took them from the Data/Tor directory of the Windows Expert Bundle [Why Should i use it](#Why Should i use it)

## Why does the Android code require minSdkVersion 16?!?!?! Why so high?
The issue is the tor executable that I get from Guardian. To run on Lollipop the executable has to be PIE. But PIE support only started with SDK 16. So if I'm going to ship a PIE executable I have to set minSdkVersion to 16. But!!!!! Guardian actually also builds a non-PIE version of the executable.

## Code of Conduct
This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

# Support
Please [open an issue](https://github.com/PanagiotisDrakatos/T0rlib4Android/issues) for support or even more [open a pull request](https://github.com/PanagiotisDrakatos/T0rlib4Android/pulls).


# License
<p> This project is licensed under the Apache License 2.0 - see the <a href="https://github.com/PanagiotisDrakatos/T0rlib4Android/blob/master/LICENSE"> Licence.md </a> file for details</p>



# Contributing
Please contribute using [Github Flow](https://github.com/PanagiotisDrakatos/T0rlib4Android). Create a branch, add commits.

 1. Fork it: git clone https://github.com/PanagiotisDrakatos/T0rlib4Android.git
 2. Create your feature branch: git checkout -b my-new-feature
 3. Commit your changes: git commit -am 'Add some feature'
 4. Push to the branch: git push origin my-new-feature
 5. Submit a pull request 
 6. :smile: :smile: :smile:
