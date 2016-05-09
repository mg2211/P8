package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    Context context = this;

    LinearLayout llUserFields;
    Button bAddUser, bRegisterUser, bEditUser, bDeleteUser;
    EditText etSearch, etUsername, etPassword, etFirstName, etLastName, etEmail, etContactEmail;
    Spinner spinnerRole;
    ListView lvListUsers, lvListClasses/*, listUsers*/;
    TextView tvRole;
    List<String> roleList = new ArrayList<>();
    ArrayAdapter roleAdapter;
    SimpleAdapter userListAdapter;
    //UserAdapter customUserListAdapter;
    List<Map<String, String>> userList = new ArrayList<>();
    UserActivity CustomListView = null;

    String userUserId;
    String userTeacherId;
    String userStudentId;
    String userClassId;
    String userRole;
    String userUsername;
    String userPassword;
    String userFirstName;
    String userLastName;
    String userEmail;
    String userParentEmail;
    Map<String, HashMap<String, String>> users;

    boolean newUser;
    boolean changed;
    boolean clear;

    //Intent intent = getIntent();
    //String roleId = intent.getStringExtra("roleId");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUsers();

        setContentView(R.layout.activity_user);

        llUserFields = (LinearLayout) findViewById(R.id.llUserFields);

        etSearch = (EditText) findViewById(R.id.etSearch);

        tvRole = (TextView) findViewById(R.id.tvRole);
        spinnerRole = (Spinner) findViewById(R.id.spinnerRole);
        etUsername = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etContactEmail = (EditText) findViewById(R.id.etContactEmail);
        bAddUser = (Button) findViewById(R.id.bAddUser);
        bRegisterUser = (Button) findViewById(R.id.bRegisterUser);
        bEditUser = (Button) findViewById(R.id.bEditUser);
        bDeleteUser = (Button) findViewById(R.id.bDeleteUser);

        lvListUsers = (ListView) findViewById(R.id.lvListUsers);
        //listUsers = (ListView) findViewById(R.id.userList);

        lvListClasses = (ListView) findViewById(R.id.lvListClasses);

        roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                if (spinnerRole.getSelectedItem().toString().equals("teacher")) {
                    //clStudentFields.setVisibility(View.GONE);
                } else if (spinnerRole.getSelectedItem().toString().equals("student")) {
                    //clStudentFields.setVisibility(View.VISIBLE);
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // noting to do do here...
            }
        });

        getRoles();

        userListAdapter = new SimpleAdapter(this,
                userList,
                android.R.layout.simple_list_item_2,
                new String[]{"firstName", "lastName"}, new int[]{android.R.id.text1, android.R.id.text2}) {
        };
        lvListUsers.setAdapter(userListAdapter);
        lvListUsers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        /*
        CustomListView = this;
        Resources res =getResources();

        customUserListAdapter = new UserAdapter(CustomListView, userList);
        customUserListAdapter.notifyDataSetChanged();

        listUsers.setAdapter(customUserListAdapter);
        listUsers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        */

        getUsers();

        /*
        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("position", String.valueOf(position));
                listUsers.setItemChecked(position, true);
                view.setSelected(true);
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                if (newUser) {
                                    if (createUser()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewUser(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                if (changed) {
                                    if (updateUser()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewUser(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            } else {
                                clear = true;
                            }
                            if (clear) {
                                setContentPane(position);
                            }
                        }
                    });
                } else {
                    setContentPane(position);
                }
            }
        });
        */


        lvListUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListUsers.setItemChecked(position, true);
                //view.setSelected(true);
                Log.d("position", String.valueOf(position));
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                if (newUser) {
                                    if (createUser()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewUser(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                if (changed) {
                                    if (updateUser()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewUser(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            } else {
                                clear = true;
                            }
                            if (clear) {
                                setContentPane(position);
                            }
                        }
                    });
                } else {
                    setContentPane(position);
                }
            }
        });



        bAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changed){
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if(dialogResponse){
                                if(newUser){
                                    if(createUser()){
                                        clear = true;
                                        setChanged(false);
                                        setNewUser(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            } else {
                                clear = true;
                            }
                        }
                    });
                }
            }
        });

        bRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        bEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        bDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        /*
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                customUserListAdapter.getFilter().filter(cs);
                customUserListAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        */

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Auto generated stub
            }
        });
    }

    public void setNewUser(boolean value){
        newUser = value;
        Log.d("new user value", String.valueOf(newUser));
    }

    public void setChanged(boolean value){
        changed = value;
        Log.d("Changed value", String.valueOf(changed));
    }

    public void confirm(final DialogCallback callback){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes - Save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.dialogResponse(true);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.dialogResponse(false);

                    }
                })
                .show();
    }

    public void getRoles() {
        new RoleTask(new RoleCallback() {
            @Override
            public void roleListDone(Map<String, HashMap<String, String>> roles) {
                for (Map.Entry<String, HashMap<String, String>> line : roles.entrySet()) {
                    for (Map.Entry<String, String> role : line.getValue().entrySet()) {
                        String roleName = role.getValue();
                        roleList.add(roleName);
                    }
                }
                roleAdapter.notifyDataSetChanged();
            }
        }, context).execute(); //roleID
    }

    public void getUsers() {
        new UserTask(new UserCallback() {
            @Override
            public void userTaskDone(Map<String, HashMap<String, String>> users) {
                userList.clear();
                for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                    Map<String, String> userInfo = new HashMap<>();
                    String role = user.getValue().get("role");
                    String userId = user.getValue().get("userId");
                    String teacherId = user.getValue().get("teacherId");
                    String studentId = user.getValue().get("studentId");
                    String username = user.getValue().get("username");
                    String password = user.getValue().get("password");
                    String firstName = user.getValue().get("firstName");
                    String lastName = user.getValue().get("lastName");
                    String email = user.getValue().get("email");
                    String parentEmail = user.getValue().get("parentEmail");
                    userInfo.put("role", role);
                    userInfo.put("userId", userId);
                    userInfo.put("teacherId", teacherId);
                    userInfo.put("studentId", studentId);
                    userInfo.put("username", username);
                    userInfo.put("password", password);
                    userInfo.put("firstName", firstName);
                    userInfo.put("lastName", lastName);
                    userInfo.put("email", email);
                    userInfo.put("parentEmail", parentEmail);
                    userList.add(userInfo);
                }
                userListAdapter.notifyDataSetChanged();
            }
        }, context).executeTask("FETCH","","","","","","","","","","",""); //Nothing within "" to get every text - see php script
    }

    public boolean createUser() {
        String role = spinnerRole.getSelectedItem().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String lastName = etLastName.getText().toString();
        String firstName = etFirstName.getText().toString();
        String email = etEmail.getText().toString();
        String parentEmail = etContactEmail.getText().toString();

        boolean general = false;
        boolean student = false;

        if (!username.equals("") && !password.equals("") && !firstName.equals("") && !lastName.equals("")
                && !email.equals("")) {
            general = true;
        }
        if (general && !parentEmail.equals("")) {
            student = true;
        }
        if (role.equals("student") && student) {
            new UserTask(new UserCallback() {
                @Override
                public void userTaskDone(Map<String, HashMap<String, String>> users) {
                }
            }, context).executeTask("CREATE", role, "", "", "", "", username, password, lastName, firstName,
                    email, parentEmail);
            return true;
        } else if (!role.equals("student") && general) {
            new UserTask(new UserCallback() {
                @Override
                public void userTaskDone(Map<String, HashMap<String, String>> users) {
                }
            }, context).executeTask("CREATE", role, "", "", "", "", username, password, lastName, firstName,
                    email, "");
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }
    }

    public boolean updateUser(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String parentEmail = etContactEmail.getText().toString();

        new UserTask(new UserCallback() {
            @Override
            public void userTaskDone(Map<String, HashMap<String, String>> users) {
            }
        }, context).execute("UPDATE", "", userUserId, "", "", "", username, password, lastName, firstName,
                email, parentEmail);
        return true;
    }

    public void deleteUser(){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete user " + userUsername)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UserTask(new UserCallback() {
                            @Override
                            public void userTaskDone(Map<String, HashMap<String, String>> users) {
                            }
                        }, context).executeTask("DELETE", userRole, userUserId,"","","","","","","","","");
                        getUsers();
                        etUsername.setText("");
                        etFirstName.setText("");
                        etLastName.setText("");
                        etEmail.setText("");
                        etContactEmail.setText("");
                        Log.d("id", userUserId);
                        Log.d("username", userUsername);
                        setNewUser(true);
                        setChanged(false);
                        bDeleteUser.setEnabled(false);
                        userUsername = "";
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void setContentPane(int position){
        if(position >= 0) {
            Map<String, String> userData = userList.get(position);
            userUserId = userData.get("userId");
            userTeacherId = userData.get("teacherId");
            userStudentId = userData.get("studentId");
            userClassId = userData.get("classId");
            userRole = userData.get("role");
            userUsername = userData.get("username");
            userPassword = userData.get("password");
            userFirstName = userData.get("firstName");
            userLastName = userData.get("lastName");
            userEmail = userData.get("email");
            userParentEmail = userData.get("parentEmail");
            tvRole.setText(userRole);
            etUsername.setText(userUsername);
            etPassword.setText(userPassword);
            etFirstName.setText(userFirstName);
            etLastName.setText(userLastName);
            etEmail.setText(userEmail);
            etContactEmail.setText(userParentEmail);
            tvRole.setVisibility(View.VISIBLE);
            etContactEmail.setVisibility(View.VISIBLE);
            spinnerRole.setVisibility(View.GONE);
            bEditUser.setEnabled(true);
            bDeleteUser.setEnabled(true);
            bRegisterUser.setEnabled(false);
            setChanged(false);
            setNewUser(false);
            //get question from db and add to listview.
        } else {
            etUsername.setText("");
            etFirstName.setText("");
            etLastName.setText("");
            etEmail.setText("");
            etContactEmail.setText("");
            bRegisterUser.setEnabled(true);
            bDeleteUser.setEnabled(false);
            bEditUser.setEnabled(false);
            spinnerRole.setVisibility(View.VISIBLE);
            etContactEmail.setVisibility(View.VISIBLE);
            tvRole.setVisibility(View.GONE);
            if (spinnerRole.getSelectedItem().toString().equals("teacher")) {
                etContactEmail.setVisibility(View.GONE);
            }
            setChanged(false);
            setNewUser(true);
        }
    }
}
