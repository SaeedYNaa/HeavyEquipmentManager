package com.example.heavyequipmentmanager.Administration;

import android.content.Context;

import com.example.heavyequipmentmanager.Administration.Database;
import com.example.heavyequipmentmanager.Administration.Manager;

import java.util.ArrayList;

public class Constants {

    /**
     * Application Context, assigned at runtime
     * The context is used for accessing system resources
     * **/
    public static Context CONTEXT = null;



    /**
     * App Manager, Only one Manager..
     *
     * **/
    public static Manager manager = null;



    /**
     * Intervals of time for checking the dates of each object.
     * if date expired (week before) a notification sent from the device
     * **/

    public static long miliseconds = 1000000;


    /**
     * A date regex for split date functionalaty
     * */
    public static String regex = "/";


    /**
     * the file name where the manager state is saved as serialize object.
     * **/
    public static final String manager_File_name = "Manager.ser";


    /**
     * A reference to the database which contains the Engines list
     * **/

    public static Database db = null;

    /**
     * For AlarmHandler:
     *      - triggerAfter: amount of time to set
     *      - triggerEvery: when to restart the alaram
     *      (both parameters in milliseconds)
     * **/
    public static long triggerAfter =  60 * 1000;        // one minute
    public static long triggerEvery =  60 * 1000;




    public static final String CHANNEL_ID = "ChannelID";



}
