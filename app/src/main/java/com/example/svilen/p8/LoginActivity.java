package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameInput;
    EditText passwordInput;
    String username;
    String password;
    Context context = this;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Check if a user is already logged in
        userInfo = new UserInfo(this);
        HashMap<String, String> user = userInfo.getUser();
        String role = user.get("role");
        if(!user.isEmpty()){
            Intent intent = null;
            if(role.equals("student")){
                intent = new Intent(this, StudentActivity.class);
            } else if(role.equals("teacher")) {
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

                username = usernameInput.getText().toString();
                password = passwordInput.getText().toString();



                if (!username.equals("") && !password.equals("")) {//check if both input fields has text
                    new LoginTask(context).execute(username, password);
                } else {
                    //make toast if one or both inputs are empty.
                    CharSequence alert = "Please fill in both username and password";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
            }
        });
    }


    }
