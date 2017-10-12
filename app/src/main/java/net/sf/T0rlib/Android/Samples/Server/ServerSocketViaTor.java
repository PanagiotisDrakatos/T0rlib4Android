package net.sf.T0rlib.Android.Samples.Server;

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


