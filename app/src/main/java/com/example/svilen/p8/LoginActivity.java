package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
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
import org.json.JSONException;
import org.json.JSONObject;

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

                if (!Username.equals("") && !Password.equals("")) { //check if both input fields has text
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


                        //Catch server response
                        InputStream in = new BufferedInputStream(connection.getInputStream());

                        String response = IOUtils.toString(in, "UTF-8"); //convert to readable string

                        //convert to JSON object
                        JSONObject result = new JSONObject(response);

                        //extract variables from JSONObject result var
                        String generalResponse = result.getString("generalresponse");
                        int responseCode = result.getInt("responsecode");
                        String username = result.getString("username");
                        String role = result.getString("role");

                        //call method to check login
                        checkCredentials(generalResponse, responseCode, username, role);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("...","...");
                    }
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

    public void checkCredentials(String generalResponse, int responsecode, String username, String role) {

        Intent intent;
        if(responsecode == 100){

            //if login credentials are right - set intent to either student or teacher depending on role variable.
            if(role.equals("student")){
                intent = new Intent(this, StudentActivity.class);
            } else {
               intent = new Intent(this, TeacherActivity.class);
            }

            //start the right activity
            startActivity(intent);

        } else if(responsecode == 200){
            //if login is wrong - make a toast saying so.
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
        }

    }
}
