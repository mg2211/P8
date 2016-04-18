package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentActivity extends AppCompatActivity {

    Button bGetText;
    SimpleAdapter textAdapter;
    List<Map<String, String>> textList = new ArrayList<>();
    Context context = this;
    ListView lvTextToAss;
    TextView tvTextChosen;
    TextView tvTextId;
    EditText etAssName;
    ListView lvAssignments;
    Button bTeacher;
    List<Map<String, String>> assignmentLibraryList = new ArrayList<>();
    SimpleAdapter assignmentLibraryAdapter;
    Button bCreateNewAss;

    TextView assLibId;
    TextView assignmentName;
    TextView textName;
    TextView textId1;
    Button bAssToStudent;

    List<Map<String, String>> classList = new ArrayList<>();
    String teacherId;
    UserInfo userinfo;
    HashMap<String, String> user;
    SimpleAdapter classAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        teacherId = user.get("teacherId");

        assLibId = (TextView) findViewById(R.id.tvAssLibId);
        assignmentName = (TextView) findViewById(R.id.tvAssName);
        textName = (TextView) findViewById(R.id.tvTextname);
        textId1 = (TextView) findViewById(R.id.tvTextId1);
        bAssToStudent = (Button) findViewById(R.id.bAssToStudent);
        bGetText = (Button) findViewById(R.id.bGetText);
        tvTextChosen = (TextView) findViewById(R.id.tvTextChosen);
        tvTextId = (TextView) findViewById(R.id.tvTextId);
        etAssName = (EditText) findViewById(R.id.etAssName);
        lvAssignments = (ListView) findViewById(R.id.lvAssignments);
        bTeacher = (Button) findViewById(R.id.bTeacher);
        bCreateNewAss = (Button) findViewById(R.id.bCreateNewAss);


        bAssToStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_assignment, null);
                ListView lvClasses = (ListView) layout.findViewById(R.id.lvClasses1);



                classAdapter = new SimpleAdapter(context, classList,
                        android.R.layout.simple_list_item_2,
                        new String[] {"Class", "Number of students" },
                        new int[] {android.R.id.text1, android.R.id.text2 });
                lvClasses.setAdapter(classAdapter);

                getClasses();
                builder.setView(layout);

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();





            }
        });


        lvAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> assignmentData = assignmentLibraryList.get(position);


                String AssLibId = assignmentLibraryList.get(position).get("id");
                String tvAssname = assignmentLibraryList.get(position).get("assignmentName");
                String tvTextId = assignmentLibraryList.get(position).get("textId");
                String textname = assignmentLibraryList.get(position).get("textname");


                assignmentName.setText(tvAssname);
                textId1.setText(tvTextId);
                textName.setText(textname);
                assLibId.setText(AssLibId);


            }});



                bCreateNewAss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String textId = tvTextId.getText().toString();
                        String assignmentName = etAssName.getText().toString();

                        if (!textId.equals("") && !assignmentName.equals("")) {
                            new CreateAssToLibTask(context).execute(assignmentName, textId);
                        } else {
                            int duration = Toast.LENGTH_LONG;
                            CharSequence alert = "Please fill all required fields";
                            Toast toast = Toast.makeText(context, alert, duration);
                            toast.show();
                        }

                    }
                });


                bTeacher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(AssignmentActivity.this, TeacherActivity.class);
                        startActivity(intent);
                    }
                });


                assignmentLibraryAdapter = new SimpleAdapter(this,
                        assignmentLibraryList,
                        android.R.layout.simple_list_item_1,
                        new String[]{"assignmentName"},
                        new int[]{android.R.id.text1});
                lvAssignments.setAdapter(assignmentLibraryAdapter);


                getAssignments();


                bGetText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.dialog_text_overview, null);

                        lvTextToAss = (ListView) layout.findViewById(R.id.lvTextToAss);
                        builder.setView(layout);

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        textAdapter = new SimpleAdapter(context,
                                textList,
                                android.R.layout.simple_list_item_1,
                                new String[]{"textname"},
                                new int[]{android.R.id.text1}); //text1 = the text within the listView
                        lvTextToAss.setAdapter(textAdapter);
                        lvTextToAss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Map<String, String> textData = textList.get(position);
                                String textName = textData.get("textname");
                                String textId = textData.get("id");
                                tvTextChosen.setText(textName);
                                tvTextId.setText(textId);

                            }
                        });

                        getTexts();

                    }
                });


            }

            public void getTexts() {
                new TextTask(new TextCallback() {
                    @Override
                    public void textListDone(HashMap<String, HashMap<String, String>> texts) {
                        textList.clear();
                        for (Map.Entry<String, HashMap<String, String>> text : texts.entrySet()) {

                            Map<String, String> textInfo = new HashMap<>();
                            String textId = text.getValue().get("id");
                            String textName = text.getValue().get("textname");
                            String textContent = text.getValue().get("textcontent");
                            String textBook = text.getValue().get("textbook");
                            String complexity = text.getValue().get("complexity");
                            textInfo.put("textname", textName);
                            textInfo.put("textcontent", textContent);
                            textInfo.put("textbook", textBook);
                            textInfo.put("complexity", complexity);
                            textInfo.put("id", textId);
                            textList.add(textInfo);
                        }
                        textAdapter.notifyDataSetChanged();
                    }
                }, context).execute(""); //Nothing within "" to get every text - see php script
            }

            public void getAssignments() {

                new ALTask(new AssignmentCallback() {
                    @Override
                    public void assignmentListDone(HashMap<String, HashMap<String, String>> assignments) {
                        for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {

                            Map<String, String> assInfo = new HashMap<String, String>();
                            String id = assignment.getValue().get("id");
                            String assignmentName = assignment.getValue().get("assignmentName");
                            String assignmentId = assignment.getValue().get("assignmentId");
                            String textId = assignment.getValue().get("textId");
                            String textName = assignment.getValue().get("textname");

                            assInfo.put("id", id);
                            assInfo.put("assignmentName", assignmentName);
                            assInfo.put("assignmentId", assignmentId);
                            assInfo.put("textId", textId);
                            assInfo.put("textname", textName);



                            assignmentLibraryList.add(assInfo);
                        }
                        assignmentLibraryAdapter.notifyDataSetChanged();
                    }

                }, context).execute("", "");
            }

            public void getClasses() {
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



