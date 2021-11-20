package com.example.heavyequipmentmanager.Administration;

import android.content.Context;

import com.example.heavyequipmentmanager.Administration.Database;
import com.example.heavyequipmentmanager.Administration.Manager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

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
     * starting a new approach, each user will have it's own manager
     * **/


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
     * another that contains the users and passwords
     * **/

    public static Database db = null;
    public static Database usersDB = null;

    /**
     * For AlarmHandler:
     *      - triggerAfter: amount of time to set
     *      - triggerEvery: when to restart the alaram
     *      (both parameters in milliseconds)
     * **/
    public static long triggerAfter =  60 * 60 * 1000;        // one HOUR
    public static long triggerEvery = 60 * 60 * 1000;




    public static final String CHANNEL_ID = "ChannelID";



}
