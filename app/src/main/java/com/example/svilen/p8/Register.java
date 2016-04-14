package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity{

    Button bRegister;
    EditText etUsername, etPassword, etFirstName, etLastName, etEmail;
    Spinner spinnerRole;
    Context context = this;
    List<String> roleList = new ArrayList<>();
    ArrayAdapter roleAdapter;
    String[] test = new String[]{"EEN","TWEE","DRIE"};

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
        spinnerRole = (Spinner) findViewById(R.id.spinnerRole);

        roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        Intent intent = getIntent();
        String roleId = intent.getStringExtra("roleId");

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = spinnerRole.getSelectedItem().toString();
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

        new RoleTask(new RoleCallback() {
            @Override
            public void roleListDone(Map<String, HashMap<String, String>> roles) {
                System.out.println("Map roles in Register: "+roles);
                for(Map.Entry<String, HashMap<String,String>> line : roles.entrySet()) {
                    System.out.println(line.getKey());
                    for (Map.Entry<String,String> role : line.getValue().entrySet()) {
                        System.out.println("KEY: " + role.getKey() +role+ " VALUE:" + role.getValue());
                        String roleName = role.getValue();
                        roleList.add(roleName);
                        System.out.println("String RoleName in Register: "+roleName);
                    }
                }
                roleAdapter.notifyDataSetChanged();
            }
        }, context).execute(roleId);
    }
}



