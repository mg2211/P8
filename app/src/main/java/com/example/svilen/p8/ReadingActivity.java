package com.example.svilen.p8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReadingActivity extends AppCompatActivity {

    Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        bLogout = (Button) findViewById(R.id.bLogOutStReading);

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userinfo = new UserInfo(getApplicationContext());
                userinfo.logOut();
            }
        });
    }
}
