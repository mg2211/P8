package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
List Classes
List Students
List Assignments
Add button to switch to student in User
 */

public class ClassActivity extends AppCompatActivity {

    Context context = this;
    UserInfo userinfo;
    HashMap<String, String> user;
    ListView lvListClasses;
    ListView lvListStudents;
    ListView lvListAssignments;
    TextView tvStudents;
    TextView tvAssignments;
    EditText etSearch;
    SimpleAdapter classListAdapter;
    SimpleAdapter studentListAdapter;
    SimpleAdapter studentAssignmentListAdapter;
    List<Map<String, String>> classList = new ArrayList<>();
    List<Map<String, String>> studentList = new ArrayList<>();
    List<Map<String, String>> assignmentList = new ArrayList<>();
    String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        lvListClasses = (ListView) findViewById(R.id.lvClasses);
        lvListStudents = (ListView) findViewById(R.id.lvStudents);
        lvListAssignments = (ListView) findViewById(R.id.lvAssignments);
        tvStudents = (TextView) findViewById(R.id.tvStudents);
        tvAssignments = (TextView) findViewById(R.id.tvAssignments);
        etSearch = (EditText) findViewById(R.id.etSearch);

        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        teacherId = user.get("teacherId");
        getTeacherClasses();

        classListAdapter = new SimpleAdapter(this, classList,
                R.layout.listview_class_item,
                new String[]{"className", "teacherFirstName", "teacherLastName", "NumOfStudents"},
                new int[]{R.id.clTvClassName, R.id.clTvFirstName, R.id.clTvLastName, R.id.clTvNumberOfStudents});
        lvListClasses.setAdapter(classListAdapter);
        lvListClasses.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        studentListAdapter = new SimpleAdapter(this, studentList,
                android.R.layout.simple_list_item_1,
                new String[]{"studentName"},
                new int[]{android.R.id.text1});
        lvListStudents.setAdapter(studentListAdapter);
        lvListStudents.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        studentAssignmentListAdapter = new SimpleAdapter(this, assignmentList,
                android.R.layout.simple_list_item_1,
                new String[]{"assignmentName"},
                new int[]{android.R.id.text1});
        lvListStudents.setAdapter(studentListAdapter);
        lvListStudents.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

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

        lvListClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> classData = (Map) classListAdapter.getItem(position);
                String classId = classData.get("classId");
                String className = classData.get("className");
                tvStudents.setText("Students in class " + className);

                new UserTask(new UserCallback() {
                    @Override
                    public void userTaskDone(Map<String, HashMap<String, String>> users) {
                        if (!studentList.isEmpty()) {
                            studentList.clear();
                        }
                        for (Map.Entry<String, HashMap<String, String>> student : users.entrySet()) {
                            Map<String, String> studentInfo = new HashMap<>();
                            String studentId = student.getValue().get("studentId");
                            studentInfo.put("studentId", studentId);
                            String studentName = student.getValue().get("firstName") + " " + student.getValue().get("lastName");
                            studentInfo.put("studentName", studentName);
                            Log.d("studentName", studentName);
                            studentInfo.put("classId", student.getValue().get("classId"));
                            studentList.add(studentInfo);
                        }
                        studentListAdapter.notifyDataSetChanged();
                    }
                }, context).execute("FETCH", "", "", "", "", classId, "", "", "", "", "", "");
            }
        });

        lvListStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String studentname = studentList.get(position).get("Name");
                String studentClass = studentList.get(position).get("Class");
                String studentId = studentList.get(position).get("StudentId");

                new AssignmentTask(new AssignmentCallback() {
                    @Override
                    public void assignmentDone(HashMap<String, HashMap<String, String>> assignments) {
                        classList.clear();
                        for (Map.Entry<String, HashMap<String, String>> classData : assignments.entrySet()) {
                            Map<String, String> assignmentInfo = new HashMap<>();
                            String assignmentId = classData.getValue().get("id");
                            String assignmentName = classData.getValue().get("assignmentName");
                            String textId = classData.getValue().get("textId");
                            String studentId = classData.getValue().get("studentId");
                            String from = classData.getValue().get("from");
                            String to = classData.getValue().get("to");
                            String isComplete = classData.getValue().get("isComplete");
                            assignmentInfo.put("assignmentId", assignmentId);
                            assignmentInfo.put("assignmentName", assignmentName);
                            assignmentInfo.put("textId", textId);
                            assignmentInfo.put("studentId", studentId);
                            assignmentInfo.put("availableFrom", from);
                            assignmentInfo.put("availableTo", to);
                            assignmentInfo.put("isComplete", isComplete);
                            assignmentList.add(assignmentInfo);
                        }
                        classListAdapter.notifyDataSetChanged();
                        Log.d("ListAssignmentsAdapter", "successfully updated");
                    }
                }, context).executeTask("FETCH", studentId, "", "", "", "");
            }
        });

        lvListAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                //View layout = inflater.inflate(R.layout.student_dialog, null);
                //TextView text = (TextView) layout.findViewById(R.id.tvStudentClass);
                //builder.setView(layout);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
    }

        public void getTeacherClasses() {
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
        }, context).executeTask("FETCH", "", teacherId, "", "", "");
    }
}
