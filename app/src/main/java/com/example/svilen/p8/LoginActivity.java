package com.example.svilen.p8;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
