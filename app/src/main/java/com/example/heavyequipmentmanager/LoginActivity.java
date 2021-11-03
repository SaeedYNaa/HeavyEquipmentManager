package com.example.heavyequipmentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.heavyequipmentmanager.Administration.Client;
import com.example.heavyequipmentmanager.Administration.Constants;
import com.example.heavyequipmentmanager.Administration.Database;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setting up the database
        if(Constants.usersDB == null) {
            Constants.usersDB = new Database(LoginActivity.this, "USRS");
            Constants.usersDB.initUsersTable();
        }else
            checkClientStillLoggedIn();

        Button btn = (Button) findViewById(R.id.login_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernm = ((EditText) findViewById(R.id.inputName)).getText().toString();
                String passwd = ((EditText) findViewById(R.id.inputPassword)).getText().toString();

                Client c = Constants.usersDB.getClientByName(usernm, passwd);
                if(c == null){
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.inputPassword)).setText("");
                }
                else {
                    if(!c.isLoggedIn())
                        c.setLoggedIn(true);

                    Constants.usersDB.updateLogIn(usernm, passwd);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void checkClientStillLoggedIn(){
//        Client c = Constants.usersDB.getClientByName(usernm, passwd);
        // TO-DO.
    }
}