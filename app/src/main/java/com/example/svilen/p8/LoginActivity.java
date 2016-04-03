package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameInput;
    EditText passwordInput;
    String Username;
    String Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ServerRequests serverRequests = new ServerRequests(this);

        //Check if a user is already logged in
        UserInfo userinfo = new UserInfo(this);
        HashMap<String, String> user = userinfo.getUser();
        String role = user.get("role");
        if(!user.isEmpty()){
            Intent intent = null;
            if(role.equals("student")){
                Log.d("Student","logged in");
                intent = new Intent(this, StudentActivity.class);
            } else if(role.equals("teacher")) {
                Log.d("Teacher","logged in");
                intent = new Intent(this, TeacherActivity.class);
            }
            startActivity(intent);
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput = (EditText) findViewById(R.id.username);
                passwordInput = (EditText) findViewById(R.id.password);

                Username = usernameInput.getText().toString();
                Password = passwordInput.getText().toString();



                if (!Username.equals("") && !Password.equals("")) {//check if both input fields has text
                    serverRequests.loginExecute(Username, Password);
                } else {
                    //make toast if one or both inputs are empty.
                    Context context = getApplicationContext();
                    CharSequence alert = "Please fill in both username and password";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
            }
        });
    }


    }
