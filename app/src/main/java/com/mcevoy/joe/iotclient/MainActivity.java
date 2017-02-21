package com.mcevoy.joe.iotclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String response;
    private  ArrayList<String> items;
    private  String[] discoveredHomeServices ={"home1","home2","home3","home4","home5"};
    private  String[] discoveredWorkServices ={"work1","work2","work3","work4","work5"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        items = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create edit prefs
        SharedPreferences storedItems = getSharedPreferences("items", 0);
        String item1String = storedItems.getString("item1", "Unknown");
        String item2String = storedItems.getString("item2", "Unknown");
        String item3String = storedItems.getString("item3", "Unknown");
        String item4String = storedItems.getString("item4", "Unknown");
        String item5String = storedItems.getString("item5", "Unknown");

        Log.i("saved",item1String);
        Button homeCheckInButton = (Button) findViewById(R.id.homeCheckin);

        homeCheckInButton.setOnClickListener(this);
        Button workCheckInButton = (Button) findViewById(R.id.workCheckin);
        workCheckInButton.setOnClickListener(this);

        Button endpointButton1 = (Button) findViewById(R.id.item1);
        endpointButton1.setOnClickListener(this);
        endpointButton1.setText(item1String);

        Button endpointButton2 = (Button) findViewById(R.id.item2);
        endpointButton2.setOnClickListener(this);
        endpointButton2.setText(item2String);


        Button endpointButton3 = (Button) findViewById(R.id.item3);
        endpointButton3.setOnClickListener(this);
        endpointButton3.setText(item3String);


        Button endpointButton4 = (Button) findViewById(R.id.item4);
        endpointButton4.setOnClickListener(this);
        endpointButton4.setText(item4String);


        Button endpointButton5 = (Button) findViewById(R.id.item5);
        endpointButton5.setOnClickListener(this);
        endpointButton5.setText(item5String);


        for(int i = 0;i <5; i++){
            items.add("local unknown");
        }
    }



    private  String buildServiceQuery(String[] discoveredServices){
        String retVal = "?uid="+getString(R.string.uid)+"&services=";
            retVal = retVal+discoveredServices[0];
        for(int i = 1; i< discoveredServices.length;i++){
            retVal = retVal+","+ discoveredServices[i];
        }
        return retVal;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeCheckin:
                TextView x = (TextView) findViewById(R.id.checkinStatus);
                x.setText("Home");
                Log.d("URL",getString(R.string.checkInURL)+buildServiceQuery(discoveredHomeServices));
                contactServer(getString(R.string.checkInURL)+buildServiceQuery(discoveredHomeServices));
                break;
            case R.id.workCheckin:
                TextView y = (TextView) findViewById(R.id.checkinStatus);
                y.setText("Work");

                contactServer(getString(R.string.checkInURL)+buildServiceQuery(discoveredWorkServices));
                break;
            case R.id.item1:
                startService(new Intent(this, CheckinService.class));
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(this, CheckinService.class);
                PendingIntent pintent = PendingIntent
                        .getService(this, 0, intent, 0);

                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                // Start service every hour
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        60*1000, pintent);
        }
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
                    if(jsArray != null){
                        items = new ArrayList<>();
                        int len = jsArray.length();
                        for(int i = 0; i <len;i++){

                           items.add(jsArray.get(i).toString());
                        }
                        Button   one =(Button)  findViewById(R.id.item1);
                        Button   two =(Button)  findViewById(R.id.item2);
                        Button   three =(Button)  findViewById(R.id.item3);
                        Button   four =(Button)  findViewById(R.id.item4);
                        Button   five =(Button)  findViewById(R.id.item5);
                        one.setText(items.get(0));
                        two.setText(items.get(1));
                        three.setText(items.get(2));
                        four.setText(items.get(3));
                        five.setText(items.get(4));
                    }
                    response = reader.getString("id");
                }catch(JSONException j) {
                    Log.d("json error",j.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String x = String.valueOf( statusCode);
                Log.d("Request failed. Error: ",x );
                response = "failure";
            }
        });
    }
}
