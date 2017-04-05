package com.mcevoy.joe.iotclient;

import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe on 05/04/2017.
 */

public class RequestSender {
    public RequestSender(){

    }

    public void sendRequest(String URL){
        RequestParams rp = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle handle =  client.get(URL, rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String x = String.valueOf( statusCode);
                Log.d("Request failed. Error: ",x );
            }
        });
    }
}
