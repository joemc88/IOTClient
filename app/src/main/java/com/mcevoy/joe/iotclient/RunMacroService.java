package com.mcevoy.joe.iotclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.security.PrivateKey;

/**
 * Created by Joe on 30/03/2017.
 */

public class RunMacroService extends IntentService {
   private String endpoint;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RunMacroService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        endpoint  = intent.getStringExtra("endpoint");
    }

    @Override
    public void onCreate() {

        Log.i("Calling",endpoint);
        Toast.makeText(this, " Sending Rest Call to "+endpoint, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart(Intent intent, int startId) {


    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
    }
}
