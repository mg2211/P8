package com.example.svilen.p8;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.HashMap;

public class StudentActivity extends AppCompatActivity {


    Button bLogout;

    Context context = this;
    ListView lvAssToStudent;
    UserInfo userinfo;
    HashMap<String, String> user;
    String studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        setContentView(R.layout.activity_student);

        ListView lvAssToStudent = (ListView) findViewById(R.id.lvAssToStudent);



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

        Log.d("Back button pressed", " -Disabled");

    }



}
