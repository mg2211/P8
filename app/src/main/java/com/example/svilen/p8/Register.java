package com.example.svilen.p8;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Register extends AppCompatActivity{

    Button bRegister;
    EditText etUsername, etPassword, etFirstName, etLastName, etEmail;
    Spinner spinner;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        bRegister = (Button) findViewById(R.id.bRegister);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter;

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = spinner.getSelectedItem().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String firstname = etFirstName.getText().toString();
                String lastname = etLastName.getText().toString();
                String email = etEmail.getText().toString();

                if(!username.equals("") && !password.equals("") && !firstname.equals("") && !lastname.equals("") && !email.equals("")){
                    new RegisterTask(context).execute(role, username, password, firstname, lastname, email);
                } else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill all required fields";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }


            }
        });
    }
}



