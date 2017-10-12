package net.sf.T0rlib.Android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import t0rlib.sf.net.t0rlibandroidexample.R;

import net.sf.T0rlib.Android.Samples.Clients.TorClientSocks4;
import net.sf.T0rlib.Android.Samples.Server.ServerSocketViaTor;

public class MainActivity extends AppCompatActivity {
    private BootStrapper bootstrapper;
    private static Context context;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        //TRUE VALUE IF YOU WANT SETUP CLIENT ROLE
        //FALSE VALUE AUTO SERVER WILL BE INIT
        bootstrapper = new BootStrapper(false);
        bootstrapper.execute();

        // Example of a call to a native method
        // TextView tv = (TextView) findViewById(R.id.sample_text);
        //  tv.setText(stringFromJNI());
    }

    private class BootStrapper extends AsyncTask<String, Void, Void> {
        private static final long TIMEOUT = 30000;
        private static final int exportvalue = 500;
        private Boolean ClientRole;

        public BootStrapper(Boolean ClientRole) {
            this.ClientRole = ClientRole;
        }

        @Override
        protected Void doInBackground(String... fg) {
            if (ClientRole)
                InitClient();
            else
                InitServer();
            return null;
        }

        public void InitClient() {
            try {
                new TorClientSocks4(context).Init();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void InitServer() {
            try {
                new ServerSocketViaTor(context).Init();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("Tag", "On Post Execute");
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
   /* public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }*/
}
