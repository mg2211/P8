package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.svilen.p8.R;

import callback.*;
import helper.*;
import serverRequests.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    /** context */
    private final Context context = this;

    /** Button used to start the process of creating a new user */
    private Button bAddUser;

    /** Button used to create a user */
    private Button bRegisterUser;

    /** Button used to edit a user */
    private Button bEditUser;

    /** Button used to delete a user */
    private Button bDeleteUser;

    /** Button used to assign a class to a student */
    private Button bAssignClass;

    /** EditText field where username is entered */
    private EditText etUsername;

    /** EditText field where password is entered */
    private EditText etPassword;

    /** EditText field where first name is entered */
    private EditText etFirstName;

    /** EditText field where last name is entered */
    private EditText etLastName;

    /** EditText field where email is entered */
    private EditText etEmail;

    /** EditText field where parent or contact email is entered */
    private EditText etContactEmail;

    /** Spinner containing roles*/
    private Spinner spinnerRole;

    /** ListView containing users */
    private ListView lvListUsers;

    /** ListView containing classes - used in dialog */
    private ListView lvDialogListClasses;

    /** TextView that displays the title for the current operation (edit/create user) */
    private TextView tvTitleCRUDUser;

    /** TextView that displays the title of the class block */
    private TextView tvTitleStudentClass;

    /** TextView that displays the caption for the parent/caretaker mail field */
    private TextView tvTitleContactEmail;

    /** TextView that displays the caption for the class name field */
    private TextView tvTitleStudentClassCapt;

    /** TextView that displays the name of the class the student is currently assigned to */
    private TextView tvUserClassName;

    /** TextView that displays the name of the class the student is currently assigned to - used in dialog */
    private TextView tvDialogClassName;

    /** TextView that displays the name of the teacher of class the student is currently assigned to */
    private TextView tvDialogTeacherName;

    /** TextView that displays the email adress of the teacher of class the student is currently assigned to */
    private TextView tvDialogTeacherEmail;

    /** Adapter for displaying roles in the role spinner */
    private ArrayAdapter roleAdapter;

    /** Adapter for displaying users in the userList ListView */
    private SimpleAdapter userListAdapter;

    /** Adapter for displaying classes in the dialogClassList adapter */
    private SimpleAdapter dialogClassListAdapter;

    /** ArrayList for storing roles - used by roleAdapter*/
    private final List<String> roleList = new ArrayList<>();

    /** ArrayList for storing users - used by userListAdapter */
    private final List<Map<String, String>> userList = new ArrayList<>();

    /** ArrayList for storing classes - used by dialogClassListAdapter */
    private final List<Map<String, String>> classList = new ArrayList<>();

    /** String for storing the username of the user currently logged in */
    private String currentUserUsername;

    /** String for storing the user id of the user that has been selected from lvListUsers */
    private String userUserId;

    /** String for storing the class id of the user that has been selected from lvListUsers */
    private String userClassId;

    /** String for storing the role of the user that has been selected from lvListUsers */
    private String userRole;

    /** String for storing the user name of the user that has been selected from lvListUsers */
    private String userUsername;

    /** String for storing the user password of the user that has been selected from lvListUsers */
    private String userPassword;

    /** String for storing the first name of the user that has been selected from lvListUsers */
    private String userFirstName;

    /** String for storing the last name of the user that has been selected from lvListUsers */
    private String userLastName;

    /** String for storing the email address of the user that has been selected from lvListUsers */
    private String userEmail;

    /** String for storing the parent or caretaker email adress of the user that has been selected from lvListUsers */
    private String userParentEmail;

    /** String for storing the id of the class that has been selected from lvDialogListClasses */
    private String classClassId;

    /** String for storing the name of the class that has been selected from lvDialogListClasses */
    private String classClassName;

    /** boolean for setting whether a new user is being created */
    private boolean newUser;

    /** boolean for setting whether changes have been made that might need to be saved */
    private boolean changed;

    /** boolean for setting whether fields are clear */
    private boolean clear;

    /**
     * onCreate sets up the ui elements, populates them and describes what to do on click
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        /** EditText for searching */
        EditText etSearch = (EditText) findViewById(R.id.etSearch);

        tvTitleCRUDUser = (TextView) findViewById(R.id.tvTitleCRUDUser);
        tvTitleStudentClass = (TextView) findViewById(R.id.tvTitleStudentClass);
        tvUserClassName = (TextView) findViewById(R.id.tvUserClassName);
        tvTitleContactEmail = (TextView) findViewById(R.id.tvTitleContactEmail);
        tvTitleStudentClassCapt = (TextView) findViewById(R.id.tvTitleStudentClassCapt);

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

        lvListUsers = (ListView) findViewById(R.id.lvListUsers);

        /** create a new UserInfo to get information on the user currently logged in */
        UserInfo userinfo = new UserInfo(context);
        HashMap<String, String> user = userinfo.getUser();
        Log.d("userinfo content", String.valueOf(user));
        currentUserUsername = user.get("username");

        /** create an adapter that will contain roles */
        roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /** assigns the roleAdapter to spinnerRole */
        spinnerRole.setAdapter(roleAdapter);

        /** make sure to start with all roles in spinnerRole */
        getRoles();

        /**
         * set OnItemSelectedListener to spinnerRole - changes ui elements appearance based on role
         */
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                Log.d("Spinner position", spinnerRole.getSelectedItem().toString());
                /** if teacher - hide UI elements specific for student */
                if (spinnerRole.getSelectedItem().toString().equals("teacher")) {
                    etContactEmail.setVisibility(View.GONE);
                    bAssignClass.setVisibility(View.GONE);
                    tvTitleStudentClass.setVisibility(View.GONE);
                    tvUserClassName.setVisibility(View.GONE);
                    tvTitleContactEmail.setVisibility(View.GONE);
                    tvTitleStudentClassCapt.setVisibility(View.GONE);
                } else if (spinnerRole.getSelectedItem().toString().equals("student")) {
                    etContactEmail.setVisibility(View.VISIBLE);
                    bAssignClass.setVisibility(View.VISIBLE);
                    tvTitleStudentClass.setVisibility(View.VISIBLE);
                    tvUserClassName.setVisibility(View.VISIBLE);
                    tvTitleStudentClassCapt.setVisibility(View.VISIBLE);
                    tvTitleContactEmail.setVisibility(View.VISIBLE);
                }
            }
            /** empty required method for setOnItemSelected */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /** create an adapter that will contain users - displays their username, firstname, lastname and role */
        userListAdapter = new SimpleAdapter(this, userList,
                R.layout.listview_custom_item, new String[]{"username", "firstName", "lastName", "role"},
                new int[]{R.id.clTvRow1, R.id.clTvRow2_1, R.id.clTvRow2_2, R.id.clTvRow3});

        /** assigns the userListAdapter to lvListUsers */
        lvListUsers.setAdapter(userListAdapter);
        /** set adapter to single choice mode */
        lvListUsers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        /** make sure to start with all users in lvListUsers */
        getAllUsers();

        /** make sure activity is started in new user mode */
        setNewUser(true);
        setChanged(false);
        setContentPane(-1);

        /**
         * create an adapter that will contain classes - displays their name, teacher first and last name and number of students
         * used in class assignment dialog
         */
        dialogClassListAdapter = new SimpleAdapter(this, classList,
                R.layout.listview_custom_item,
                new String[]{"className", "teacherFirstName", "teacherLastName", "numOfStudents"},
                new int[]{R.id.clTvRow1, R.id.clTvRow2_1, R.id.clTvRow2_2, R.id.clTvRow3});

        /** make sure to start with all classes in lvListUsers */
        getAllClasses();

        /**
         * set OnItemSelectedListener to lvListUsers
         * define behaviour if editTexts have been edited since last click
         */
        lvListUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListUsers.setItemChecked(position, true);
                view.setSelected(true);
                Log.d("position", String.valueOf(position));
                if (changed) {
                    /** if changes have been made since last save */
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            /** if user indicates changes must be saved */
                            if (dialogResponse) {
                                if (newUser) {
                                    /** if saving using the creation a new user */
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
                                        /** if saving using the updating of an existing user */
                                        clear = true;
                                        setChanged(false);
                                        setNewUser(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            }
                            if (clear) {
                                /** if user indicates changes must be discarded */
                                setContentPane(position);
                            } else {
                                clear = true;
                            }
                        }
                    });
                } else {
                    /** if no changes have been made since last save */
                    setContentPane(position);
                }
            }
        });

        /**
         * set OnItemSelectedListener to bAddUser
         * define behaviour if editTexts have been edited since last click
         * behaviour is the same as above
         */
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
                                        resetAdapter(lvListUsers, userListAdapter);
                                    } else {
                                        clear = false;
                                        setContentPane(-1);
                                        resetAdapter(lvListUsers, userListAdapter);
                                    }
                                }
                            } else {
                                clear = true;
                                setContentPane(-1);
                                resetAdapter(lvListUsers, userListAdapter);
                            }
                        }
                    });
                } else {
                    setContentPane(-1);
                    resetAdapter(lvListUsers, userListAdapter);
                }
            }
        });

        /** call createUser() on click */
        bRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                setChanged(false);
                setNewUser(false);
                bAssignClass.setEnabled(true);
            }
        });

        /** call updateUser() on click */
        bEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
                setChanged(false);
                setNewUser(false);
            }
        });

        /** call deleteUser() on click */
        bDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                setChanged(false);
                setNewUser(true);
            }
        });

        /** build a dialog where classes can be assigned after click */
        bAssignClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classAssignmentDialog();
            }
        });

        /** set addTextChangedListener to etSearch to allow for searching through lvListUser */
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

        /** add an addTextChangedListener to allow for throwing dialogs in case of unsaved changes */
        etUsername.addTextChangedListener(new TextWatcher() {

            /** required empty method */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            /** checks if username entry has been changed but is not empty and is not the same as the currently stored username for this entry */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etUsername.getText().toString();
                if (!content.equals("") && !content.equals(userUsername)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            /** required empty method */
            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        /** add an addTextChangedListener to allow for throwing dialogs in case of unsaved changes */
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

        /** add an addTextChangedListener to allow for throwing dialogs in case of unsaved changes - behaviour as above */
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

        /** add an addTextChangedListener to allow for throwing dialogs in case of unsaved changes - behaviour as above */
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

        /** add an addTextChangedListener to allow for throwing dialogs in case of unsaved changes - behaviour as above */
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

        /** add an addTextChangedListener to allow for throwing dialogs in case of unsaved changes - behaviour as above */
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

    /**
     * sets whether a new user is being created and adapts the UI elements to that
     *
     * @param value the value to set the boolean to
     */
    private void setNewUser(boolean value) {
        newUser = value;
        setEnabledUiItems();
        Log.d("new user value", String.valueOf(newUser));
    }

    /**
     * sets whether a user's details have been changed since last button click and adapts the UI elements to that
     *
     * @param value the value to set the boolean to
     */
    private void setChanged(boolean value) {
        changed = value;
        setEnabledUiItems();
        Log.d("Changed value", String.valueOf(changed));
    }

    /**
     * creates a dialog asking the user is sure about discarding unsaved changes
     *
     * @param callback the callback interface used
     */
    private void confirm(final DialogCallback callback) {
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

    /**
     * launch a RoleTask to fetch all roles from the database and add them to roleList
     */
    private void getRoles() { // To populate the spinner

        new RoleTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> asyncResults) {
                /** execute a new RoleTask  */
                for (Map.Entry<String, HashMap<String, String>> line : asyncResults.entrySet()) {
                    for (Map.Entry<String, String> role : line.getValue().entrySet()) {
                        String roleName = role.getValue();
                        roleList.add(roleName);
                    }
                }
                roleAdapter.notifyDataSetChanged();
            }
        }, context).execute(); //roleID
    }

    /**
     * launch a ClassTask to fetch all classes from the database and add them to classList
     */
    private void getAllClasses() {
        new ClassTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                /** execute a new ClassTask with method FETCH  */
                classList.clear();
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    /** get data from the returned HashMap and add it to ArrayList classList that can be used by classListAdapter */
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    Log.d("teacherFirstName", teacherFirstName);
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    Log.d("teacherFirstName", teacherLastName);
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    String numOfStudents = classData.getValue().get("numOfStudents");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    classInfo.put("numOfStudents", "number of students: " + numOfStudents);
                    Log.d("getAllClasses result", String.valueOf(classInfo));
                    classList.add(classInfo);
                }
                dialogClassListAdapter.notifyDataSetChanged();
                Log.d("dialogClassListAdapter", "successfully updated");
            }
        }, context).executeTask("FETCH", "", "", "");
    }

    /**
     * launch a UserTask to fetch all users from the database and add them to userList
     *
     * procedure same as above
     */
    private void getAllUsers() {
        new UserTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> users) {
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
        }, context).executeTask("FETCH", "", "", "", "", "", "", "", "", ""); //Nothing within "" to get every user - see php script
    }

    /**
     * determine the role of the user to be created, get the contents of relevant EditTexts
     * and feed them to a new UserTask
     *
     * @return true if a new user has been created, else false
     */
    private boolean createUser() {
        String role = spinnerRole.getSelectedItem().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString(); //bfk
        String lastName = etLastName.getText().toString();
        String firstName = etFirstName.getText().toString();
        String email = etEmail.getText().toString();
        String parentEmail = etContactEmail.getText().toString();

        /** if true a user can be created */
        boolean general = false;

        /** if true a student can be created */
        boolean student = false;

        if (!username.equals("") && !password.equals("") && !firstName.equals("") && !lastName.equals("")
                && !email.equals("")) {
            general = true;
        }
        if (general && !parentEmail.equals("")) {
            student = true;
        }
        if (role.equals("student") && student) {
            /** start a new UserTask to create a student - get the id of the newly created user */
            new UserTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> users) {
                    for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                        userUserId = user.getValue().get("lastUserId");
                    }
                }
            }, context).executeTask("CREATE", role, "", "", username, password, lastName, firstName, // bfk
                    email, parentEmail);
            /** get all users and reset adapter to reset selection to zero */
            getAllUsers();
            resetAdapter(lvListUsers, userListAdapter);
            return true;
        } else if (!role.equals("student") && general) {
            /** start a new UserTask to create a teacher - get the id of the newly created user */
            new UserTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> users) {
                    for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                        userUserId = user.getValue().get("lastUserId");
                    }
                }
            }, context).executeTask("CREATE", role, "", "", username, password, lastName, firstName,
                    email, "");
            /** get all users and reset adapter to reset selection to zero */
            getAllUsers();
            resetAdapter(lvListUsers, userListAdapter);
            /** boolean used by lvListUsers onItemClickListener */
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            /** boolean used by lvListUsers onItemClickListener */
            return false;
        }
    }

    /**
     * get the content of relevant EditTexts and update a user with this content
     *
     * @return true if the user has been updated
     */
    private boolean updateUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String parentEmail = etContactEmail.getText().toString();

        new UserTask(new Callback() {
            /** start a new UserTask to update the student using method UPDATE */
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> users) {
            }
        }, context).execute("UPDATE", "", userUserId, "", "", "", username, password, lastName, firstName,
                email, parentEmail);
        /** get all users and reset adapter to reset selection to zero */
        getAllUsers();
        resetAdapter(lvListUsers, userListAdapter);
        /** boolean used by lvListUsers onItemClickListener */
        return true;
    }

    /**
     * updates or assigns the class a student is in through a new ClassTask
     *
     * @param userId the id of the user to be assigned a (new) class
     * @param classId the class id the student is to be assigned to
     */
    private void updateUserClass(String userId, String classId) {
        new UserTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> users) {
            }
        }, context).execute("UPDATE", "", userId, "", "", classId, "", "", "", "",
                "", "");
        getAllClasses();
        resetAdapter(lvDialogListClasses, dialogClassListAdapter);
    }

    /**
     * deletes a user from the database
     * checks if the user to be deleted is not the one currently logged in,
     * else throws a dialog alert for confirmation
     */
    private void deleteUser() {
        if(userUsername.equals(currentUserUsername)){
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "You cannot delete the user currently logged in!";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        } else {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete user " + userUsername)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new UserTask(new Callback() {
                                /** create a new UserTask to delete the user using method DELETE */
                                @Override
                                public void asyncDone(HashMap<String, HashMap<String, String>> users) {
                                }
                            }, context).executeTask("DELETE", userRole, userUserId, "", "", "", "", "", "", "");
                            /** clear all EditTexts */
                            getAllUsers();
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
            /** get all users and reset adapter to reset selection to zero */
            getAllUsers();
            resetAdapter(lvListUsers, userListAdapter);
        }
    }

    /**
     * sets the UI items to be enabled based on whether a new user is being created and
     * user information has been changed
     */
    private void setEnabledUiItems() {
        if (newUser) {
            /** if a new user is being created */
            tvTitleCRUDUser.setText(R.string.registerUser);
            bRegisterUser.setVisibility(View.VISIBLE);
            bEditUser.setVisibility(View.GONE);
            bDeleteUser.setVisibility(View.GONE);
            bAssignClass.setEnabled(false);
            if (changed) {
                /** enable register button if changes have been made else disable */
                bEditUser.setEnabled(false);
                bDeleteUser.setEnabled(false);
                bRegisterUser.setEnabled(true);
            } else {
                bEditUser.setEnabled(false);
                bDeleteUser.setEnabled(false);
                bRegisterUser.setEnabled(false);
            }
        } else {
            /** else a user is being updated */
            tvTitleCRUDUser.setText("Edit user " + userUsername);
            bEditUser.setVisibility(View.VISIBLE);
            bDeleteUser.setVisibility(View.VISIBLE);
            bRegisterUser.setVisibility(View.GONE);
            if (changed) {
                /** enable save button if changes have been made else disable */
                bEditUser.setEnabled(true);
                bDeleteUser.setEnabled(true);
                bRegisterUser.setEnabled(false);
                bAssignClass.setEnabled(false);
            } else {
                bEditUser.setEnabled(false);
                bDeleteUser.setEnabled(true);
                bRegisterUser.setEnabled(false);
                bAssignClass.setEnabled(true);
            }
        }
    }

    /**
     * fills the user EditTexts based on the entry selected in lvListUsers
     * empties the EditTexts if no entry is selected
     *
     * @param position the position of the user clicked in lvListUsers
     */
    private void setContentPane(int position) {
        if (position >= 0) {
            /** if a user has been selected */
            Map<String, String> userData = (Map) userListAdapter.getItem(position);
            userUserId = userData.get("userId");
            userClassId = userData.get("classId");
            userRole = userData.get("role");
            userUsername = userData.get("username");
            userPassword = userData.get("password");
            userFirstName = userData.get("firstName");
            userLastName = userData.get("lastName");
            userEmail = userData.get("email");
            userParentEmail = userData.get("parentEmail");
            String userClassName = getClassName(userUserId, userList);
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
            /** else clear fields and Strings */
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
            spinnerRole.setEnabled(true);
            setChanged(false);
            setNewUser(true);
        }
    }

    /**
     * Get the class name for the class a student is currently enrolled in
     *
     * @param userId the user id of the user we want to get the associated class name for
     * @param userList the list we want to loop through
     * @return the name of the class the user is enrolled in, if not a student: null
     * if a student but not enrolled in a class: String userNotInClass
     */
    private String getClassName(String userId, List<Map<String, String>> userList) {
        String userNotInClass = "Not enrolled in class";
        if ("teacher".equals(userRole)) {
            Log.d("getClassName result", String.valueOf((Object) null));
            return null;
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

    /**
     * get the index of the item currently selected in a spinner
     *
     * @param spinner the spinner we want to get an index from
     * @param myString the entry we want to find
     * @return the index at which myString was found
     */
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * create a dialog window for assigning classes to students
     */
    private void classAssignmentDialog() {

        /** create a new alertDialog builder and inflate it */
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_assign_class, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();


        /** EditText to allow for searching the list of classes */
        EditText etDialogSearch = (EditText) layout.findViewById(R.id.etDialogSearch);

        /** TextView that will hold the title for this dialog */
        TextView tvDialogClassTitle = (TextView) layout.findViewById(R.id.tvDialogClassTitle);

        /** Button to assign a student to a class */
        Button bDialogAssignClass = (Button) layout.findViewById(R.id.bDialogAssignClass);

        /** Button to cancel the assignment process */
        Button bDialogCancel = (Button) layout.findViewById(R.id.bDialogCancel);

        tvDialogClassName = (TextView) layout.findViewById(R.id.tvDialogClassName);
        tvDialogTeacherName = (TextView) layout.findViewById(R.id.tvDialogTeacherName);
        tvDialogTeacherEmail = (TextView) layout.findViewById(R.id.tvDialogTeacherEmail);
        lvDialogListClasses = (ListView) layout.findViewById(R.id.lvDialogListClasses);

        /** Assign dialogClassListAdapter to lvDialogListClasses */
        lvDialogListClasses.setAdapter(dialogClassListAdapter);
        lvDialogListClasses.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        getAllClasses();

        /** set OnItemClickListener to lvDialogListClasses */
        lvDialogListClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListUsers.setItemChecked(position, true);
                view.setSelected(true);
                Log.d("position lvDLC", String.valueOf(position));
                setDialogContentPane(position);
            }
        });

        /** set addTextChangedListener to etDialogSearch to allow for searching through lvDialogListClasses */
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

        /** closes the dialog if clicked */
        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        /** calls updateUserClass() if clicked */
        bDialogAssignClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserClass(userUserId, classClassId);
                tvUserClassName.setText(classClassName);
                dialog.dismiss();
            }
        });

        setDialogContentPane(getClassListPosition(userClassId, classList));
    }

    /**
     * get the index of a classId ArrayList<Map<String, String>> entry
     * based on its id
     *
     * @param classId the id of the class we want to know the index of
     * @param classList the list we look through
     * @return the index at which classId was found, if none return -1
     */
    private int getClassListPosition(String classId, List<Map<String, String>> classList) {
        for (int i = 0; i < classList.size(); i++) {
            Map<String, String> map = classList.get(i);
            if (map.get("classId").equals(classId)) {
                Log.d("GetClassListPos result", String.valueOf(i));
                return i;
            }
        }
        Log.d("GetClassListPos result", String.valueOf(-1));
        return -1;
    }

    /**
     * fills the user EditTexts based on the entry selected in lvDialogListClasses
     * empties the EditTexts if no entry is selected
     *
     * @param position the position of the currently selected item in lvDialogListClasses
     */
    private void setDialogContentPane(int position) {
        if (position >= 0) {
            /** if an item has been selected */
            Map<String, String> classData = (Map) dialogClassListAdapter.getItem(position);
            classClassId = classData.get("classId");
            classClassName = classData.get("className");
            String classTeacherFistName = classData.get("teacherFirstName");
            String classTeacherLastName = classData.get("teacherLastName");
            String classTeacherEmail = classData.get("teacherEmail");
            tvDialogClassName.setText(classClassName);
            tvDialogTeacherName.setText(classTeacherFistName + " " + classTeacherLastName);
            tvDialogTeacherEmail.setText(classTeacherEmail);
        } else {
            /** clear fields if no item has been selected */
            tvDialogClassName.setText("");
            tvDialogTeacherName.setText("");
            tvDialogTeacherEmail.setText("");
        }
    }

    /**
     * re-assign an adapter to a listview
     * hacky method but the only way to clear selections in the list after list data is updated
     *
     * @param lv the ListView to which a new adapter is to be assigned
     * @param sa the adapter that the ListView is to be assigned to
     */
    private void resetAdapter(ListView lv, SimpleAdapter sa) {
        lv.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        lv.setAdapter(sa);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }
}
