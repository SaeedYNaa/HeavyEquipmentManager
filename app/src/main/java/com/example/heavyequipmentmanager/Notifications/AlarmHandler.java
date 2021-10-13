package com.example.heavyequipmentmanager.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.heavyequipmentmanager.Administration.Constants;

public class AlarmHandler {

    private Context context;

    public AlarmHandler(Context context){
        this.context = context;
    }

    // this will activate the alarm
    public void setAlarmManager(){
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, new Intent(context, ExecutableService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null){
            am.setRepeating(AlarmManager.RTC_WAKEUP, Constants.triggerAfter, Constants.triggerEvery, sender);
        }
    }

    public void cancelAlarmManager(){
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, new Intent(context, ExecutableService.class), 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null){
            am.cancel(sender);
        }
    }
}
