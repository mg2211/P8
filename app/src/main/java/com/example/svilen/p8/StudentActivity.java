package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {


    Button bLogout;
    Context context = this;
    ListView lvAssToStudent;
    SimpleAdapter assignmentAdapter;
    List<Map<String, String>> assignmentList = new ArrayList<>();
    UserInfo userinfo;
    HashMap<String, String> user;
    String studentId;
    String textId;
    String assignmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);


        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        Log.d("StudentId:  ", studentId);

        lvAssToStudent = (ListView) findViewById(R.id.lvAssOverview);

        assignmentAdapter = new SimpleAdapter(this, assignmentList,
                android.R.layout.simple_list_item_1,
                new String[]{"assignmentName"},
                new int[]{android.R.id.text1});
        lvAssToStudent.setAdapter(assignmentAdapter);
        getAssignment();

        lvAssToStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> assignmentData = assignmentList.get(position);
                textId = assignmentData.get("textId");
                assignmentName = assignmentData.get("assignmentName");
                Log.d("TEXTID::: ", textId);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Do you wish to start " + assignmentName + " homework?")
                        .setTitle(assignmentName)
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(StudentActivity.this, ReadingActivity.class);
                        intent.putExtra("textId", textId);
                        intent.putExtra("assignmentName", assignmentName);
                        startActivity(intent);

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();



                new TextTask(new TextCallback() {
                    @Override
                    public void TextCallBack(HashMap<String, HashMap<String, String>> results) {

                        //remove other hashmaps in results var to avoid the first returning null

                        // remove progressDial if possible
                        for (Map.Entry<String, HashMap<String, String>> text : results.entrySet()) {
                            Map<String, String> textInfo = new HashMap<>();
                            String textContent = text.getValue().get("textcontent");
                            String textName = text.getValue().get("textname");

                            textInfo.put("textcontent", textContent);
                            textInfo.put("textname", textName);
                            // assignmentList.add(textInfo);
                            Log.d("TEXTTASK", String.valueOf(textInfo));


                        }
                    }
                }, context).executeTask("get", textId, "", "", 0);

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

        Log.d("Back button pressed", " -Disabled");

    }


    public void getAssignment() {

        Log.d("HALLO", "HALLO");
        new AssignmentTask(new AssignmentCallback() {
            @Override
            public void assignmentDone(HashMap<String, HashMap<String, String>> assignments) {

                Log.d("PRUFA", "PRUFA");
                if (!assignmentList.isEmpty()) {
                    assignmentList.clear();
                }

                for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {
                    Map<String, String> assignmentInfo = new HashMap<>();
                    String specificAssignmentName = assignment.getValue().get("assignmentName");
                    String specificAssignmentId = assignment.getValue().get("id");
                    String specificAssLibId = assignment.getValue().get("assignmentlibraryid");
                    String specificTextId = assignment.getValue().get("textId");

                    assignmentInfo.put("assignmentName", specificAssignmentName);
                    assignmentInfo.put("id", specificAssignmentId);
                    assignmentInfo.put("assignmentlibraryid", specificAssLibId);
                    assignmentInfo.put("textId", specificTextId);

                    assignmentList.add(assignmentInfo);

                    Log.d("TESTING111: ", String.valueOf(assignmentInfo));

                }

            }
        }, context).executeTask("get", studentId, "");
    }



}


