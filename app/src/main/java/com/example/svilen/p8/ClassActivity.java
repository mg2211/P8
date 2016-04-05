package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClassActivity extends AppCompatActivity {
    Context context = this;
    List<Map<String, String>> studentList = new ArrayList<>();
    SimpleAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        TextView classLabel = (TextView) findViewById(R.id.classLabel);
        ListView studentListView = (ListView) findViewById(R.id.studentListView);

        studentAdapter = new SimpleAdapter(this, studentList,
                android.R.layout.simple_list_item_1,
                new String[] {"Name"},
                new int[] {android.R.id.text1});
        studentListView.setAdapter(studentAdapter);



        Intent intent = getIntent();
        String classID = intent.getStringExtra("classId");
        String className = intent.getStringExtra("className");

        new StudentTask(new StudentCallback() {
            @Override
            public void studentListDone(HashMap<String, HashMap<String, String>> students) {
                for(Entry<String, HashMap<String, String>> student : students.entrySet()){

                    Map<String, String> studentInfo = new HashMap<>();
                    String studentName = student.getValue().get("lastname")+", "+student.getValue().get("firstname");
                    studentInfo.put("Name",studentName);
                    studentList.add(studentInfo);
                }
                studentAdapter.notifyDataSetChanged();
            }
        }, context).execute(classID);

        classLabel.setText("Showing students in class " + className);
    }
}
