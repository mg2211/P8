package com.example.svilen.p8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
    }

    public boolean SaySomething(String message){

        if(message.equals("hello World")){
            return false;
        } else {
            return true;
        }

    }
}
