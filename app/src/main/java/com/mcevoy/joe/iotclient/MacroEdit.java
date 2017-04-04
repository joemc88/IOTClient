package com.mcevoy.joe.iotclient;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class MacroEdit extends AppCompatActivity implements View.OnClickListener{
    MacroHandler macroHandler;
    ArrayList<String> actions;
    ArrayList<Integer> hours;
    ArrayList<Integer> minutes;
    String macroName;
    int MID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_edit);
        macroHandler = new MacroHandler(this);
        macroName = getIntent().getStringExtra("macroToEdit");
        actions = getIntent().getStringArrayListExtra("actions");
        hours = getIntent().getIntegerArrayListExtra("hours");
        minutes = getIntent().getIntegerArrayListExtra("minutes");
        MID = getIntent().getIntExtra("MID",0);

        prepLayout();
    }
    private void prepLayout(){
        LinearLayout titlelayout = (LinearLayout) findViewById(R.id.titleSpace);
        titlelayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView title= (TextView) findViewById(R.id.titleText) ;
        title.setText(macroName);
       // titlelayout.addView(title);
        Button editTitle = (Button)findViewById(R.id.editTitle);
        editTitle.setText("editTitle");
        editTitle.setOnClickListener(this);
       // titlelayout.addView(editTitle);
        LinearLayout layout = (LinearLayout) findViewById(R.id.actionSpace);
        layout.setOrientation(LinearLayout.VERTICAL);

        //add list of actions
        for (int i = 0; i < actions.size(); i++) {
            final int j =i;
            LinearLayout macroRow = new LinearLayout(this);
            macroRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView text= new TextView(this);
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setText(actions.get(i));
            text.setId(i + 1);
            text.setWidth(300);

            final Button deleteButton = new Button(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            deleteButton.setMinWidth(20);
            deleteButton.setText("X");
            deleteButton.setId(i + 5);
            deleteButton.setWidth(20);
            deleteButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    AlertDialog yesNo =new AlertDialog.Builder(MacroEdit.this)
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    Log.i("CONFIRM","YES  DELETE");
                                    //TODO change text to deleted.
                                    //remove from list and remove its hour and minute entries also.
                                    actions.remove(j);
                                    hours.remove(j);
                                    minutes.remove(j);
                                    LinearLayout lyt = (LinearLayout) findViewById(R.id.actionSpace);
                                    lyt.removeAllViews();
                                    prepLayout();
                            }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    Log.i("CONFIRM","NO DO NOT  DELETE");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });
            //TODO fix id assignment and get actual time values from time picker and reassign times arraylist

            final Button editTime = new Button(this);
            editTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            editTime.setText(hours.get(i).toString()+":"+minutes.get(i).toString());

            editTime.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) { //When you click on the
                   // Process to get Current Time
                   final Calendar c = Calendar.getInstance();
                   int mHour = c.get(Calendar.HOUR_OF_DAY);
                   int mMinute = c.get(Calendar.MINUTE);

                   TimePickerDialog tpd = new TimePickerDialog(MacroEdit.this, //same Activity Context like before
                           new TimePickerDialog.OnTimeSetListener() {
                               @Override
                               public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                  editTime.setText(hourOfDay + ":" + minute);
                                  hours.set(j,hourOfDay);
                                  minutes.set(j,minute);
                                   //You set the time for the EditText created
                                   for(int i = 0; i < hours.size(); i ++){
                                       Log.i("hour", hours.get(i).toString());
                                       Log.i("minute", minutes.get(i).toString());
                                   }
                               }
                           }, mHour, mMinute, false);
                   tpd.show();
               }
            });
            macroRow.addView(text);
            macroRow.addView(deleteButton);
            macroRow.addView(editTime);
            layout.addView(macroRow);
        }
        //apply onclick to save and cancel buttons
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.addActionButton).setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        Log.i("Something clicked","entering switch");
        switch(v.getId()){
            case R.id.saveButton:
               TextView titleText = (TextView)findViewById(R.id.titleText);
                String name = (String) titleText.getText();
                macroHandler.editMacro(MID, name, actions, hours, minutes,this);
                Log.i("Save clicked","Saving");
                this.finish();
                break;
            case R.id.cancelButton:
                this.finish();
                Log.i("Cancel clicked","cancelling");
                break;
            case R.id.addActionButton:
                ServiceDiscovery service = new ServiceDiscovery();
                Calendar cal = Calendar.getInstance();
                final String services[] = service.getServices(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.DAY_OF_WEEK));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Action to add");
                builder.setItems(services, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String serviceName = services[which]; //Optional parameters
                        actions.add(serviceName);
                        hours.add(0);
                        minutes.add(0);
                        LinearLayout lyt = (LinearLayout) findViewById(R.id.actionSpace);
                        lyt.removeAllViews();
                        prepLayout();
                    }
                });
                builder.show();
                break;
            case R.id.editTitle:
                AlertDialog.Builder stringBuilder = new AlertDialog.Builder(this);
                stringBuilder.setTitle("Enter New Macro Name:");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                stringBuilder.setView(input);

                // Set up the buttons
                stringBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView x = (TextView) findViewById(R.id.titleText);
                        x.setText( input.getText().toString());
                        Log.i("New Macro Name","Accepted");
                    }
                });
                stringBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                stringBuilder.show();
                break;
        }



    }
}
