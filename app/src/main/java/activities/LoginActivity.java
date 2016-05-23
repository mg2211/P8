package activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.svilen.p8.R;

import helper.*;
import serverRequests.*;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    /*Setting up UI*/
    private EditText usernameInput;
    private EditText passwordInput;
    private String username;
    private String password;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Checking if a user is already logged in and sending the user to either student or teacheractivity if so*/
        UserInfo userInfo = new UserInfo(this);
        HashMap<String, String> user = userInfo.getUser();
        String role = user.get("role");
        if (!user.isEmpty()) {
            Intent intent = null;
            if (role.equals("student")) {
                intent = new Intent(this, StudentActivity.class);
            } else if (role.equals("teacher")) {
                intent = new Intent(this, TeacherActivity.class);
            }
            startActivity(intent);
        }

        Button loginButton = (Button) findViewById(R.id.loginButton);

        /*Setting onClickListener for login button*/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*setting UI elements*/
                usernameInput = (EditText) findViewById(R.id.username);
                passwordInput = (EditText) findViewById(R.id.password);

                /*Getting text from input fields*/
                username = usernameInput.getText().toString();
                password = passwordInput.getText().toString();

                /* Check if both inputs has text and if so, create a LoginTask*/
                if (!username.equals("") && !password.equals("")) {
                    new LoginTask(context).execute(username, password);
                } else {
                    /*If one or both of the fields are empty a toast saying so is shown*/
                    CharSequence alert = "Please fill in both username and password";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
            }
        });
    }

    /*Overriding the native back button - so the user can't log back in by pressing it*/
    @Override
    public void onBackPressed() {

    }
}
