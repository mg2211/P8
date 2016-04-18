package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity{

    LinearLayout clTeacherRegister, clStudentRegister;
    Button bRegister;
    EditText etUsername, etPassword, etFirstName, etLastName, etEmail, etContactEmail;
    Spinner spinnerRole;
    Context context = this;
    List<String> roleList = new ArrayList<>();
    ArrayAdapter roleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        clTeacherRegister = (LinearLayout) findViewById(R.id.clGeneralRegister);
        clStudentRegister = (LinearLayout) findViewById(R.id.clStudentRegister);

        spinnerRole = (Spinner) findViewById(R.id.spinnerRole);
        etUsername = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etContactEmail = (EditText) findViewById(R.id.etContactEmail);
        bRegister = (Button) findViewById(R.id.bRegister);

        roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        Intent intent = getIntent();
        String roleId = intent.getStringExtra("roleId");

        new RoleTask(new RoleCallback() {
            @Override
            public void roleListDone(Map<String, HashMap<String, String>> roles) {
                for(Map.Entry<String, HashMap<String,String>> line : roles.entrySet()) {
                    for (Map.Entry<String,String> role : line.getValue().entrySet()) {
                        System.out.println("KEY: " + role.getKey() +role+ " VALUE:" + role.getValue());
                        String roleName = role.getValue();
                        roleList.add(roleName);
                    }
                }
                roleAdapter.notifyDataSetChanged();
            }
        }, context).execute(roleId);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                if(spinnerRole.getSelectedItem().toString().equals("teacher")){
                    clStudentRegister.setVisibility(View.GONE);
                }
                else if(spinnerRole.getSelectedItem().toString().equals("student")){
                    clStudentRegister.setVisibility(View.VISIBLE);
                }
            }


            public void onNothingSelected(AdapterView<?> arg0) {
                // noting to do do here...
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = spinnerRole.getSelectedItem().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String firstname = etFirstName.getText().toString();
                String lastname = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String parentemail = etContactEmail.getText().toString();

                boolean general = false;
                boolean student = false;

                if(!username.equals("") && !password.equals("") && !firstname.equals("") && !lastname.equals("") && !email.equals("")){
                    general = true; }

                if(general && !parentemail.equals("")){
                    student = true; }

                if(role.equals("student") && student) {
                    new RegisterTask(context).execute(role, username, password, firstname, lastname, email, parentemail);
                } else if(!role.equals("student") && general) {
                    new RegisterTask(context).execute(role, username, password, firstname, lastname, email, parentemail);
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



