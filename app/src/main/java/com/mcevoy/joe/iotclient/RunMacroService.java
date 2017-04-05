package com.mcevoy.joe.iotclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.security.PrivateKey;

import static android.content.ContentValues.TAG;

/**
 * Created by Joe on 30/03/2017.
 */

public class RunMacroService extends IntentService {
   private String endpoint;
    private String URL;
    private static final String TAG = "RunMacroService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RunMacroService(){
        super(TAG);
    }
    public RunMacroService(String name) {
        super(name);

    // endpoint  = intent.getStringExtra("endpoint");
    Log.d("inside macro", "onHandleIntent: ");
}
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        endpoint  = intent.getStringExtra("endpoint");
        URL = intent.getStringExtra("URL");
        Log.d(TAG, "onHandleIntent: ");
        Log.i("Calling",endpoint+" On time");
        Toast.makeText(this, " Sending Rest Call to "+endpoint, Toast.LENGTH_SHORT).show();
        RequestSender requestSender = new RequestSender();
        requestSender.sendRequest(URL);

    }

    @Override
    public void onCreate() {

        Log.i("Calling","Service On time");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("Calling","Service On time");


    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
    }
}
