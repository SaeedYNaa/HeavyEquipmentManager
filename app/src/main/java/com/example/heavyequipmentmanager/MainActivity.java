package com.example.heavyequipmentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavyequipmentmanager.Administration.Constants;
import com.example.heavyequipmentmanager.Administration.Database;
import com.example.heavyequipmentmanager.Administration.Manager;
import com.example.heavyequipmentmanager.Engine.AddEngine;
import com.example.heavyequipmentmanager.Engine.EngineTool;
import com.example.heavyequipmentmanager.Notifications.AlarmHandler;
import com.example.heavyequipmentmanager.Notifications.ExecutableService;
import com.example.heavyequipmentmanager.Notifications.Notifications;
import com.example.heavyequipmentmanager.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ListView listViewData;
    public final String CHANNEL_ID = "ChannelID";
    public final String CHANE_NAME = "Notification";
//    Database db;
    Notifications not;
    AlarmHandler alarmHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        Constants.CONTEXT = getApplicationContext();

        createNotificationChannel();
        startAlarm();


        if(Constants.db == null)
            Constants.db = new Database(this);
        if(Constants.manager == null)
            Constants.manager = Manager.init();

        // Start the notification and background alarm
//        not.createNotificationChannel();
//        alarmHandler = new AlarmHandler(this);
//        alarmHandler.cancelAlarmManager();
//        alarmHandler.setAlarmManager();

        // read from the database it all saved equipments

        Constants.db.loadData();

//        Constants.manager.logEngines();
//        Constants.manager.logEngines();

        // starting the ListView in the first page
        listViewData = showLIstView(getApplicationContext(), Constants.manager.getTools());

        FloatingActionButton addEngineButton = (FloatingActionButton) findViewById(R.id.addFloatingButton);
        addEngineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEngine.class);
                startActivity(intent);
            }
        });


        FloatingActionButton searchEngine = (FloatingActionButton) findViewById(R.id.searchFloatingButton);
        searchEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


    }


    public ListView showLIstView(Context context, ArrayList<EngineTool> ens){
        ListViewEquipments viewEquipment = new ListViewEquipments(context, ens);
        ListView l = (ListView) findViewById(R.id.list_view);
        l.setAdapter(viewEquipment);
        l.setClickable(true);

        Activity that = this;
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(that);
                View infoPopUp = that.getLayoutInflater().inflate(R.layout.popup_info, (ViewGroup) view, false);

                TextView name = (TextView) infoPopUp.findViewById(R.id.engineName_popup);
                TextView testDate = (TextView) infoPopUp.findViewById(R.id.testDate_popup);
                TextView ensureneDate = (TextView) infoPopUp.findViewById(R.id.ensurensDate_popup);
                TextView treatment = (TextView) infoPopUp.findViewById(R.id.treatment_popup);
                TextView nextTreatment = (TextView) infoPopUp.findViewById(R.id.nextTreatment_popup);
                TextView workingHours = (TextView) infoPopUp.findViewById(R.id.working_popup);


                EngineTool en = Constants.manager.getEngines().get(i);
                name.setText(en.getName());
                testDate.setText(en.getTestDate());
                ensureneDate.setText(en.getEnsurenceDate());
                treatment.setText(en.getTreatment());
                nextTreatment.setText(en.getNextTreatment());
                workingHours.setText(en.getWorkingHours() + "h");


                /*Delete button*/
                dialogBuilder.setView(infoPopUp);
                AlertDialog dialog = dialogBuilder.create();
                FloatingActionButton delete = (FloatingActionButton) infoPopUp.findViewById(R.id.deleteEngine_popup);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(context, "id: "+ i, Toast.LENGTH_SHORT).show();
                        YesNoDialog(infoPopUp.getContext(), "Delete this item?", new YesNoCallbacks() {
                            @Override
                            public void yes() {
                                Constants.manager.deleteEngine(i);
                                showLIstView(that, Constants.manager.getTools());
                                dialog.dismiss();
                            }

                            @Override
                            public void no() {//nothing to do

                            }
                        });

                    }
                });

                FloatingActionButton edit = (FloatingActionButton) infoPopUp.findViewById(R.id.editEngine_popup);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AddEngine.class);
                        intent.putExtra("Engine", en);
                        intent.putExtra("index", i);
                        startActivity(intent);
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(850, 1600);
            }
        });
        return l;
    }


    public static void YesNoDialog(Context context, String title, YesNoCallbacks callbacks){
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set a title for alert dialog
        builder.setTitle(title);


        // Set the alert dialog yes button click listener
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.yes();
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.no();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }


    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, "Manager", importance);
            channel.setDescription("maybe it worked..");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startAlarm(){
        Intent intent = new Intent(this, ExecutableService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                Constants.triggerAfter,
                pendingIntent);

    }
}