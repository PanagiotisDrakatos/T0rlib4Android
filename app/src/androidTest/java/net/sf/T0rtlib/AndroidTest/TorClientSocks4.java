package net.sf.T0rtlib.AndroidTest;


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
        AndroidTorRelay node = new  AndroidTorRelay(ctx,fileLocation);
        int hiddenServicePort = 80;
        int localPort = node.getSocksPort();
        String OnionAdress = "xl5rbgygp2wbgdbn.onion";
        String localhost="127.0.0.1";

        Socket clientSocket = Utilities.socks4aSocketConnection(OnionAdress, hiddenServicePort, "127.0.0.1", localPort);

        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();

        out.writeObject("i am workingg");
        out.flush();
    }
}

