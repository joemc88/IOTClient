package com.mcevoy.joe.iotclient;

/**
 * Created by Joe on 20/02/2017.
 */

public class ServiceDiscovery {
    private String[] discoveredHomeServices ={"Blinds","GarageDoor","YardLight","HiveUp","lightOn"};
    private String[] discoveredWorkServices ={"Temp Up","TempDown","BlindsUp","lightOn","work5"};
    private String[] discoveredCollegeService = {"SmartCard","UnlockLab","TempUp","lightOn","BookRoom"};
    private String[] discoveredLunchServices = {"OrderFood","Pay","lunch3","lunch4","lightOn"};
    private String[] discoveredTravelServices =  {"SummonCar","DismissCar","VolumeUp","lightOn","Travel5"};
    private String[] discoveredGymServices = {"CheckIn","lightOn","ReserveCourt","IncreaseWeight","DecreaseWeight"};

    private String[][] contexts = {discoveredHomeServices,discoveredWorkServices,discoveredCollegeService,discoveredLunchServices,discoveredTravelServices, discoveredGymServices};

    public ServiceDiscovery(){

    }
    public String[] getServices(int hour,  int day){
        String[] services;
        switch(hour){
            case 0:
                services = contexts[0];
                break;
            case 1:
                services = contexts[0];
                break;
            case 2:
                services = contexts[0];
                break;
            case 3:
                services = contexts[0];
                break;
            case 4:
                services = contexts[0];
                break;
            case 5:
                services = contexts[0];
                break;
            case 6:
                services = contexts[0];
                break;
            case 7:
                services = contexts[0];
                break;
            case 8:
                services = contexts[0];
                break;
            case 9:
                services = contexts[2];
                break;
            case 10:
                services = contexts[2];
                break;
            case 11:
                services = contexts[2];
                break;
            case 12:
                services = contexts[2];
                break;
            case 13:
                services = contexts[3];
                break;
            case 14:
                services = contexts[2];
                break;
            case 15:
                services = contexts[2];
                break;
            case 16:
                services = contexts[2];
                break;
            case 17:
                services = contexts[1];
                break;
            case 18:
                services = contexts[1];
                break;
            case 19:
                services = contexts[1];
                break;
            case 20:
                services = contexts[5];
                break;
            case 21:
                services = contexts[5];
                break;
            case 22:
                services = contexts[0];
                break;
            case 23:
                services = contexts[0];
                break;
            default:
                services = contexts[0];
        }
        return services;
    }
}
