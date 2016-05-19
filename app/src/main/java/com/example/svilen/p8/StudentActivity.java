package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {


    Button bLogout;
    Context context = this;
    ListView lvAssToStudent;
    SimpleAdapter assignmentAdapter;
    AssignmentListAdapter  assignedAdapter;

    List<Map<String, String>> assignmentList = new ArrayList<>();
    UserInfo userinfo;
    HashMap<String, String> user;
    String studentId;
    String textId;
    String assignmentName;
    String specificAssignmentId;
    String assignmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student);
        //getWindow().setBackgroundDrawableResource(R.drawable.green);
        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        Log.d("StudentId:  ", studentId);

        lvAssToStudent = (ListView) findViewById(R.id.lvAssOverview);

      /*  assignmentAdapter = new SimpleAdapter(this, assignmentList,
                android.R.layout.simple_list_item_1,
                new String[]{"assignmentLibName"},
                new int[]{android.R.id.text1});
        lvAssToStudent.setAdapter(assignmentAdapter);*/

        assignedAdapter = new AssignmentListAdapter(this,assignmentList);

        lvAssToStudent.setAdapter(assignedAdapter);



        getAssignment();

        lvAssToStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> assignmentData = assignmentList.get(position);
                textId = assignmentData.get("textId");
                assignmentName = assignmentData.get("assignmentLibName");
                assignmentId = assignmentData.get("assignmentid");
                String isComplete = assignmentData.get("isComplete");

                if(isComplete.equals("0")){
                    Log.d("TEXTID::: ", textId);

                    //add if statement is complete == 0{} else {toast= you have competed that assignment]
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("Do you wish to start " + assignmentName + " homework?")
                            .setTitle(assignmentName)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(StudentActivity.this, ReadingActivity.class);
                                    intent.putExtra("textId", textId);
                                    intent.putExtra("assignmentName", assignmentName);
                                    intent.putExtra("id", assignmentId);
                                    startActivity(intent);
                                    Log.d("8787", assignmentId);

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                }else{
                    Toast.makeText(StudentActivity.this, "You have finished this assignment", Toast.LENGTH_SHORT).show();

                }


                new TextTask(new Callback() {
                    @Override
                    public void asyncDone(HashMap<String, HashMap<String, String>> results) {

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

        new AssignmentTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

                if (!assignmentList.isEmpty()) {
                    assignmentList.clear();
                }

                for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {
                    Map<String, String> assignmentInfo = new HashMap<>();
                    String specificAssignmentName = assignment.getValue().get("assignmentLibName");
                    specificAssignmentId = assignment.getValue().get("assignmentid");
                    String specificAssLibId = assignment.getValue().get("assignmentlibraryid");
                    String specificTextId = assignment.getValue().get("textId");
                    String isComplete = assignment.getValue().get("isComplete");
                    Long availablefrom = Long.valueOf(assignment.getValue().get("availableFrom"));
                    Long availableto = Long.valueOf(assignment.getValue().get("availableTo"));

                    Log.d("1919 ", availablefrom.toString());
                    Log.d("2020 ", availableto.toString());



                        Long tsLong = System.currentTimeMillis() / 1000;
                        String ts = tsLong.toString();
                        Log.d("1818", tsLong.toString());

                        if (tsLong <= availableto && availablefrom <= tsLong) {


                            assignmentInfo.put("assignmentLibName", specificAssignmentName);
                            assignmentInfo.put("Name", specificAssignmentName);
                            assignmentInfo.put("assignmentid", specificAssignmentId);
                            assignmentInfo.put("assignmentlibraryid", specificAssLibId);
                            assignmentInfo.put("textId", specificTextId);
                            assignmentInfo.put("isComplete", isComplete);
                            assignmentInfo.put("availableFrom", assignment.getValue().get("availableFrom"));
                            assignmentInfo.put("availableTo", assignment.getValue().get("availableTo"));

                            assignmentList.add(assignmentInfo);

                            Log.d("2222: ", String.valueOf(assignmentInfo));
                            Log.d("2222", specificAssignmentId);
                        }


                }
            }

        }, context).executeTask("get", studentId, "", "","","");
    }



}


