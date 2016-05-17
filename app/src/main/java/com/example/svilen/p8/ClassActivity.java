package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassActivity extends AppCompatActivity {

    Context context = this;
    UserInfo userinfo;
    HashMap<String, String> user;
    LinearLayout llListStudents;
    ListView lvListClasses, lvListStudents, lvListTeachers;
    TextView tvTitleStudentsInClass, tvTitleAllTeachers, tvTitleCreateClass,
    tvTitleEditClass, tvTeacherName, tvTeacherEmail;
    EditText etSearch, etClassName, etSearchTeacher;
    SimpleAdapter classListAdapter, studentListAdapter, teacherListAdapter;
    Button bAddClass, bShowTeacherClasses, bShowAllClasses, bCreateClass, bEditClass, bDeleteClass;

    List<Map<String, String>> classList = new ArrayList<>();
    List<Map<String, String>> studentList = new ArrayList<>();
    List<Map<String, String>> teacherList = new ArrayList<>();

    String currentUserTeacherId;
    String currentUserFirstName;
    String teacherTeacherId;
    String teacherTeacherFullName;
    String teacherTeacherEmail;
    String currentTeacherFullName;
    String currentTeacherEmail;
    String classClassId;
    String classClassName;

    boolean newClass;
    boolean changed;
    boolean clear;

    int classListPosition;
    int teacherListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        llListStudents = (LinearLayout) findViewById(R.id.llListStudents);

        tvTitleStudentsInClass = (TextView) findViewById(R.id.tvTitleStudentsInClass);
        tvTitleAllTeachers = (TextView) findViewById(R.id.tvTitleAllTeachers);
        tvTitleCreateClass = (TextView) findViewById(R.id.tvTitleCreateClass);
        tvTitleEditClass = (TextView) findViewById(R.id.tvTitleEditClass);
        tvTeacherName = (TextView) findViewById(R.id.tvTeacherName);
        tvTeacherEmail = (TextView) findViewById(R.id.tvDialogTeacherEmail);

        etSearch = (EditText) findViewById(R.id.etSearch);
        etClassName = (EditText) findViewById(R.id.etClassName);
        etSearchTeacher = (EditText) findViewById(R.id.etSearchTeacher);

        lvListClasses = (ListView) findViewById(R.id.lvListClasses);
        lvListStudents = (ListView) findViewById(R.id.lvListStudents);
        lvListTeachers = (ListView) findViewById(R.id.lvListTeachers);

        bAddClass = (Button) findViewById(R.id.bAddClass);
        bShowTeacherClasses = (Button) findViewById(R.id.bShowTeacherClasses);
        bShowAllClasses = (Button) findViewById(R.id.bShowAllClasses);
        bCreateClass = (Button) findViewById(R.id.bCreateClass);
        bEditClass = (Button) findViewById(R.id.bEditClass);
        bDeleteClass = (Button) findViewById(R.id.bDeleteClass);

        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        currentUserTeacherId = user.get("teacherId");
        currentUserFirstName = user.get("userName");
        getTeacherClasses();

        classListPosition = -1;
        teacherListPosition = -1;

        classListAdapter = new SimpleAdapter(this, classList,
                R.layout.listview_class_item,
                new String[]{"className", "teacherFirstName", "teacherLastName", "NumOfStudents"},
                new int[]{R.id.clTvClassName, R.id.clTvTeacherFirstName, R.id.clTvTeacherLastName, R.id.clTvNumberOfStudents});
        lvListClasses.setAdapter(classListAdapter);
        lvListClasses.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        studentListAdapter = new SimpleAdapter(this, studentList,
                android.R.layout.simple_list_item_1,
                new String[]{"fullName"},
                new int[]{android.R.id.text1});
        lvListStudents.setAdapter(studentListAdapter);
        lvListStudents.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        teacherListAdapter = new SimpleAdapter(this, teacherList,
                android.R.layout.simple_list_item_1,
                new String[]{"fullName"},
                new int[]{android.R.id.text1});
        lvListTeachers.setAdapter(teacherListAdapter);
        lvListTeachers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        bShowTeacherClasses.setText("Show " + currentUserFirstName + "'s classes");

        getAllTeachers();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO AUTO GENERATED METHOD
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                classListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO AUTO GENERATED METHOD
            }
        });

        etSearchTeacher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO AUTO GENERATED METHOD
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                teacherListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO AUTO GENERATED METHOD
            }
        });

        lvListClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setContentPane(position, -1);
                classListPosition = position;

                getClassStudents(classClassId);

                tvTitleStudentsInClass.setText("students in class " + classClassName);


                setNewClass(false);
                setChanged(false);

                //tvTitleStudentsInClass.setText("Students in class " + classClassName);

            }
        });

        lvListTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setContentPane(classListPosition, position);
            }
        });

        bAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewClass(true);
                setChanged(false);
                setContentPane(-1, -1);
            }
        });

        bShowTeacherClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTeacherClasses();
            }
        });

        bShowAllClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllClasses();
            }
        });

        bCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass(teacherListPosition);
            }
        });

        bEditClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClass();
            }
        });

        bDeleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClass(classListPosition);
            }
        });

        lvListTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListTeachers.setItemChecked(position, true);
                teacherListPosition = position;
                view.setSelected(true);
                Log.d("position", String.valueOf(position));
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                setChanged(false);
                                if (newClass) {
                                    if (createClass(position)) {
                                        clear = true;
                                        setChanged(false);
                                        setNewClass(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                if (changed) {
                                    if (updateClass()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewClass(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            }
                            if (clear) {
                                setContentPane(classListPosition, position);
                            } else {
                                clear = true;
                            }
                        }
                    });
                } else {
                    setContentPane(classListPosition, position);
                }
            }
        });

        etClassName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etClassName.getText().toString();
                if (!content.equals("") && !content.equals(classClassName)) {
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

        tvTeacherName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = tvTeacherName.getText().toString();
                Log.d("String teacherName", content);
                if (!content.equals(currentTeacherFullName)) {
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

        tvTeacherEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = tvTeacherEmail.getText().toString();
                Log.d("String teacherEmail", content);
                if (!content.equals(currentTeacherEmail)) {
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

    public void setContentPane(int classListPos, int teacherListPos) {
        if (classListPos >= 0 && teacherListPos >= 0) {
            Map<String, String> classData = (Map) classListAdapter.getItem(classListPos);
            classClassId = classData.get("classId");
            classClassName = classData.get("className");

            currentTeacherFullName = classData.get("teacherFullName");
            currentTeacherEmail = classData.get("teacherEmail");

            Map<String, String> teacherData = (Map) teacherListAdapter.getItem(teacherListPos);
            teacherTeacherId = teacherData.get("teacherId");
            teacherTeacherFullName = teacherData.get("fullName");
            teacherTeacherEmail = teacherData.get("email");

            etClassName.setText(classClassName);
            tvTeacherName.setText(teacherTeacherFullName);
            tvTeacherEmail.setText(teacherTeacherEmail);

        } else if (classListPos >= 0) {
            Map<String, String> classData = (Map) classListAdapter.getItem(classListPos);
            classClassId = classData.get("classId");
            classClassName = classData.get("className");

            currentTeacherFullName = classData.get("teacherFullName");
            currentTeacherEmail = classData.get("teacherEmail");

            teacherTeacherId = classData.get("teacherId");
            teacherTeacherFullName = (classData.get("teacherFirstName") + " " + classData.get("teacherLastName"));
            teacherTeacherEmail = classData.get("teacherEmail");

            etClassName.setText(classClassName);
            tvTeacherName.setText(teacherTeacherFullName);
            tvTeacherEmail.setText(teacherTeacherEmail);
        } else if (teacherListPos >= 0) {
            Map<String, String> userData = (Map) teacherListAdapter.getItem(teacherListPos);
            teacherTeacherId = userData.get("teacherId");
            teacherTeacherFullName = userData.get("fullName");
            teacherTeacherEmail = userData.get("email");

            etClassName.setText("");
            tvTeacherName.setText(teacherTeacherFullName);
            tvTeacherEmail.setText(teacherTeacherEmail);
        } else {
            classClassName = null;

            teacherTeacherId = null;
            teacherTeacherFullName = null;
            teacherTeacherEmail = null;

            currentTeacherFullName = null;
            currentTeacherEmail = null;

            etClassName.setText("");
            tvTeacherName.setText("");
            tvTeacherEmail.setText("");
        }
    }

    public int getTeacherPosition(String teacherId){
        for(int i = 0; i < teacherList.size(); i++){
            Map<String, String> map = teacherList.get(i);
            if(map.get("teacherId").equals(teacherId)){
                return i;
            }
        }
        return -1;
    }

    public boolean createClass(int teacherListPos) {
        Map<String, String> teacherData = (Map) teacherListAdapter.getItem(teacherListPos);
        String teacherId = teacherData.get("teacherId");
        String className = etClassName.getText().toString();
        if (!teacherId.equals("") && !className.equals("")) {
            new ClassTaskNew(new ClassCallbackNew() {
                @Override
                public void classListDone(Map<String, HashMap<String, String>> classes) {
                    for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                        classClassId = classData.getValue().get("lastClassId");
                        Log.d("lastClassId value", classData.getValue().get("lastClassId"));
                    }
                    classListAdapter.notifyDataSetChanged();
                    Log.d("dialogClassListAdapter", "successfully updated");
                }
            }, context).executeTask("CREATE", "", teacherId, className, "", "");
            getAllClasses();
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
        return false;
    }

    public boolean updateClass() {
       String className = etClassName.getText().toString();
        new ClassTaskNew(new ClassCallbackNew() {
            @Override
            public void classListDone(Map<String, HashMap<String, String>> classes) {
                classListAdapter.notifyDataSetChanged();
                Log.d("classListAdapter", "successfully updated");
            }
        }, context).executeTask("UPDATE", classClassId, teacherTeacherId, className, "", "");
        classClassName = className;
        getAllClasses();
        return true;
    }

    public void deleteClass(int classListPosition) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete class " + classClassName + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ClassTaskNew(new ClassCallbackNew() {
                            @Override
                            public void classListDone(Map<String, HashMap<String, String>> users) {
                            }
                        }, context).executeTask("DELETE", classClassId, "", "", "", "");
                        getAllClasses();
                        etClassName.setText("");
                        tvTeacherName.setText("");
                        tvTeacherEmail.setText("");
                        Log.d("classId", classClassId);
                        Log.d("classname", classClassName);
                        setNewClass(true);
                        setChanged(false);
                        bDeleteClass.setEnabled(false);
                        classClassName = null;
                        classClassId = null;
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    public void getTeacherClasses() {
        new ClassTaskNew(new ClassCallbackNew() {
            @Override
            public void classListDone(Map<String, HashMap<String, String>> classes) {
                if (!classList.isEmpty()) {
                    classList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    String numOfStudents = classData.getValue().get("numOfStudents");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherFullName", teacherFirstName + " " + teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    classInfo.put("NumOfStudents", "Number of students: "+ numOfStudents);
                    Log.d("getAllClasses result", String.valueOf(classInfo));
                    classList.add(classInfo);
                }
                classListAdapter.notifyDataSetChanged();
                Log.d("classListAdapter", "successfully updated");
            }
        }, context).executeTask("FETCH", "", currentUserTeacherId, "", "", "");
    }

    public void setEnabledUiItems() {
        if (newClass) {
            tvTitleCreateClass.setVisibility(View.VISIBLE);
            bCreateClass.setVisibility(View.VISIBLE);
            tvTitleEditClass.setVisibility(View.GONE);
            bEditClass.setVisibility(View.GONE);
            bDeleteClass.setVisibility(View.GONE);
            if (changed) {
                bEditClass.setEnabled(false);
                bDeleteClass.setEnabled(false);
                bCreateClass.setEnabled(true);
            } else {
                bEditClass.setEnabled(false);
                bDeleteClass.setEnabled(false);
                bCreateClass.setEnabled(false);
            }
        } else {
            tvTitleEditClass.setVisibility(View.VISIBLE);
            bEditClass.setVisibility(View.VISIBLE);
            bDeleteClass.setVisibility(View.VISIBLE);
            tvTitleCreateClass.setVisibility(View.GONE);
            bCreateClass.setVisibility(View.GONE);
            if (changed) {
                bEditClass.setEnabled(true);
                bDeleteClass.setEnabled(true);
                bCreateClass.setEnabled(false);
            } else {
                bEditClass.setEnabled(false);
                bDeleteClass.setEnabled(true);
                bCreateClass.setEnabled(false);
            }
        }
    }

    public void getAllClasses() {
        new ClassTaskNew(new ClassCallbackNew() {
            @Override
            public void classListDone(Map<String, HashMap<String, String>> classes) {
                if (!classList.isEmpty()) {
                    classList.clear();
                }
                classListAdapter.notifyDataSetChanged();
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    String numOfStudents = classData.getValue().get("numOfStudents");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    classInfo.put("NumOfStudents", "Number of students: "+ numOfStudents);
                    Log.d("getAllClasses result", String.valueOf(classInfo));
                    classList.add(classInfo);
                }
                classListAdapter.notifyDataSetChanged();
                Log.d("dialogClassListAdapter", "successfully updated");
            }
        }, context).executeTask("FETCH", "", "", "", "", "");
    }

    public void setNewClass(boolean value) {
        newClass = value;
        setEnabledUiItems();
        Log.d("new class value", String.valueOf(newClass));
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
                .setMessage("You have unsaved changes - save before continuing?")
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

    private void getClassStudents(String classId){
        new StudentTask(new StudentCallback() {
            @Override
            public void studentListDone(HashMap<String, HashMap<String, String>> students) {
                if (!studentList.isEmpty()) {
                    studentList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> student : students.entrySet()) {
                    Map<String, String> studentInfo = new HashMap<>();
                    String studentId = student.getValue().get("studentId");
                    String studentName = student.getValue().get("lastName") + ", " + student.getValue().get("firstName");
                    studentInfo.put("studentId",studentId);
                    studentInfo.put("fullName", studentName);
                    studentList.add(studentInfo);
                }
                if (studentList.isEmpty()){
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("fullName", "no students in this class");
                    studentList.add(userInfo);
                }
                studentListAdapter.notifyDataSetChanged();
            }
        }, context).execute(classId, currentUserTeacherId);
    }

    /*
    public void getClassStudents(String classId){
        new UserTask(new UserCallback() {
            @Override
            public void userTaskDone(Map<String, HashMap<String, String>> users) {
                if (!studentList.isEmpty()) {
                    studentList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                    Map<String, String> userInfo = new HashMap<>();
                    String userId = user.getValue().get("userId");
                    String studentId = user.getValue().get("studentId");
                    String firstName = user.getValue().get("firstName");
                    String lastName = user.getValue().get("lastName");
                    String fullName = (firstName + " " + lastName);
                    userInfo.put("userId", userId);
                    userInfo.put("studentId", studentId);
                    userInfo.put("firstName", lastName);
                    userInfo.put("lastName", lastName);
                    userInfo.put("fullName", fullName);
                    Log.d("full student name", fullName);
                    studentList.add(userInfo);
                }
                if (studentList.isEmpty()){
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("fullName", "no students in this class");
                    studentList.add(userInfo);
                }
                studentListAdapter.notifyDataSetChanged();
            }
        }, context).execute("FETCH", "", "", "", "", classId, "", "", "", "", "", "");
    }
    */

    public void getAllTeachers(){
        new UserTask(new UserCallback() {
            @Override
            public void userTaskDone(Map<String, HashMap<String, String>> users) {
                if (!teacherList.isEmpty()) {
                    teacherList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                    Map<String, String> userInfo = new HashMap<>();
                    String userId = user.getValue().get("userId");
                    String teacherId = user.getValue().get("teacherId");
                    String firstName = user.getValue().get("firstName");
                    String lastName = user.getValue().get("lastName");
                    String fullName = (firstName + " " + lastName);
                    String email = user.getValue().get("email");
                    userInfo.put("userId", userId);
                    userInfo.put("teacherId", teacherId);
                    userInfo.put("firstName", lastName);
                    userInfo.put("lastName", lastName);
                    userInfo.put("fullName", fullName);
                    userInfo.put("email", email);
                    Log.d("full teacher name", fullName);
                    teacherList.add(userInfo);
                }
                teacherListAdapter.notifyDataSetChanged();
            }
        }, context).execute("FETCH", "teacher", "", "", "", "", "", "", "", "", "", "");
    }
}