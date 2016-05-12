package com.example.svilen.p8;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
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

    Button bAddUser, bRegisterUser, bEditUser, bDeleteUser, bAssignClass, bModifyClasses,
            bDialogAssignClass, bDialogCancel;
    EditText etSearch, etUsername, etPassword, etFirstName, etLastName, etEmail, etContactEmail,
            etDialogSearch;
    Spinner spinnerRole;
    ListView lvListUsers, lvDialogListClasses;
    TextView tvTitleRegister, tvTitleEdit, tvTitleStudentClass, tvDialogClassTitle, tvUserClassName, tvDialogClassName, tvDialogTeacherName,
            tvDialogTeacherEmail;
    ArrayAdapter roleAdapter;
    SimpleAdapter userListAdapter, dialogClassListAdapter;
    List<String> roleList = new ArrayList<>();
    List<Map<String, String>> userList = new ArrayList<>();
    List<Map<String, String>> classList = new ArrayList<>();

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
    String userClassName;

    String classClassId;
    String classClassName;
    String classTeacherFistName;
    String classTeacherLastName;
    String classTeacherEmail;

    boolean newUser;
    boolean changed;
    boolean clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        etSearch = (EditText) findViewById(R.id.etSearch);

        tvTitleRegister = (TextView) findViewById(R.id.tvTitleRegister);
        tvTitleEdit = (TextView) findViewById(R.id.tvTitleEdit);
        tvTitleStudentClass = (TextView) findViewById(R.id.tvTitleStudentClass);
        tvUserClassName = (TextView) findViewById(R.id.tvUserClassName);

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
        bAssignClass = (Button) findViewById(R.id.bAssignClass);
        bModifyClasses = (Button) findViewById(R.id.bModifyClasses);

        lvListUsers = (ListView) findViewById(R.id.lvListUsers);
        //lvListClasses = (ListView) findViewById(R.id.lvListClasses);

        roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                Log.d("Spinner position", spinnerRole.getSelectedItem().toString());
                if (spinnerRole.getSelectedItem().toString().equals("teacher")) {
                    bModifyClasses.setVisibility(View.VISIBLE);
                    etContactEmail.setVisibility(View.GONE);
                    bAssignClass.setVisibility(View.GONE);
                    tvTitleStudentClass.setVisibility(View.GONE);
                    tvUserClassName.setVisibility(View.GONE);
                } else if (spinnerRole.getSelectedItem().toString().equals("student")) {
                    bModifyClasses.setVisibility(View.VISIBLE);
                    etContactEmail.setVisibility(View.VISIBLE);
                    bAssignClass.setVisibility(View.VISIBLE);
                    tvTitleStudentClass.setVisibility(View.VISIBLE);
                    tvUserClassName.setVisibility(View.VISIBLE);
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // noting to do do here...
            }
        });

        setNewUser(true);
        setChanged(false);
        getRoles();

        userListAdapter = new SimpleAdapter(this, userList,
                R.layout.user_listview_item, new String[]{"username", "firstName", "lastName", "role"},
                new int[]{R.id.clTvUsername, R.id.clTvFirstName, R.id.clTvLastName, R.id.clTvRole});

        /*
        userListAdapter = new SimpleAdapter(this, userList,
                android.R.layout.simple_list_item_2, new String[]{"firstName", "lastName"},
                new int[]{android.R.id.text1, android.R.id.text2}) {
        };
        */

        lvListUsers.setAdapter(userListAdapter);
        lvListUsers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        getUsers();
        setContentPane(-1);

        /*
        classListAdapter = new SimpleAdapter(this,
                classList,
                android.R.layout.simple_list_item_2,
                new String[]{"className", "teacherFirstName"}, new int[]{android.R.id.text1, android.R.id.text2}) {
        };
        lvListClasses.setAdapter(classListAdapter);
        lvListClasses.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        */

        dialogClassListAdapter = new SimpleAdapter(this, classList,
                R.layout.user_class_listview_item, new String[]{"className", "teacherFirstName", "TeacherLastName"},
                new int[]{R.id.clTvClassName, R.id.clTvTeacherFirstName, R.id.clTvTeacherLastName}) {
        };

        getAllClasses();

        lvListUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListUsers.setItemChecked(position, true);
                view.setSelected(true);
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
                            }
                            if (clear) {
                                setContentPane(position);
                            } else {
                                clear = true;
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
                                        setContentPane(-1);
                                    } else {
                                        clear = false;
                                        setContentPane(-1);
                                    }
                                }
                            } else {
                                clear = true;
                                setContentPane(-1);
                            }
                        }
                    });
                } else {
                    setContentPane(-1);
                }
            }
        });

        bRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                setChanged(false);
            }
        });

        bEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
                setChanged(false);
            }
        });

        bDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                setChanged(false);
            }
        });

        bModifyClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassActivity.class);
                startActivity(intent);
            }
        });

        bAssignClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classAssignmentDialog(userUserId);
            }
        });

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

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etUsername.getText().toString();
                if (!content.equals("") && !content.equals(userUsername)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etPassword.getText().toString();
                if (!content.equals("") && !content.equals(userPassword)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etFirstName.getText().toString();
                if (!content.equals("") && !content.equals(userFirstName)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etLastName.getText().toString();
                if (!content.equals("") && !content.equals(userLastName)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etEmail.getText().toString();
                if (!content.equals("") && !content.equals(userEmail)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        etContactEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etContactEmail.getText().toString();
                if (!content.equals("") && !content.equals(userParentEmail)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });
    }

    public void setNewUser(boolean value) {
        newUser = value;
        setEnabledUiItems();
        Log.d("new user value", String.valueOf(newUser));
    }

    public void setChanged(boolean value) {
        changed = value;
        setEnabledUiItems();
        Log.d("Changed value", String.valueOf(changed));
    }

    public void confirm(final DialogCallback callback) {
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

    public void getAllClasses() {
        new ClassTaskNew(new ClassCallbackNew() {
            @Override
            public void classListDone(Map<String, HashMap<String, String>> classes) {
                classList.clear();
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    Log.d("getAllClasses result", String.valueOf(classInfo));
                    classList.add(classInfo);
                }
                dialogClassListAdapter.notifyDataSetChanged();
                Log.d("dialogClassListAdapter", "successfully updated");
            }
        }, context).executeTask("FETCH", "", "", "", "", "");
    }

    public void getUserClasses(String userId) {
        classList.clear();
        new ClassTaskNew(new ClassCallbackNew() {
            @Override
            public void classListDone(Map<String, HashMap<String, String>> classes) {
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    classList.add(classInfo);
                }
            }
        }, context).executeTask("FETCH", "", "", "", "", userId);
        //dialogClassListAdapter.notifyDataSetChanged();
    }

    public void getUsers() {
        new UserTask(new UserCallback() {
            @Override
            public void userTaskDone(Map<String, HashMap<String, String>> users) {
                userList.clear();
                for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                    final Map<String, String> userInfo = new HashMap<>();
                    String role = user.getValue().get("role");
                    String userId = user.getValue().get("userId");
                    String teacherId = user.getValue().get("teacherId");
                    String studentId = user.getValue().get("studentId");
                    String classId = user.getValue().get("classId");
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
                    userInfo.put("classId", classId);
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
        }, context).executeTask("FETCH", "", "", "", "", "", "", "", "", "", "", ""); //Nothing within "" to get every text - see php script
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
            getUsers();
            //setContentPane(getLastEntryPosition(userList));
            return true;
        } else if (!role.equals("student") && general) {
            new UserTask(new UserCallback() {
                @Override
                public void userTaskDone(Map<String, HashMap<String, String>> users) {
                }
            }, context).executeTask("CREATE", role, "", "", "", "", username, password, lastName, firstName,
                    email, "");
            getUsers();
            //setContentPane(getLastEntryPosition(userList));
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }
    }

    public boolean updateUser() {
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
        getUsers();
        return true;
    }

    public boolean updateUserClass(String userId, String classId){

        new UserTask(new UserCallback() {
            @Override
            public void userTaskDone(Map<String, HashMap<String, String>> users) {
            }
        }, context).execute("UPDATE", "", userId, "", "", classId, "", "", "", "",
                "", "");
        getUsers();
        return true;
    }

    public void deleteUser() {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete user " + userUsername)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UserTask(new UserCallback() {
                            @Override
                            public void userTaskDone(Map<String, HashMap<String, String>> users) {
                            }
                        }, context).executeTask("DELETE", userRole, userUserId, "", "", "", "", "", "", "", "", "");
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

    /*
    public int getLastEntryPosition(List<Map<String, String>> userList) {
        int highestEntry = 0;
        int currentEntry;
        int position = 0;
        for (Map<String, String> userMap : userList) {
            currentEntry = Integer.parseInt(userMap.get("UserId"));
            if (currentEntry > highestEntry) {
                highestEntry = currentEntry;
            }
        }
        etDialogSearch.setText("");
        for (int i = 0; i < userList.size(); i++) {
            Map<String, String> userMap = userList.get(i);
            if(Integer.toString(highestEntry).equals(userMap.get("userId"))){
                return i;
            }
        }
        return 0;
    }
    */


    public void setEnabledUiItems() {
        if (newUser) {
            tvTitleRegister.setVisibility(View.VISIBLE);
            bRegisterUser.setVisibility(View.VISIBLE);
            tvTitleEdit.setVisibility(View.GONE);
            bEditUser.setVisibility(View.GONE);
            bDeleteUser.setVisibility(View.GONE);
            if (changed) {
                bEditUser.setEnabled(false);
                bDeleteUser.setEnabled(false);
                bRegisterUser.setEnabled(true);
            } else {
                bEditUser.setEnabled(false);
                bDeleteUser.setEnabled(false);
                bRegisterUser.setEnabled(false);
            }
        } else {
            tvTitleEdit.setVisibility(View.VISIBLE);
            bEditUser.setVisibility(View.VISIBLE);
            bDeleteUser.setVisibility(View.VISIBLE);
            tvTitleRegister.setVisibility(View.GONE);
            bRegisterUser.setVisibility(View.GONE);
            if (changed) {
                bEditUser.setEnabled(true);
                bDeleteUser.setEnabled(true);
                bRegisterUser.setEnabled(false);
            } else {
                bEditUser.setEnabled(false);
                bDeleteUser.setEnabled(true);
                bRegisterUser.setEnabled(false);
            }
        }
    }

    public void setContentPane(int position) {
        if (position >= 0) {
            Map<String, String> userData = (Map) userListAdapter.getItem(position);
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
            userClassName = getClassName(userUserId, userList);
            spinnerRole.setSelection(getIndex(spinnerRole, userRole));
            spinnerRole.setEnabled(false);
            etUsername.setText(userUsername);
            etPassword.setText(userPassword);
            etFirstName.setText(userFirstName);
            etLastName.setText(userLastName);
            etEmail.setText(userEmail);
            etContactEmail.setText(userParentEmail);
            tvUserClassName.setText(userClassName);
            setChanged(false);
            setNewUser(false);
        } else {
            userUserId = null;
            userTeacherId = null;
            userStudentId = null;
            userClassId = null;
            userRole = null;
            userUsername = null;
            userPassword = null;
            userFirstName = null;
            userLastName = null;
            userEmail = null;
            userParentEmail = null;
            etUsername.setText("");
            etPassword.setText("");
            etPassword.setText("");
            etFirstName.setText("");
            etLastName.setText("");
            etEmail.setText("");
            etContactEmail.setText("");
            //tvUserClassName.setText("");
            spinnerRole.setEnabled(true);
            setChanged(false);
            setNewUser(true);
        }
    }

    private String getClassName(String userId, List<Map<String, String>> userList){
        String userTeacher = null;
        String userNotInClass = "Not enrolled in class";
        if("teacher".equals(userRole)){
            Log.d("getClassName result", String.valueOf(userTeacher));
            return userTeacher;
        } else {
            for (Map<String, String> userMap : userList) {
                if (userMap.get("userId").equals(userId) && userMap.containsKey("classId")) {
                    for (Map<String, String> classMap : classList) {
                        if (classMap.get("classId").equals(userMap.get("classId"))) {
                            Log.d("getClassName result", classMap.get("className"));
                            return classMap.get("className");
                        }
                    }
                }
            }
        }
        Log.d("getClassName result", userNotInClass);
        return userNotInClass;
    }

    //set Spinner value
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    //create a new classAssignmentDialog
    private void classAssignmentDialog(final String userId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_assign_class, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();

        lvDialogListClasses = (ListView) layout.findViewById(R.id.lvDialogListClasses);

        etDialogSearch = (EditText) layout.findViewById(R.id.etDialogSearch);
        tvDialogClassTitle = (TextView) layout.findViewById(R.id.tvDialogClassTitle);
        tvDialogClassName = (TextView) layout.findViewById(R.id.tvDialogClassName);
        tvDialogTeacherName = (TextView) layout.findViewById(R.id.tvDialogTeacherName);
        tvDialogTeacherEmail = (TextView) layout.findViewById(R.id.tvDialogTeacherEmail);

        bDialogAssignClass = (Button) layout.findViewById(R.id.bDialogAssignClass);
        bDialogCancel = (Button) layout.findViewById(R.id.bDialogCancel);

        lvDialogListClasses.setAdapter(dialogClassListAdapter);
        lvDialogListClasses.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        getAllClasses();

        lvDialogListClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListUsers.setItemChecked(position, true);
                view.setSelected(true);
                Log.d("position", String.valueOf(position));
                setDialogContentPane(position);
            }
        });

        etDialogSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogClassListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Auto generated stub
            }
        });

        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bDialogAssignClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserClass(userUserId, classClassId);
            }
        });

        setDialogContentPane(getClassListPosition(userClassId, classList));
    }

    public int getClassListPosition(String classId, List<Map<String, String>> classList){
        for(int i = 0; i < classList.size(); i++){
            Map<String, String> map = classList.get(i);
            if(map.get("classId").equals(classId)){
                return i;
            }
        }
        return -1;
    }

    public void setDialogContentPane(int position) {
        if (position >= 0) {
            Map<String, String> classData = (Map) dialogClassListAdapter.getItem(position);
            classClassId = classData.get("classId");
            classClassName = classData.get("className");
            classTeacherFistName = classData.get("teacherFirstName");
            classTeacherLastName = classData.get("teacherLastName");
            classTeacherEmail = classData.get("teacherEmail");
            tvDialogClassName.setText(classClassName);
            tvDialogTeacherName.setText(classTeacherFistName + " " + classTeacherLastName);
            tvDialogTeacherEmail.setText(classTeacherEmail);
        } else {
            tvDialogClassName.setText("");
            tvDialogTeacherName.setText("");
            tvDialogTeacherEmail.setText("");
        }
    }
}
