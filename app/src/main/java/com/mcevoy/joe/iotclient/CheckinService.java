package com.mcevoy.joe.iotclient;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import java.util.Calendar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckinService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, " MyService Created ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
       // Toast.makeText(this, " MyService Started", Toast.LENGTH_SHORT).show();
        Log.i("url attempt", getString(R.string.serverURL)+buildServiceQuery());
        contactServer(getString(R.string.serverURL)+"/checkIn"+buildServiceQuery());

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
       // Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    private void contactServer( String url){
        RequestParams rp = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle handle =  client.get(url, rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                String respString = new String(responseBody);
                try{
                    JSONObject reader = new JSONObject(respString);
                    JSONArray jsArray = reader.getJSONArray("item");

                    SharedPreferences mPrefs = getSharedPreferences("items", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    if(jsArray != null) {
                        mEditor.putString("item1", jsArray.get(0).toString()).commit();
                        Log.i("item",jsArray.get(0).toString());
                        mEditor.putString("item2", jsArray.get(1).toString()).commit();
                        mEditor.putString("item3", jsArray.get(2).toString()).commit();
                        mEditor.putString("item4", jsArray.get(3).toString()).commit();
                        mEditor.putString("item5", jsArray.get(4).toString()).commit();
                    }

                }catch(JSONException j) {
                    Log.d("json error",j.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String x = String.valueOf( statusCode);
                Log.d("Request failed. Error: ",x );
            }
        });
    }
    private int getHour(){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour;
    }
    private int getDay(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        return day;
    }
    private  String buildServiceQuery(){
        ServiceDiscovery serviceFinder = new ServiceDiscovery();
        //temporarily swap hours with minutes to speed up learning
        Calendar c = Calendar.getInstance();
        int notReallyHour = c.get(Calendar.MINUTE);

        String discoveredServices[] = serviceFinder.getServices(notReallyHour%24,getDay()) ;
        String retVal = "?uid="+getString(R.string.uid)+"&services=";
        retVal = retVal+discoveredServices[0];
        for(int i = 1; i< discoveredServices.length;i++){
            retVal = retVal+","+ discoveredServices[i];
        }
        retVal = retVal+"&day="+getDay()+"&hour="+notReallyHour%24;
        Log.i("sending", retVal);
        return retVal;
    }
}
