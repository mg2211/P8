package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svilen.p8.R;

import org.w3c.dom.Text;

import callback.*;
import helper.*;
import serverRequests.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {


    Button bLogout;
    Context context = this;
    ListView lvAssToStudent;
    AssignmentListAdapterStudent assignedAdapter;


    List<Map<String, String>> assignmentList = new ArrayList<>();
    UserInfo userinfo;
    HashMap<String, String> user;
    String studentId;
    String textId;
    String assignmentName;
    String specificAssignmentId;
    String assignmentId;
    TextView homeWork;
    TextView teacherName;
    TextView teacherEmail;
    TextView className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student);
        //getWindow().setBackgroundDrawableResource(R.drawable.green);




        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        String firstName = user.get("firstname");
        String lastName = user.get("lastname");
        String emailAddress = user.get("email");
        Log.d("StudentId:  ", studentId);

        getClassInfo();



        teacherName = (TextView) findViewById(R.id.tvTeacherName);
        teacherEmail = (TextView) findViewById(R.id.tvTeacherEmail);
        className = (TextView) findViewById(R.id.tvClassName);
        TextView name = (TextView) findViewById(R.id.tvFirst);
        name.setText(firstName);
        TextView surName = (TextView) findViewById(R.id.tvLast);
        surName.setText(lastName);
        TextView email = (TextView) findViewById(R.id.tvEmail);
        email.setText(emailAddress);
          homeWork = (TextView) findViewById(R.id.tvHomeWork) ;

        lvAssToStudent = (ListView) findViewById(R.id.lvAssOverview);

      /*  assignmentAdapter = new SimpleAdapter(this, assignmentList,
                android.R.layout.simple_list_item_1,
                new String[]{"assignmentLibName"},
                new int[]{android.R.id.text1});
        lvAssToStudent.setAdapter(assignmentAdapter);*/

        assignedAdapter = new AssignmentListAdapterStudent(this,assignmentList);

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
                Long availableto = Long.valueOf(assignmentData.get("availableTo"));

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

                        if (tsLong <= availableto && availablefrom <= tsLong) { // Only assignment that are still available within the timeframe will be displayed


                            assignmentInfo.put("assignmentLibName", specificAssignmentName);
                            assignmentInfo.put("Name", specificAssignmentName);
                            assignmentInfo.put("assignmentid", specificAssignmentId);
                            assignmentInfo.put("assignmentlibraryid", specificAssLibId);
                            assignmentInfo.put("textId", specificTextId);
                            assignmentInfo.put("isComplete", isComplete);
                            assignmentInfo.put("availableFrom", assignment.getValue().get("availableFrom"));
                            assignmentInfo.put("availableTo", assignment.getValue().get("availableTo"));

                            if (isComplete.equals("0")){
                                homeWork.setText("You have unfinished assignments");
                             //   homeWork.setTextColor(getResources().getColorStateList(R.color.UnfinishedRed));

                            }
                            assignmentList.add(assignmentInfo);

                            Log.d("2222: ", String.valueOf(assignmentInfo));
                            Log.d("2222", specificAssignmentId);
                        }


                }
            }

        }, context).executeTask("get", studentId, "", "","","");
    }


    public void getClassInfo(){



        new ClassTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> asyncResults) {
                for (Map.Entry<String, HashMap<String, String>> classData : asyncResults.entrySet()) {

                  String teacherName1 = classData.getValue().get("teacherFirstName");
                    String className1 = classData.getValue().get("className");
                    String teacherEmail1 = classData.getValue().get("teacherEmail");

                    className.setText(className1);
                    teacherName.setText(teacherName1);
                    teacherEmail.setText(teacherEmail1);

                    Log.d("123456", classData.getValue().get("teacherFirstName"));
                }


            }
        }, context).execute("FETCH", "", "", "", studentId, "");
    }

}


