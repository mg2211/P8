package com.example.svilen.p8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TeacherActivity extends AppCompatActivity {

    Button bRegisterUser;
    Button bShowClasses;
    Button bShowStudents;
    Button bLogout;
    List<Entry<String, HashMap<String, String>>> classList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final ServerRequests serverRequests = new ServerRequests(this);
        setContentView(R.layout.activity_teacher);
        final ServerRequests serverRequests = new ServerRequests(this);


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
                UserInfo userinfo = new UserInfo(getApplicationContext());
                userinfo.logOut();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Log.d("", "Back button pressed - disabled");
    }

    public void getClassList(HashMap<String, HashMap<String, String>> classes){
        Log.d("classlist reponse", classes.toString());

        for (Entry<String, HashMap<String, String>> classId : classes.entrySet()){
            Log.d("...",classId.getKey());
            String specificClass = classId.getKey();
            String specificClassname = classId.getValue().get("className");
            Log.d("classname", specificClassname);
            classList.add(classId);
        }

        Log.d("classlist array",classList.toString());

    }
}
