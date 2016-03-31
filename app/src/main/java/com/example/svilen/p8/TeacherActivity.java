package com.example.svilen.p8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeacherActivity extends AppCompatActivity {

    Button bRegisterUser;
    Button bShowClasses;
    Button bShowStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ServerRequests serverRequests = new ServerRequests(this);
        setContentView(R.layout.activity_teacher);

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
                        String teacherID = "1";
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
    }
}
