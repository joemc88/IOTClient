package com.mcevoy.joe.iotclient;


import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Joe on 16/03/2017.
 */

public class Macro {
    private ArrayList<String> actionNames = new ArrayList<>();
    private  ArrayList<Long> actionTimes = new ArrayList<>();
    private String name;
    public Macro(String name, Long[] times, String[]actions){
        for(int i= 0; i< times.length; i++){
            actionNames.add(actions[i]);
            actionTimes.add(times[i]);
        }
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public void addAction(String action, Long time){
        actionNames.add(action);
        actionTimes.add(time);
    }
    public void changeTime(int index, Long newTime){
        actionTimes.set(index, newTime);
    }
    public void removeAction(int index){
        actionNames.remove(index);
        actionTimes.remove(index);
    }
}
