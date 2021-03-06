package com.mcevoy.joe.iotclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Joe on 16/03/2017.
 */

public class MacroHandler implements Parcelable {
    private ArrayList<String> macroNames = new ArrayList<>();
    private ArrayList<Integer> macroIDS = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> macroHours = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> macroMinutes = new ArrayList<>();
    private ArrayList<ArrayList<String>> macroActions = new ArrayList<>();
    String URL ="http://192.168.0.151:8080";

    public MacroHandler(Context context){
       // createMacro("my first macro", new String[]{"Thing one","action 2","third task"}, new Integer[]{new Integer(12),new Integer(34), new Integer(4)}, new Integer[]{new Integer(12),new Integer(34), new Integer(4)});
        //TODO encorporate pollserver into macrohandler initialisation
        final Context contxt  = context;
        pollServer(context.getString(R.string.serverURL)+"/getMacros?uid=1", contxt);


    }

    protected MacroHandler(Parcel in) {
        macroNames = in.createStringArrayList();
    }

    public static final Creator<MacroHandler> CREATOR = new Creator<MacroHandler>() {
        @Override
        public MacroHandler createFromParcel(Parcel in) {
            return new MacroHandler(in);
        }

        @Override
        public MacroHandler[] newArray(int size) {
            return new MacroHandler[size];
        }
    };

    public void pollServer(String url, Context context){
        Toast.makeText(context, "Beginning poll", Toast.LENGTH_SHORT).show();
        RequestParams rp = new RequestParams();
        final Context contxt = context;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle handle =  client.get(url, rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                macroNames = new ArrayList<>();
                macroIDS = new ArrayList<>();
                macroHours = new ArrayList<>();
                macroMinutes = new ArrayList<>();
                macroActions = new ArrayList<>();

                String respString = new String(responseBody);
                try{
                    JSONObject reader = new JSONObject(respString);

                    for(int i=0; i <reader.length();i++){
                        JSONObject currentJSON=new JSONObject(reader.get("macro"+i).toString());
                        String name = currentJSON.get("name").toString();
                        String MID =currentJSON.get("MID").toString();
                        Log.i("MID",MID);
                        JSONArray recievedActions = (JSONArray) currentJSON.get("actions");
                        JSONArray recievedHours =(JSONArray) currentJSON.get("hours");
                        JSONArray recievedMinutes =(JSONArray) currentJSON.get("minutes");
                        ArrayList<String> Actions = new ArrayList<String>();
                        ArrayList<Integer> Hours = new ArrayList<Integer>();
                        ArrayList<Integer> Minutes = new ArrayList<Integer>();
                        Log.i("num actions:", recievedActions.toString());
                        for(int j =0; j< recievedActions.length();j++){
                            Actions.add(recievedActions.get(j).toString());//.toString("actions"));
                            Hours.add((Integer) recievedHours.get(j));
                            Minutes.add((Integer)recievedMinutes.get(j));

                        }
                        String[] actionArr =Actions.toArray(new String[Actions.size()]);
                        Integer[] hourArr = Hours.toArray(new Integer[Hours.size()]);
                        Integer[] minuteArr = Minutes.toArray(new Integer[Minutes.size()]);

                        Log.i("Creating macro:", name);
                        createMacro(Integer.valueOf(MID), name,actionArr,hourArr,minuteArr);

                    }



                    //TODO find a way of knowing how many macros are coming in and iterate over them
                  //  Log.d("name of macro 0",name);
                }catch(JSONException j) {
                    Log.d("json error",j.toString());
                }
                SharedPreferences mPrefs = contxt.getSharedPreferences("macros", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                Log.d("Clearing macros", "Clearing macros");
                mEditor.clear();
                String[] names = getNames();
                for(int k = 0; k <getNames().length;k++){
                    mEditor.putString("macro"+k,names[k]).apply();
                    Log.d("Adding macro","adding macro");
                }
                Toast.makeText(contxt, "Poll complete", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String x = String.valueOf( statusCode);
                Log.d("Macro poll failedError:",x );
            }
        });


        //TODO add code here to retrieve list of macros from server

    }

