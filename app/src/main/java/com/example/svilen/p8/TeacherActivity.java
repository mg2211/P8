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

public class TeacherActivity extends AppCompatActivity {

    Button bRegisterUser;
    Button bLogout;
    List<Map<String, String>> classList = new ArrayList<>();
    ListView classListView;
    SimpleAdapter classAdapter;
    UserInfo userInfo;
    HashMap<String, String> user;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        classListView = (ListView) findViewById(R.id.classListView);
        userInfo = new UserInfo(context);
        user = userInfo.getUser();
        String teacherID = user.get("teacherId");


        classAdapter = new SimpleAdapter(this, classList,
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
                classIntent.putExtra("className",classData.get("Class"));
                startActivity(classIntent);
            }
        });

        new ClassTask(new ClassCallback() {
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
                classAdapter.notifyDataSetChanged();
            }
        }, context).execute(teacherID);


        bRegisterUser = (Button) findViewById(R.id.bRegisterUser);
        bRegisterUser.setOnClickListener(new View.OnClickListener() {
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
                userInfo.logOut();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.d("", "Back button pressed - disabled");
    }
}

