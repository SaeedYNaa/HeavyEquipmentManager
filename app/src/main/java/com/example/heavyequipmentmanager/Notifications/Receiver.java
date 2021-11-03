package com.example.heavyequipmentmanager.Notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.heavyequipmentmanager.Administration.Constants;
import com.example.heavyequipmentmanager.Engine.EngineTool;
import com.example.heavyequipmentmanager.NotificationsActivity;
import com.example.heavyequipmentmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * In order to listen for and react to broadcast intents that are triggered by alarms,
 *  we must create a broadcast receiver and register it with the system (manifest file).
 * */
public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // this will be executed at selected interval time
//        Toast.makeText(context, "alarmed!", Toast.LENGTH_SHORT).show();

        HashMap<EngineTool, LinkedList<String>> expired = Constants.manager.checkEnginesDates();
        String contentText = "";
        ArrayList<String> contentTextlist = new ArrayList<>();
        for(EngineTool en: expired.keySet()){
            String name = en.getName();

            LinkedList<String> tags = expired.get(en);
            String exs = "";
            if(tags != null) {
                for (String ex : tags) {
                    exs += ex + " next month!" + "\n";
                }
            }
            String res = "\n" + name + "\n" + exs;
            contentTextlist.add(res);
            contentText += res;
        }


        Log.d("Receiver", "works done: " + expired.size());

        Intent noti_intent = new Intent(context, NotificationsActivity.class);
        noti_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        noti_intent.putExtra("contentList", contentTextlist);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, noti_intent, PendingIntent.FLAG_UPDATE_CURRENT);


       Notifications not = new Notifications(context);
       not.createNotification("Manager", pendingIntent, contentText);

//         for sound
        MediaPlayer md = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        md.start();

    }
}