    public void createMacro(int MID, String name, String[] actions, Integer[] hours, Integer[] minutes){
       if(!macroNames.contains(name)){
            macroNames.add(name);
            macroIDS.add(MID);
            ArrayList<Integer> timesHours = new ArrayList<>();
            for(Integer x : hours){
                timesHours.add(x);
            }
            ArrayList<Integer> timesMinutes = new ArrayList<>();
            for(Integer x : minutes){
                timesMinutes.add(x);
            }
            macroHours.add(timesHours);
            macroMinutes.add(timesMinutes);
            ArrayList<String> actionsTemp = new ArrayList<>();
            for(String x : actions){
                actionsTemp.add(x);
            }
           macroActions.add(actionsTemp);
            //TODO add this new macro to the database
       }
    }

    public Object[]  getMacro(String macroName){
        if(macroNames.indexOf(macroName)>=0){
        int i = macroNames.indexOf(macroName);
        Object[] retArray = new Object[5];
        retArray[0] = macroActions.get(i);
        retArray[1] = macroHours.get(i);
        retArray[2] = macroMinutes.get(i);
        retArray[3] = macroIDS.get(i);
        retArray[4] = macroNames.get(i);
        return retArray;
        }else{
            return null;
        }
    }
    public Object[]  getMacro(int macroID){
        if(macroIDS.contains(macroID)){
            int i = macroIDS.indexOf(macroID);
            Object[] retArray = new Object[5];
            retArray[0] = macroActions.get(i);
            retArray[1] = macroHours.get(i);
            retArray[2] = macroMinutes.get(i);
            retArray[3] = macroIDS.get(i);
            retArray[4] = macroNames.get(i);
            return retArray;
        }else{
            return null;
        }
    }

    public String[] getNames(){
        String[] names = new String[macroNames.size()];
        for(int i=0; i< macroNames.size(); i++){
            names[i] = macroNames.get(i);
        }
        return names;
    }
    public void editMacro(int MID, String name,ArrayList<String> actions, ArrayList<Integer> hours, ArrayList<Integer> minutes, Context context){

        Log.i("SAVING",name);
        RequestParams rp = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        String query = context.getString(R.string.serverURL)+"/editMacro?uid=1&mid="+MID+"&name="+name+
        "&actions="+android.text.TextUtils.join(",", actions)+
        "&hours="+android.text.TextUtils.join(",", hours)+
        "&minutes="+ android.text.TextUtils.join(",", minutes);

        RequestHandle handle = client.get(query, rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Log.i("EditMacro","SUCCESS");

            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String x = String.valueOf( statusCode);
                Log.d("Request failed. Error: ",x );
            }
        });
    }


    public void playMacro(int macroID, Context context){
      Log.i("PLAY MACRO","Arrived");
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Object[] info = getMacro(macroID);
        ArrayList<String> actions = (ArrayList<String>) info[0];
        ArrayList<Integer> hours = (ArrayList<Integer>) info[1];
        ArrayList<Integer> minutes = (ArrayList<Integer>) info[2];
        int MID = (Integer) info[3];
        for(int i=0; i < actions.size();i++) {
            cal.set(Calendar.HOUR_OF_DAY, hours.get(i));
            cal.set(Calendar.MINUTE, minutes.get(i));
            cal.set(Calendar.SECOND, 0);
          //  cal.set(Calendar.MINUTE, 48);
            Log.i("Scheduling:",actions.get(i)+"for"+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            Intent intent =  new Intent(context, RunMacroService.class);

            intent.putExtra("endpoint",actions.get(i));
            PendingIntent pIntent = PendingIntent
                    .getService(context, 0, intent, 0);

            alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
        }





    }
    public void stopMacro(int macroID){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(macroNames);
    }
}
