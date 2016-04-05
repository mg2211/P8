package com.example.svilen.p8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        TextView classLabel = (TextView) findViewById(R.id.classId);

        Intent intent = getIntent();
        String classID = intent.getStringExtra("classId");

        classLabel.setText(classID);
    }
}
