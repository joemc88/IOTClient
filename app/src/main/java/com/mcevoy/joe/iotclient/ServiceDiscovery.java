package com.mcevoy.joe.iotclient;

/**
 * Created by Joe on 20/02/2017.
 */

public class ServiceDiscovery {
    private String[] discoveredHomeServices ={"Blinds","Garage Door","Yard Light","Hive Up","Hive Down"};
    private String[] discoveredWorkServices ={"Temp Up","Temp Down","Blinds Up","","work5"};
    private String[] discoveredCollegeService = {"Smart Card","Unlock Lab","Temp Up","Temp Down","Book Room"};
    private String[] discoveredLunchServices = {"Order Food","Pay","lunch 3","lunch 4","lunch 5"};
    private String[] discoveredTravelServices =  {"Summon Car","Dismiss Car","Volume Up","Volume Down","Travel 5"};
    private String[] discoveredGymServices = {"Check In","Skip Song","Reserve Court","Increase Weight","Decrease Weight"};

    private String[][] contexts = {discoveredHomeServices,discoveredCollegeService,discoveredLunchServices,discoveredWorkServices,discoveredGymServices,discoveredTravelServices};

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
                services = contexts[1];
                break;
            case 10:
                services = contexts[1];
                break;
            case 11:
                services = contexts[1];
                break;
            case 12:
                services = contexts[1];
                break;
            case 13:
                services = contexts[2];
                break;
            case 14:
                services = contexts[1];
                break;
            case 15:
                services = contexts[1];
                break;
            case 16:
                services = contexts[1];
                break;
            case 17:
                services = contexts[3];
                break;
            case 18:
                services = contexts[3];
                break;
            case 19:
                services = contexts[3];
                break;
            case 20:
                services = contexts[4];
                break;
            case 21:
                services = contexts[4];
                break;
            case 22:
                services = contexts[0];
                break;
            case 23:
                services = contexts[0];
                break;
            case 24:
                services = contexts[0];
                break;
            default:
                services = contexts[0];
        }
        return services;
    }
}
