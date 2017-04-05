package com.mcevoy.joe.iotclient;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

/**
 * Created by Joe on 03/04/2017.
 */

public class CustomButton extends Button {

    private String URL;
    public CustomButton(Context context) {
        super(context);
        //this.setTextColor(Color.WHITE);

        GradientDrawable gd1 = new GradientDrawable();
        gd1.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark)); // Changes this drawbale to use a single color instead of a gradient
        gd1.setCornerRadius(10);
        gd1.setStroke(2, ContextCompat.getColor(context, R.color.colorAccent));

        this.setBackgroundDrawable(gd1);

        this.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
      //  this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
       // this.setPadding(10,10,10,10);

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
