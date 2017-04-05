package com.mcevoy.joe.iotclient;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.ArrayList;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public int recording;
    public int playing;
    private String response;
    private  ArrayList<String> items;
    ServiceDiscovery discoverer;
    Intent intent;
    PendingIntent pIntent;

    MacroHandler macroHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recording = -1;
        setContentView(R.layout.activity_main);
        discoverer = new ServiceDiscovery();

        //create edit prefs
        SharedPreferences storedItems = getSharedPreferences("items", 0);
        String item1String = storedItems.getString("item1", "Unknown");
        String item2String = storedItems.getString("item2", "Unknown");
        String item3String = storedItems.getString("item3", "Unknown");
        String item4String = storedItems.getString("item4", "Unknown");
        String item5String = storedItems.getString("item5", "Unknown");
        macroHandler = new MacroHandler(this);
        populateView(macroHandler);

        Button addMacroButton = (Button) findViewById(R.id.addMacro);
        addMacroButton.setOnClickListener(this);


       intent = new Intent(this, CheckinService.class);
         pIntent = PendingIntent
                .getService(this, 0, intent, 0);

        startService(new Intent(this, CheckinService.class));
        Calendar cal = Calendar.getInstance();
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                30*1000, pIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        macroHandler.pollServer(getString(R.string.serverURL)+"/getMacros?uid=1", this);
        Calendar cal = Calendar.getInstance();
        TextView time = (TextView) findViewById(R.id.time);
        int min = cal.get(Calendar.MINUTE);
        Log.i("Minute as hour",String.valueOf(min));
        time.setText(String.valueOf(min%24)+":00");
        populateView(macroHandler);
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
            case R.id.addMacro:
                //TODO add code here to call macrohandler and create a new macro
                AlertDialog.Builder stringBuilder = new AlertDialog.Builder(this);
                stringBuilder.setTitle("Enter Macro Name");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                stringBuilder.setView(input);

                // Set up the buttons
                stringBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();

                        RequestParams rp = new RequestParams();

                        AsyncHttpClient client = new AsyncHttpClient();


                        RequestHandle handle = client.get(getString(R.string.serverURL) + "/createNewMacro?uid=1&name=" + newName, rp, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                Log.i("createMacro", "SUCCESS");

                            }

                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                String x = String.valueOf(statusCode);
                                Log.d("create failed. Error: ", x);
                            }
                        });
                    }
                });
                stringBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                stringBuilder.show();
        }

    }

    public void populateView(final MacroHandler macroHandler){
        //getLayouts
        LinearLayout macroSpace = (LinearLayout) findViewById(R.id.macroSpace);
        macroSpace.setOrientation(LinearLayout.VERTICAL);
        macroSpace.removeAllViews();

        LinearLayout serviceSpace = (LinearLayout) findViewById(R.id.visibleServicesSpace);
        serviceSpace.setOrientation(LinearLayout.VERTICAL);
        serviceSpace.removeAllViews();

        LinearLayout predictionSpace = (LinearLayout) findViewById(R.id.predictionSpace);
        predictionSpace.setOrientation(LinearLayout.VERTICAL);
        predictionSpace.removeAllViews();

        //MacroHandler macroHandler = new MacroHandler();
        Log.i("GOING AHEAD"," With whats in this object");
        SharedPreferences mPrefs = getSharedPreferences("macros", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        String[] macroNames = new String[mPrefs.getAll().size()];
        for(int i = 0; i <mPrefs.getAll().size();i++){
           macroNames[i] = mPrefs.getString("macro"+i,"Unknown");
        }

       // final String[] macroNames = macroHandler.getNames();

        //add macro buttons
        int j = 0;

        for (String name : macroNames) {
            LinearLayout macroRow = new LinearLayout(this);
            macroRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            CustomButton btnTag = new CustomButton(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            btnTag.setText(name);
            btnTag.setId(j + 1);
            btnTag.setWidth(500);
            final String currentName = name;
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //When you click on the
                    Intent intent = new Intent(getApplicationContext(), MacroEdit.class);

                    if(macroHandler.getMacro(currentName)!= null) {
                        Object[] info = macroHandler.getMacro(currentName);
                        ArrayList<String> actions = (ArrayList<String>) info[0];
                        ArrayList<Integer> hours = (ArrayList<Integer>) info[1];
                        ArrayList<Integer> minutes = (ArrayList<Integer>) info[2];
                        int MID = (Integer) info[3];
                        intent.putExtra("macroToEdit", currentName); //Optional parameters
                        intent.putExtra("actions", actions);
                        intent.putExtra("hours", hours);
                        intent.putExtra("minutes", minutes);
                        intent.putExtra("MID", MID);
                        getApplicationContext().startActivity(intent);
                    }
                }
            });

            final CustomButton recordButton = new CustomButton(this);
            if(macroHandler.getMacro(currentName)!= null) {
                Object[] info = macroHandler.getMacro(currentName);
                int MID = (Integer) info[3];
                if(recording==MID){
                    recordButton.setText("[]");
                }else{
                    recordButton.setText("O");
                }
            }
            recordButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            recordButton.setMinWidth(20);

            recordButton.setId(j + 1);
            recordButton.setWidth(20);
            //TODO Add onlick listener to change recording mode to index of macro being recorded.
            //implement global variable for recorded. Once one recording startes any running recordings must stop
            recordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(macroHandler.getMacro(currentName)!= null) {
                        Object[] info = macroHandler.getMacro(currentName);
                        int MID = (Integer) info[3];
                        if(recording==MID){
                            recording = -1;
                            recordButton.setText("O");
                        }else{
                            recording = MID;
                            recordButton.setText("[]");
                        }

                    }
                }

            });
            final CustomButton playButton = new CustomButton(this);
            playButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            playButton.setMinWidth(20);
            playButton.setText(">");

            playButton.setId(j + 1);
            playButton.setWidth(20);
           playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(playing <= 0){
                        Toast.makeText(getApplicationContext(), "Playing Macro", Toast.LENGTH_SHORT).show();
                        Calendar cal = Calendar.getInstance();
                        Object[] info = macroHandler.getMacro(currentName);
                        if(info != null){
                            ArrayList<String> actions = (ArrayList<String>) info[0];
                            ArrayList<Integer> hours = (ArrayList<Integer>) info[1];
                            ArrayList<Integer> minutes = (ArrayList<Integer>) info[2];
                            int MID = (Integer) info[3];
                            playButton.setText("playing");
                            playing = MID;
                            macroHandler.playMacro(MID, getApplicationContext());

                        }

                    }else{
                        playButton.setText(">");
                        playing = -1;
                        Toast.makeText(getApplicationContext(), "Stop Recording/playing before playing macro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            macroRow.addView(btnTag);
            macroRow.addView(recordButton);
            macroRow.addView(playButton);
            macroSpace.addView(macroRow);
            j++;
        }
        //get visible services
        ServiceDiscovery discover = new ServiceDiscovery();
        Calendar cal = Calendar.getInstance();
        final String[] visibleServices = discover.getServices(cal.get(Calendar.MINUTE)%24, cal.get(Calendar.DAY_OF_WEEK));
        //add visible service buttons
        for(int i = 0; i< visibleServices.length;i++){
            LinearLayout serviceRow = new LinearLayout(this);
            serviceRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            final CustomButton btnTag = new CustomButton(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            final String name = visibleServices[i];
            btnTag.setText(visibleServices[i]);
            if(visibleServices[i].equals("lightOn")){
                btnTag.setURL(getString(R.string.serverURL)+"/lightOn");
            }
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Rest Call sent to "+name, Toast.LENGTH_SHORT).show();

                    if(recording >= 0){
                        Calendar cal = Calendar.getInstance();
                        Object[] info = macroHandler.getMacro(recording);
                        ArrayList<String> actions = (ArrayList<String>) info[0];
                        ArrayList<Integer> hours = (ArrayList<Integer>) info[1];
                        ArrayList<Integer> minutes = (ArrayList<Integer>) info[2];
                        int MID = (Integer) info[3];
                        String macroName = info[4].toString();
                        actions.add(name);
                        hours.add(cal.get(Calendar.HOUR_OF_DAY));
                        minutes.add(cal.get(Calendar.MINUTE));
                        macroHandler.editMacro(MID,macroName,actions,hours,minutes,getApplicationContext());
                    }
                    if(btnTag.getText().equals("lightOn")){
                        RequestSender requestSender = new RequestSender();
                        requestSender.sendRequest(btnTag.getURL());
                    }
                }
            });
            btnTag.setWidth(1000);



            btnTag.setId(i + 5);
            serviceRow.addView(btnTag);
            serviceSpace.addView(serviceRow);
        }
        //get shared prefs
        SharedPreferences storedItems = getSharedPreferences("items", 0);

        for(int i = 1; i <= storedItems.getAll().size();i++){
            LinearLayout predictionRow = new LinearLayout(this);
            predictionRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            final CustomButton buttonTag = new CustomButton(this);
            buttonTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            buttonTag.setText( storedItems.getString("item"+i, "Unknown"));
            if( storedItems.getString("item"+i, "Unknown").equals("lightOn")){
                buttonTag.setURL(getString(R.string.serverURL)+"/lightOn");
            }

            final String pName = storedItems.getString("item"+i, "Unknown");
            buttonTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Rest Call sent to "+pName, Toast.LENGTH_SHORT).show();
                    if(recording >= 0){
                        Calendar cal = Calendar.getInstance();
                        Object[] info = macroHandler.getMacro(recording);
                        ArrayList<String> actions = (ArrayList<String>) info[0];
                        ArrayList<Integer> hours = (ArrayList<Integer>) info[1];
                        ArrayList<Integer> minutes = (ArrayList<Integer>) info[2];
                        int MID = (Integer) info[3];
                        actions.add(pName);
                        hours.add(cal.get(Calendar.HOUR_OF_DAY));
                        minutes.add(cal.get(Calendar.MINUTE));
                        macroHandler.editMacro(MID,pName,actions,hours,minutes,getApplicationContext());

                    }
                    if(buttonTag.getText().equals("lightOn")){
                        RequestSender requestSender = new RequestSender();
                        requestSender.sendRequest(buttonTag.getURL());
                    }
                }

            });
            buttonTag.setWidth(1000);
            buttonTag.setId(i + 10);
            predictionRow.addView(buttonTag);
            predictionSpace.addView(predictionRow);
        }
    }
}
