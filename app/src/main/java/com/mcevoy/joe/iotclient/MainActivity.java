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
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String newMacroName = "";
    private String response;
    private  ArrayList<String> items;
    private ServiceDiscovery discoverer;
    private Intent intent;
    private PendingIntent pintent;
    private  List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        discoverer = new ServiceDiscovery();

        //create edit prefs
        SharedPreferences storedItems = getSharedPreferences("items", 0);
        String item1String = storedItems.getString("item1", "Unknown");
        String item2String = storedItems.getString("item2", "Unknown");
        String item3String = storedItems.getString("item3", "Unknown");
        String item4String = storedItems.getString("item4", "Unknown");
        String item5String = storedItems.getString("item5", "Unknown");

        populateView();

        Button addMacroButton = (Button) findViewById(R.id.addMacro);
        addMacroButton.setOnClickListener(this);


       intent = new Intent(this, CheckinService.class);
         pintent = PendingIntent
                .getService(this, 0, intent, 0);

        startService(new Intent(this, CheckinService.class));
        Calendar cal = Calendar.getInstance();


        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                60*1000, pintent);
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
               stringBuilder.setTitle("Enter Mqcro Name");

               // Set up the input
               final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
               input.setInputType(InputType.TYPE_CLASS_TEXT);
               stringBuilder.setView(input);

                // Set up the buttons
               stringBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       newMacroName = input.getText().toString();
                   }
               });
               stringBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               stringBuilder.show();
               Log.i("New Macro Name",newMacroName);
               //do something with newmacro name
       }

           /* case R.id.homeCheckin:
             //   TextView x = (TextView) findViewById(R.id.checkinStatus);
                Calendar c = Calendar.getInstance();
                int notReallyHour = c.get(Calendar.HOUR);

                String time = Integer.toString(notReallyHour);
            //    x.setText(time);

                SharedPreferences storedItems = getSharedPreferences("items", 0);



                break;
            case R.id.workCheckin:
              //  TextView y = (TextView) findViewById(R.id.checkinStatus);
              //  y.setText("Cancel");
                AlarmManager salarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                salarm.cancel(pintent);
              //  contactServer(getString(R.string.checkInURL)+buildServiceQuery(discoveredWorkServices));
                break;
            case R.id.item1:
                startService(new Intent(this, CheckinService.class));
                Calendar cal = Calendar.getInstance();


                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                // Start service every hour
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        60*1000, pintent);

        }*/
    }

    /*private String[] contactServer( String url){
        RequestParams rp = new RequestParams();

        final String[] predictedServices = new String[5];
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle handle =  client.get(url, rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                SharedPreferences mPrefs = getSharedPreferences("items", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                String respString = new String(responseBody);
                try{
                    JSONObject reader = new JSONObject(respString);
                    JSONArray jsArray = reader.getJSONArray("item");
                    if(jsArray != null){
                        items = new ArrayList<>();
                        int len = jsArray.length();
                        for(int i = 0; i <len;i++){
                            mEditor.putString("item"+i, jsArray.get(i).toString()).commit();
                        }
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
        return predictedServices;
    }*/
    public void populateView(){
        //getLayouts
        LinearLayout macroSpace = (LinearLayout) findViewById(R.id.macroSpace);
        macroSpace.setOrientation(LinearLayout.VERTICAL);

        LinearLayout serviceSpace = (LinearLayout) findViewById(R.id.visibleServicesSpace);
        serviceSpace.setOrientation(LinearLayout.VERTICAL);

        LinearLayout predictionSpace = (LinearLayout) findViewById(R.id.predictionSpace);
        predictionSpace.setOrientation(LinearLayout.VERTICAL);

        MacroHandler macroHandler = new MacroHandler();
        final String[] macroNames = macroHandler.getNames();

        //add macro buttons
        int j = 0;

        for (String name : macroNames) {
            LinearLayout macroRow = new LinearLayout(this);
            macroRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            btnTag.setText(name);
            btnTag.setId(j + 1);
            btnTag.setWidth(500);
            final String currentName = name;
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //When you click on the
                    Intent intent = new Intent(getApplicationContext(), MacroEdit.class);
                    intent.putExtra("macroToEdit",currentName); //Optional parameters
                    getApplicationContext().startActivity(intent);

                }
            });

            Button recordButton = new Button(this);
            recordButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            recordButton.setMinWidth(20);
            recordButton.setText("O");
            recordButton.setId(j + 1);
            recordButton.setWidth(20);
            //TODO Add onlick listener to change recording mode to index of macro being recorded.
            //implement global variable for recorded. Once one recording startes any running recordings must stop
            Button playButton = new Button(this);
            playButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            playButton.setMinWidth(20);
            playButton.setText(">");
            playButton.setId(j + 1);
            playButton.setWidth(20);

            macroRow.addView(btnTag);
            macroRow.addView(recordButton);
            macroRow.addView(playButton);
            macroSpace.addView(macroRow);
            j++;
        }
        //get visible services
        ServiceDiscovery discover = new ServiceDiscovery();
        Calendar cal = Calendar.getInstance();
        String[] visibleServices = discover.getServices(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.DAY_OF_WEEK));
        //add visible service buttons
        for(int i = 0; i< visibleServices.length;i++){
            LinearLayout serviceRow = new LinearLayout(this);
            serviceRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            //TODO add on clicklistener to handle recording
            btnTag.setWidth(800);

            btnTag.setText(visibleServices[i]);
            btnTag.setId(i + 5);
            serviceRow.addView(btnTag);
            serviceSpace.addView(serviceRow);
        }
        //get shared prefs
        SharedPreferences storedItems = getSharedPreferences("items", 0);
        for(int i = 1; i < 5;i++){
            LinearLayout predictionRow = new LinearLayout(this);
            predictionRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            btnTag.setText( storedItems.getString("item"+i, "Unknown"));
            //TODO add onclick listener to pop up a toast saying message sent, and send the time and service to theserver
            btnTag.setWidth(800);


            btnTag.setId(i + 10);
            predictionRow.addView(btnTag);
            predictionSpace.addView(predictionRow);
        }
}}
