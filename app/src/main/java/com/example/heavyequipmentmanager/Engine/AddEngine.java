package com.example.heavyequipmentmanager.Engine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.heavyequipmentmanager.Administration.Constants;
import com.example.heavyequipmentmanager.Administration.ImageManager.ImageManager;
import com.example.heavyequipmentmanager.Administration.ImageManager.ImagePicker;
import com.example.heavyequipmentmanager.MainActivity;
import com.example.heavyequipmentmanager.Administration.Manager;
import com.example.heavyequipmentmanager.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;

import static com.example.heavyequipmentmanager.Administration.ImageManager.ImagePicker.CAMER_REQUEST;
import static com.example.heavyequipmentmanager.Administration.ImageManager.ImagePicker.STORAGE_REQUEST;

public class AddEngine extends AppCompatActivity {
    private int editIndex;
    ImageView engineImagePicker;
    LinearLayout imageLayout;
    ToggleButton switcher;
    EditText hOrKm;

    // layout elements
    EditText engine_name;
    TextView treatmentDate;
    TextView nextTreatment;
    TextView testDate;
    TextView ensurenceDate;
    EditText renterCost;
    int currentIndex;
    boolean editHours = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new Fade());
        setContentView(R.layout.activity_add_engine);
        editIndex = -1;
        switcher = (ToggleButton) findViewById(R.id.switcher);
        engine_name = (EditText) findViewById(R.id.tool_name);
        treatmentDate = (TextView) findViewById(R.id.treatment_date);
        nextTreatment = (TextView) findViewById(R.id.next_treatment_date);
        testDate = (TextView)findViewById(R.id.testDate);
        ensurenceDate = (TextView)findViewById(R.id.ensurenceDate);
        renterCost = (EditText)findViewById(R.id.renterCost);
        hOrKm = (EditText) findViewById(R.id.hOrKmHint);
        engineImagePicker = (ImageView) findViewById(R.id.addImage);
        imageLayout = (LinearLayout) findViewById(R.id.imageLayout);

        hOrKm.setHint("שעות פעילות");

        // first check if Edit or new
        Intent intent = getIntent();
        if(intent.hasExtra("Engine")) {

            EngineTool en = (EngineTool) intent.getExtras().get("Engine");
            editIndex = intent.getExtras().getInt("index");
            if (en != null) {// Editing engine
//                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                editEngine(en);
            }
        }
        currentIndex = (int) intent.getExtras().get("engineCounter");


        engine_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(engine_name.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        Activity that = this;
        // setting up the image (you can choose an image from gallery or take one from camera)
        engineImagePicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                int picd = 0;
                if(picd == 0){
                    if(!ImageManager.imagePicker.checkCameraPermission(that))
                        ImageManager.imagePicker.requestCameraPermission(that);
                    else
                        ImageManager.imagePicker.pickFromGallery(that);
                }else if(picd == 1){
                    if(!ImageManager.imagePicker.checkStoragePermission(that))
                        ImageManager.imagePicker.requestStoragrePermission(that);
                    else
                        ImageManager.imagePicker.pickFromGallery(that);
                }
            }
        });

        // Toggle Button for working hours or Km
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    hOrKm.setHint("קילומטראז'");
                    editHours = false;
                }
                else {
                    hOrKm.setHint("שעות פעילות");
                    editHours = true;
                }
            }
        });



        buildCalenderDialog(AddEngine.this, treatmentDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                createAndSetDate(treatmentDate, year, month, day);
                createAndSetDate(nextTreatment, year + 1, month, day);
            }
        });

        buildCalenderDialog(AddEngine.this, nextTreatment, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                createAndSetDate(nextTreatment, year, month, day);
            }
        });

        buildCalenderDialog(AddEngine.this, testDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                createAndSetDate(testDate, year, month, day);

            }
        });

        buildCalenderDialog(AddEngine.this, ensurenceDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                createAndSetDate(ensurenceDate, year, month, day);
            }
        });
        Button save_button = (Button)findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addEngineTool(view) == -1){
                    Toast.makeText(AddEngine.this, "Fill all information", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(AddEngine.this, MainActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AddEngine.this).toBundle());
                }
            }
        });

        Button cancel = (Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEngine.this, MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AddEngine.this).toBundle());
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                ImageManager.imagePicker.saveImagePath(currentIndex, resultUri.getPath().toString());
                engineImagePicker.setBackground(null);
                Picasso.with(this).load(resultUri).into(engineImagePicker);
            }
        }
    }


    // For picking up images
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case CAMER_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                    boolean storage_accepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED);

                    if (camera_accepted && storage_accepted)
                        ImageManager.imagePicker.pickFromGallery(this);
                    else
                        Toast.makeText(this, "Camera permission not enabel", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case STORAGE_REQUEST:{
                if (grantResults.length > 0) {
                    boolean storage_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);

                    if (storage_accepted)
                        ImageManager.imagePicker.pickFromGallery(this);
                    else
                        Toast.makeText(this, "Camera permission not enabel", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }


    public void buildCalenderDialog(Context context, TextView t, DatePickerDialog.OnDateSetListener st){
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Dialog_MinWidth, st, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setTitle(t.getHint().toString());
                dialog.show();
            }
        });
    }

    public void createAndSetDate(TextView t, int year, int month, int day){
        String date = day + "/" + (month + 1) + "/" + year;
        t.setText(date);
    }

    public int addEngineTool(View view){
        TextView renterCost = (TextView)findViewById(R.id.renterCost);
        double hoursAndKm = 0;

        // check if everything is legal
        if(engine_name.getText().toString().matches("") || treatmentDate.getText().toString().matches("") || nextTreatment.getText().toString().matches("") || ensurenceDate.getText().toString().matches("") || testDate.getText().toString().matches("") || hOrKm.getText().toString().matches(""))
            return -1;

        hoursAndKm = Double.parseDouble(hOrKm.getText().toString());
        if(editIndex != -1) {
            Constants.manager.editEngine(editIndex, engine_name.getText().toString(), treatmentDate.getText().toString(), nextTreatment.getText().toString(), editHours? hoursAndKm : 0, hoursAndKm, ImageManager.imagePicker.getImagePath(currentIndex), Double.parseDouble(renterCost.getText().toString()));
            if(!editHours) // KM
                Constants.manager.getEngines().get(editIndex).setKM(hoursAndKm);
            return editIndex;

        }else{// Edited functionality
            EngineTool newEngine = new EngineTool(engine_name.getText().toString(), treatmentDate.getText().toString(), nextTreatment.getText().toString(), editHours? hoursAndKm : 0, ImageManager.imagePicker.getImagePath(currentIndex), Double.parseDouble(renterCost.getText().toString()));
            if(!editHours) // KM
                newEngine.setKM(hoursAndKm);

            String ensurenceDatee = ensurenceDate.getText().toString();
            if(!ensurenceDatee.matches(""))
                newEngine.setEnsurenceDate(ensurenceDatee);

            String test_date = testDate.getText().toString();
            if(!ensurenceDatee.matches(""))
                newEngine.setTestDate(test_date);

            int index = Manager.manager.getEngines().size();
            Manager.manager.addEngine(index, newEngine);
            return index;
        }
    }

    public void editEngine(EngineTool en){


        engine_name.setText(en.getName());
        treatmentDate.setText(en.getTreatment());
        nextTreatment.setText(en.getNextTreatment());
        ensurenceDate.setText(en.getEnsurenceDate());
        testDate.setText(en.getTestDate());
        hOrKm.setText(en.getWorkingHours().toString());
        renterCost.setText(en.getPrice() + "");
        // some declarations
        if(en.getWorkingHours() == 0) { // means KM is pressed
            hOrKm.setText(en.getKM().toString());
            switcher.setChecked(true);
        }

        if(!en.getImagePath().matches("")) {
            Bitmap bmImg = BitmapFactory.decodeFile(en.getImagePath());
            engineImagePicker.setImageBitmap(bmImg);
            engineImagePicker.setBackground(null);
        }
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}