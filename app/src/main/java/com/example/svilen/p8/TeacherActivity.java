package com.example.svilen.p8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class TeacherActivity extends AppCompatActivity {

    Button bRegisterUser;
    Button bShowClasses;
    Button bShowStudents;
    Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ServerRequests serverRequests = new ServerRequests(this);
        setContentView(R.layout.activity_teacher);

        bShowStudents = (Button) findViewById(R.id.bShowStudents);
        bShowStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classId = "1";
                serverRequests.studentListExecute(classId);
            }
        });

        bRegisterUser = (Button) findViewById(R.id.bRegisterUser);
        bRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, Register.class);
                startActivity(intent);
            }
        });

        bShowClasses = (Button) findViewById(R.id.bShowClasses);
        bShowClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                UserInfo userInfo = new UserInfo(getApplicationContext());
                HashMap<String, String> user = userInfo.getUser();
                String teacherID = user.get("teacherId");
                serverRequests.classListExecute(teacherID);
            }
        });

        /*
        bShowStudents = (Button) findViewById(R.id.bShowStudents);
        bShowStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, Register.class);
                startActivity(intent);
            }
        });
        */

        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                HashMap<String, String> user = (HashMap<String, String>) preferences.getAll();
                Log.d("called", "yes");
                Log.d("Userinfo", user.toString());
            }
        });
    }
    @Override
    public void onBackPressed() {
        Log.d("","Back button pressed - disabled");
    }
}
