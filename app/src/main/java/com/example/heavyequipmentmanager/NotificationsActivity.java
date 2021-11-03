package com.example.heavyequipmentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * This Activity is responsible for display all the notifications in a ListView item.
 * */
public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Intent intent = getIntent();
        ArrayList<String> noti_exprired = (ArrayList<String>) intent.getExtras().get("contentList");

        ListView noti_list = (ListView) findViewById(R.id.listView_notification);

        ListViewNotification listViewNotification = new ListViewNotification(this, noti_exprired);
        noti_list.setAdapter(listViewNotification);

    }
}