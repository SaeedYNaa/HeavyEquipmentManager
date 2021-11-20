package com.example.heavyequipmentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.heavyequipmentmanager.Administration.Constants;
import com.example.heavyequipmentmanager.Administration.Database;
import com.example.heavyequipmentmanager.Administration.ImageManager.ImageManager;
import com.example.heavyequipmentmanager.Administration.Manager;
import com.example.heavyequipmentmanager.Engine.AddEngine;
import com.example.heavyequipmentmanager.Engine.EngineTool;
import com.example.heavyequipmentmanager.Notifications.Receiver;
import com.example.heavyequipmentmanager.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Pair<ListView, ListViewEquipments> listViewData;

    public static int engineCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new Fade());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_main);
        Constants.CONTEXT = getApplicationContext();
//
//        createNotificationChannel();
//        startAlarm();


        if(Constants.db == null) {
            Constants.db = new Database(this, "ENGN");
        }

        if(Constants.manager == null) {
            Constants.manager = Manager.init();
        }


        // read from the database it all saved equipments
        if(Constants.manager.getEngines().size() == 0) {
            Constants.db.loadData();
        }

        // starting the ListView in the first page
         listViewData = showListView(getApplicationContext(), Constants.manager.getTools());

        FloatingActionButton addEngineButton = (FloatingActionButton) findViewById(R.id.addFloatingButton);
        addEngineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEngine.class);
                intent.putExtra("engineCounter", engineCounter);
                engineCounter++;
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
    }



    public Pair<ListView, ListViewEquipments> showListView(Context context, ArrayList<EngineTool> ens){
        ListViewEquipments viewEquipment = new ListViewEquipments(context, ens);
        ListView l = (ListView) findViewById(R.id.list_view);
        l.setAdapter(viewEquipment);
        l.setClickable(true);

        // Pop-Up page
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
                CircleImageView engineImage = (CircleImageView) infoPopUp.findViewById(R.id.engineImage_popup);
                TextView hOrKmHint = (TextView) infoPopUp.findViewById(R.id.workingHoursHint);
                TextView price = (TextView) infoPopUp.findViewById(R.id.price_popup);


                EngineTool en = viewEquipment.getEnginesList().get(i);
                name.setText(en.getName());
                testDate.setText(en.getTestDate());
                ensureneDate.setText(en.getEnsurenceDate());
                treatment.setText(en.getTreatment());
                nextTreatment.setText(en.getNextTreatment());
                price.setText(en.getPrice() + "₪");
                if(en.getWorkingHours() > 0) {
                    workingHours.setText(en.getWorkingHours() + "h");
                }
                else {
                    hOrKmHint.setText("קילומטראז': ");
                    workingHours.setText(en.getKM() + "KM");
                }

                Bitmap bmImg = ImageManager.readImage(en.getImagePath());
                engineImage.setImageBitmap(bmImg);

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
                                showListView(that, Constants.manager.getTools());
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
                        intent.putExtra("engineCounter", i);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(850, 1650);
            }
        });

        return new Pair<>(l, viewEquipment);
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
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, "Notification", importance);
            channel.setDescription("maybe it worked..");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Receiver.class);
        // The PendingIntent object here is just a way to wrap an intent into
        // an object that can send the encapsulated intent later from outside the current application.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // setting up the time to trigger the alarm:
        Calendar checkTime = Calendar.getInstance();
        checkTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        checkTime.set(Calendar.HOUR, 14);
        checkTime.set(Calendar.MINUTE, 53);

        long thirtySecondsFromNow = System.currentTimeMillis() + 30 * 1000;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                thirtySecondsFromNow,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent);

        Log.d("startAlarm", "Alarm started");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {


                listViewData.second.getFilter().filter(s);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search)
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}