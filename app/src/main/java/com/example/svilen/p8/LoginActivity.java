package com.example.svilen.p8;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput = (EditText) findViewById(R.id.username);
                passwordInput = (EditText) findViewById(R.id.password);

                Username = usernameInput.getText().toString();
                Password = passwordInput.getText().toString();
                Log.d("username", Username);
                Log.d("Password", Password);

                if (!Username.equals("") && !Password.equals("")) {
                    try {
                        URL url = new URL("http://emilsiegenfeldt.dk/p8/login.php");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");

                        Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", Username)
                                .appendQueryParameter("password", Password);

                        String query          = builder.build().getEncodedQuery();
                        OutputStream os       = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(query);
                        writer.flush();
                        writer.close();
                        os.close();

                        connection.connect();

                        InputStream in = new BufferedInputStream(connection.getInputStream());

                        String response = IOUtils.toString(in, "UTF-8");

                        Log.d("response", response);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("Empty", "empty");
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
