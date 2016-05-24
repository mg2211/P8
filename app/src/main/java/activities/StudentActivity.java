package activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.svilen.p8.R;

import callback.*;
import helper.*;
import serverRequests.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {

    /** Button used to logout*/
    private Button bLogout;

    /** context*/
    private final Context context = this;

    /** Listview containing assignments assigned to a student*/
    private ListView lvAssToStudent;

    /** Adapter for displaying assignments in the LvAssToStudent listview*/
    private AssignmentListAdapterStudent assignedAdapter;

    /** A list for storing assignments used by the assignedAdapter*/
    private final List<Map<String, String>> assignmentList = new ArrayList<>();

    /** */
    private UserInfo userinfo;

    /** */
    private HashMap<String, String> user;

    /** A string for storing the studentId of the logged in user */
    private String studentId;





    /** A textview displaying whether the student has assigned homework waiting or not*/
    private TextView homeWork;

    /** A textview displaying the teacher name*/
    private TextView teacherName;

    /** A textview displaying the teacher email*/
    private TextView teacherEmail;

    /** A textview displaying the class name*/
    private TextView className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student);

        /** create a new UserInfo to get information on the user currently logged in */
        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        String firstName = user.get("firstname");
        String lastName = user.get("lastname");
        String emailAddress = user.get("email");


        getClassInfo();


        bLogout = (Button) findViewById(R.id.bLogout);

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

        assignedAdapter = new AssignmentListAdapterStudent(this,assignmentList);

        /** Assigns the assignedAdapter to lvAssToStudent listview */
        lvAssToStudent.setAdapter(assignedAdapter);

        getAssignment();

        /** Sets an on item clicklistener to lvAssToStudent */
        lvAssToStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> assignmentData = assignmentList.get(position);
              final String textId = assignmentData.get("textId");
                final String assignmentName = assignmentData.get("assignmentLibName");
                final String assignmentId = assignmentData.get("assignmentid");
                String isComplete = assignmentData.get("isComplete");

                /** If statement checking whether the assignment clicked in the listview is finished or not*/
                if(isComplete.equals("0")){


                    /** Alert dialog is created asking the student whether he/she wishes to start an assignment*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("Do you wish to start " + assignmentName + " homework?")
                            .setTitle(assignmentName)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            /** Sends an intent to the reading activity containing information on the assignment choosen*/
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(StudentActivity.this, ReadingActivity.class);
                                    intent.putExtra("textId", textId);
                                    intent.putExtra("assignmentName", assignmentName);
                                    intent.putExtra("id", assignmentId);
                                    startActivity(intent);

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                }else{
                    /** If the assignment is finished a toast will appear */
                    Toast.makeText(StudentActivity.this, "You have finished this assignment", Toast.LENGTH_SHORT).show();

                }
            }
        });



        /** A button click listener that once clicked will logout the user */
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userinfo = new UserInfo(getApplicationContext());
                userinfo.logOut();
            }
        });


    }


    /** Disables the back button */
    @Override
    public void onBackPressed() {

        Log.d("Back button pressed", " -Disabled");

    }


    /** Launch an Assignment task which gets the needed information on assignments from the database*/
    private void getAssignment() {

        new AssignmentTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

                if (!assignmentList.isEmpty()) {
                    assignmentList.clear();
                }

                for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {
                    Map<String, String> assignmentInfo = new HashMap<>();
                    String specificAssignmentName = assignment.getValue().get("assignmentLibName");
                    String specificAssignmentId = assignment.getValue().get("assignmentid");
                    String specificAssLibId = assignment.getValue().get("assignmentlibraryid");
                    String specificTextId = assignment.getValue().get("textId");
                    String isComplete = assignment.getValue().get("isComplete");
                    Long availablefrom = Long.valueOf(assignment.getValue().get("availableFrom"));
                    Long availableto = Long.valueOf(assignment.getValue().get("availableTo"));




                        /** Gets the current time*/
                        Long currenTime = System.currentTimeMillis() / 1000;

                        /** Only assignment that are still available within the timeframe will be displayed */
                        if (currenTime <= availableto && availablefrom <= currenTime) {

                            assignmentInfo.put("assignmentLibName", specificAssignmentName);
                            assignmentInfo.put("Name", specificAssignmentName);
                            assignmentInfo.put("assignmentid", specificAssignmentId);
                            assignmentInfo.put("assignmentlibraryid", specificAssLibId);
                            assignmentInfo.put("textId", specificTextId);
                            assignmentInfo.put("isComplete", isComplete);
                            assignmentInfo.put("availableFrom", assignment.getValue().get("availableFrom"));
                            assignmentInfo.put("availableTo", assignment.getValue().get("availableTo"));


                            /** Checks whether there is an uncomplete assignment, and if so, changes homeWork textView accordingly*/
                            if (isComplete.equals("0")){
                                homeWork.setText("You have unfinished assignments");

                            }
                            assignmentList.add(assignmentInfo);


                        }


                }
            }

            /** This Assignment task runs with the parameters of "get" for method and studentId, returning all assignments that have
             * been assigned to that specific studentId*/
        }, context).executeTask("get", studentId, "", "","","");
    }

    /** Launches a ClassTask that gets the required information on a class from the database*/
    private void getClassInfo(){


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

                }


            }
            /** This class task runs with the parameters of "FETCH" for method, and the studentId of the logged in student.
             * It will return the information of a class that student logged in belongs to */
        }, context).execute("FETCH", "", "", "", studentId, "");
    }

}


