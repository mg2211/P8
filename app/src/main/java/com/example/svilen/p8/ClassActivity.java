package com.example.svilen.p8;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassActivity extends AppCompatActivity {

    Context context = this;
    UserInfo userinfo;
    HashMap<String, String> user;
    ListView lvClasses;
    ListView lvStudents;
    TextView tvStudents;
    EditText etSearch;
    SimpleAdapter classAdapter;
    SimpleAdapter studentAdapter;
    List<Map<String, String>> classList = new ArrayList<>();
    List<Map<String, String>> studentList = new ArrayList<>();
    String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        lvClasses = (ListView) findViewById(R.id.lvClasses);
        lvStudents = (ListView) findViewById(R.id.lvStudents);
        tvStudents = (TextView) findViewById(R.id.tvStudents);
        etSearch = (EditText) findViewById(R.id.etSearch);

        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        teacherId = user.get("teacherId");
        getClasses();

        classAdapter = new SimpleAdapter(this, classList,
                android.R.layout.simple_list_item_2,
                new String[] {"Class", "Number of students" },
                new int[] {android.R.id.text1, android.R.id.text2 });
        lvClasses.setAdapter(classAdapter);

        studentAdapter = new SimpleAdapter(this, studentList,
                android.R.layout.simple_list_item_1,
                new String[] {"Name"},
                new int[] {android.R.id.text1});
        lvStudents.setAdapter(studentAdapter);


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO AUTO GENERATED METHOD
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                classAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO AUTO GENERATED METHOD
            }
        });



        lvClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> classData = classList.get(position);
                String classId = classData.get("ClassId");
                String className = classData.get("Class");
                tvStudents.setText("Students in class " + className);

                new StudentTask(new StudentCallback() {
                    @Override
                    public void studentListDone(HashMap<String, HashMap<String, String>> students) {
                        if (!studentList.isEmpty()) {
                            studentList.clear();
                        }
                        for (Map.Entry<String, HashMap<String, String>> student : students.entrySet()) {
                            Map<String, String> studentInfo = new HashMap<>();
                            String studentName = student.getValue().get("lastname") + ", " + student.getValue().get("firstname");
                            studentInfo.put("Name", studentName);
                            studentInfo.put("Class", student.getValue().get("classId"));
                            studentList.add(studentInfo);
                        }
                        studentAdapter.notifyDataSetChanged();
                    }
                }, context).execute(classId);
            }
        });

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String studentname = studentList.get(position).get("Name");
                String studentClass = studentList.get(position).get("Class");

                Log.d("Student", studentClass + studentname);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.student_dialog, null);
                TextView text = (TextView) layout.findViewById(R.id.tvStudentClass);
                text.setText(studentClass+studentname);
                builder.setView(layout);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });


    }
    public void getClasses(){
        new ClassTask(new ClassCallback() {
            @Override
            public void classListDone(HashMap<String, HashMap<String, String>> classes) {
                if(!classList.isEmpty()){
                    classList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> classId : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String specificClassname = classId.getValue().get("className");
                    String specificClassStudents = classId.getValue().get("studentsInClass");
                    String specificClassId = classId.getValue().get("classId");
                    classInfo.put("ClassId", specificClassId);
                    classInfo.put("Class", specificClassname);
                    classInfo.put("Number of students", "Number of students: "+ specificClassStudents);
                    classList.add(classInfo);
                }
                classAdapter.notifyDataSetChanged();
            }
        },context).execute(teacherId);
    }
}
