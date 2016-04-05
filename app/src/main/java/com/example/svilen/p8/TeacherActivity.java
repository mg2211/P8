package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TeacherActivity extends AppCompatActivity implements Callback {

    Button bRegisterUser;
    Button bShowClasses;
    Button bShowStudents;
    Button bLogout;
    List<Map<String, String>> classList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        final ServerRequests serverRequests = new ServerRequests(this);
        final ListView classListView = (ListView) findViewById(R.id.classListView);
        final Context context = this;

        final SimpleAdapter classAdapter = new SimpleAdapter(this, classList,
                android.R.layout.simple_list_item_2,
                new String[] {"Class", "Number of students" },
                new int[] {android.R.id.text1, android.R.id.text2 });
        classListView.setAdapter(classAdapter);

        classListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> classData = classList.get(position);
                String classId = classData.get("ClassId");
                Intent classIntent = new Intent(context, ClassActivity.class);
                classIntent.putExtra("classId",classId);
                startActivity(classIntent);
            }
        });

        bShowStudents = (Button) findViewById(R.id.bShowStudents);
        bShowStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classId = "1";
                serverRequests.studentListExecute(classId);
            }
        });

        bRegisterUser = (Button) findViewById(R.id.bRegisterUser);
        bRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, Register.class);
                startActivity(intent);
            }
        });

        bShowClasses = (Button) findViewById(R.id.bShowClasses);
        bShowClasses.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View w) {
                                                UserInfo userInfo = new UserInfo(getApplicationContext());
                                                HashMap<String, String> user = userInfo.getUser();
                                                String teacherID = user.get("teacherId");

                                                new ClassTask(new Callback() {
                                                    @Override
                                                    public void classListDone(HashMap<String, HashMap<String, String>> classes) {
                                                        if(!classList.isEmpty()){
                                                            classList.clear();
                                                        }
                                                        Log.d("classes from async",classes.toString());
                                                        for (Entry<String, HashMap<String, String>> classId : classes.entrySet()) {
                                                            Map<String, String> classInfo = new HashMap<>();
                                                            String specificClassname = classId.getValue().get("className");
                                                            String specificClassStudents = classId.getValue().get("studentsInClass");
                                                            String specificClassId = classId.getValue().get("classId");
                                                            classInfo.put("ClassId", specificClassId);
                                                            classInfo.put("Class", specificClassname);
                                                            classInfo.put("Number of students", "Number of students: "+ specificClassStudents);
                                                            classList.add(classInfo);
                                                        }
                                                        Log.d("classlist", classList.toString());
                                                        classAdapter.notifyDataSetChanged();
                                                    }
                                                }, context).execute(teacherID);
                                            }
                                        });

        bShowStudents = (Button) findViewById(R.id.bShowStudents);
        bShowStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, Register.class);
                startActivity(intent);
            }
        });

        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userinfo = new UserInfo(getApplicationContext());
                userinfo.logOut();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Log.d("", "Back button pressed - disabled");
    }

    @Override
    public void classListDone(HashMap<String, HashMap<String, String>> output) {
    }
}

