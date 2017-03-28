package com.mcevoy.joe.iotclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Joe on 16/03/2017.
 */

public class MacroHandler {
    private ArrayList<String> macroNames = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> macroHours = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> macroMinutes = new ArrayList<>();
    private ArrayList<ArrayList<String>> macroActions = new ArrayList<>();


    public MacroHandler(){
        createMacro("my first macro", new String[]{"Thing one","action 2","third task"}, new Integer[]{new Integer(12),new Integer(34), new Integer(4)}, new Integer[]{new Integer(12),new Integer(34), new Integer(4)});
        //TODO encorporate pollserver into macrohandler initialisation
        pollServer("http://192.168.0.151:8080/getMacros?uid=1");
    }
    public void pollServer(String url){
        RequestParams rp = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle handle =  client.get(url, rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                String respString = new String(responseBody);
                try{
                    JSONObject reader = new JSONObject(respString);
                    JSONObject macroZeroJSON=new JSONObject(reader.get("macro0").toString());
                    String name = macroZeroJSON.get("name").toString();
                    //TODO find a way of knwing how many macros are coming in and iterate over them
                    Log.d("name of macro 0",name);
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


        //TODO add code here to retrieve list of macros from server

    }

    public void createMacro(String name, String[] actions, Integer[] hours, Integer[] minutes){
        macroNames.add(name);
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

    public Object[]  getMacro(String macroName){
        int i = macroNames.indexOf(macroName);
        Object[] retArray = new Object[3];
        retArray[0] = macroActions.get(i);
        retArray[1] = macroHours.get(i);
        retArray[2] = macroMinutes.get(i);
        return retArray;
    }

    public String[] getNames(){
        String[] names = new String[macroNames.size()];
        for(int i=0; i< macroNames.size(); i++){
            names[i] = macroNames.get(i);
        }
        return names;
    }
    public void editMacro(String name,ArrayList<String> actions, ArrayList<Integer> hours, ArrayList<Integer> minutes){
        Log.i("SAVING",name);
    }
    public void playMacro(){

    }
    public void recordMacro(){

    }
}
