package com.example.heavyequipmentmanager.Engine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavyequipmentmanager.Administration.Constants;
import com.example.heavyequipmentmanager.MainActivity;
import com.example.heavyequipmentmanager.Administration.Manager;
import com.example.heavyequipmentmanager.R;

import java.util.Calendar;

public class AddEngine extends AppCompatActivity {
    DatePickerDialog.OnDateSetListener dateSetListener;
    private View row;
    private int editIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_engine);
        editIndex = -1;

        Intent intent = getIntent();
        if(intent.hasExtra("Engine")) {

            EngineTool en = (EngineTool) intent.getExtras().get("Engine");
            editIndex = intent.getExtras().getInt("index");
            if (en != null) {// Editing engine
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                editEngine(en);
            }
        }
        EditText engine_name = (EditText) findViewById(R.id.tool_name);
        TextView treatmentDate = (TextView) findViewById(R.id.treatment_date);
        TextView nextTreatment = (TextView) findViewById(R.id.next_treatment_date);
        TextView testDate = (TextView)findViewById(R.id.testDate);
        TextView ensurenceDate = (TextView)findViewById(R.id.ensurenceDate);

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

        buildCalenderDialog(AddEngine.this, treatmentDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                createAndSetDate(treatmentDate, year, month, day);
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
                    startActivity(intent);
                }
            }
        });

        Button cancel = (Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEngine.this, MainActivity.class);
                startActivity(intent);
            }
        });

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
                dialog.show();
            }
        });
    }

    public void createAndSetDate(TextView t, int year, int month, int day){
        String date = day + "/" + (month + 1) + "/" + year;
        t.setText(date);
    }

    public int addEngineTool(View view){
        EditText engine_name = (EditText) findViewById(R.id.tool_name);
        TextView treatment = (TextView) findViewById(R.id.treatment_date);
        TextView nextTreatment = (TextView) findViewById(R.id.next_treatment_date);
        TextView ensurence = (TextView) findViewById(R.id.ensurenceDate);
        TextView testDate = (TextView) findViewById(R.id.testDate);
        EditText working = (EditText) findViewById(R.id.working_hours);

        if(engine_name.getText().toString().matches("") || treatment.getText().toString().matches("") || nextTreatment.getText().toString().matches("") || ensurence.getText().toString().matches("") || testDate.getText().toString().matches("") || working.getText().toString().matches(""))
            return -1;
        double working_hours = working == null? 0 : Double.parseDouble(working.getText().toString());



        if(editIndex != -1) {

            Constants.manager.editEngine(editIndex, engine_name.getText().toString(), treatment.getText().toString(), nextTreatment.getText().toString(), working_hours);
            return editIndex;
        }else{// Edited functionallty
            EngineTool newEngine = new EngineTool(engine_name.getText().toString(), treatment.getText().toString(), nextTreatment.getText().toString(), working_hours, 0);
            newEngine.written = false;
            String ensurenceDatee = ensurence.getText().toString();
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
        EditText engine_name = (EditText) findViewById(R.id.tool_name);
        TextView treatment = (TextView) findViewById(R.id.treatment_date);
        TextView nextTreatment = (TextView) findViewById(R.id.next_treatment_date);
        TextView ensurence = (TextView) findViewById(R.id.ensurenceDate);
        TextView testDate = (TextView) findViewById(R.id.testDate);
        EditText working = (EditText) findViewById(R.id.working_hours);

        engine_name.setText(en.getName());
        treatment.setText(en.getTreatment());
        nextTreatment.setText(en.getNextTreatment());
        ensurence.setText(en.getEnsurenceDate());
        testDate.setText(en.getTestDate());
        working.setText(en.getWorkingHours().toString());
    }
}