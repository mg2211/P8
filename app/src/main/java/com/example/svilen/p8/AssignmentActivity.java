package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AssignmentActivity extends AppCompatActivity {

    Context context = this;
    UserInfo userInfo;
    HashMap<String, String> user;
    String teacherId;

    Button bAddAssignment;
    Button bSave;
    Button bAssign;
    EditText etSearch;
    EditText etAssignmentText;
    EditText etAssignmentName;
    ListView lvAssignments;
    TextView tvStudentPerformance;
    SimpleAdapter assignmentAdapter;
    List<Map<String, String>> assignmentLibList = new ArrayList<>();
    List<Map<String, String>> textList = new ArrayList<>();
    HashMap<String,HashMap<String, String>> assignments;

    SimpleAdapter textAdapter;
    int assignmentLibTextId;
    String assignmentLibId;
    String assignmentLibName;
    int dialogSelected;

    boolean newAssignment;
    boolean changed;

    HashMap<Integer, Integer> textListIds = new HashMap<>();
    List<Map<String, String>> studentList = new ArrayList<>();
    SimpleAdapter studentAdapter;
    List<Map<String, String>> classList = new ArrayList<>();
    SimpleAdapter classAdapter;
    List<Map<String, String>> assignedList = new ArrayList<>();
    AssignmentListAdapter assignedAdapter;
    ArrayList<Integer> studentsAssigned = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        userInfo = new UserInfo(context);
        user = userInfo.getUser();
        teacherId = user.get("teacherId");
        lvAssignments = (ListView) findViewById(R.id.lvAssignments);
        bAddAssignment = (Button) findViewById(R.id.bAddAssignment);
        etAssignmentName = (EditText) findViewById(R.id.etAssignmentName);
        etSearch = (EditText) findViewById(R.id.etSearch);
        etAssignmentText = (EditText) findViewById(R.id.etAssignmentText);
        bSave = (Button) findViewById(R.id.bSave);
        bAssign = (Button) findViewById(R.id.bAssign);
        tvStudentPerformance = (TextView) findViewById(R.id.tvStudentPerformance);

        assignmentAdapter= new SimpleAdapter(this, assignmentLibList,
                android.R.layout.simple_list_item_1,
                new String[]{"assignmentLibName"},
                new int[]{android.R.id.text1});
        lvAssignments.setAdapter(assignmentAdapter);


        textAdapter = new SimpleAdapter(this, textList,
                android.R.layout.simple_list_item_1,
                new String[]{"textname"},
                new int[]{android.R.id.text1});
        studentAdapter = new SimpleAdapter(this, studentList,
                android.R.layout.simple_list_item_1,
                new String[] {"Name"},
                new int[] {android.R.id.text1});
        assignedAdapter = new AssignmentListAdapter(this,assignedList);
        classAdapter = new SimpleAdapter(this, classList,
                android.R.layout.simple_list_item_2,
                new String[] {"Class", "Number of students" },
                new int[] {android.R.id.text1, android.R.id.text2 });
        //content pane
        etAssignmentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(assignmentLibTextId);
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
                    if(newAssignment){
                        createAssignment();
                    } else {
                        updateAssignment();
                    }
                } else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill in all relevant information";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
            }
        });
        //Left pane
        bAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changed){
                    confirm(-1);
                } else {
                    setContentPane(-1);
                }
            }
        });
        lvAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(changed) {
                    confirm(position);
                } else {
                    setContentPane(position);
                }
            }
        });

        etAssignmentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etAssignmentName.getText().toString().equals(assignmentLibName) && !etAssignmentName.getText().toString().equals("")){
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getTexts();
        getAssignmentLib();
        setNew(true);
        getStudents("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(this,TeacherActivity.class);
        startActivity(backIntent);
    }
    private void setChanged(boolean value){
        changed = value;
        Log.d(".......","changed value"+ String.valueOf(changed));
        if(value) {
            bAssign.setEnabled(false);
            bAssign.setText("Please save before assigning to students");
        } else {
            bAssign.setEnabled(true);
            bAssign.setText("Assign to students");
        }
    }
    private void setNew(boolean value){
        if(value){
            bAssign.setEnabled(false);
        } else {
            bAssign.setEnabled(true);
        }
        newAssignment = value;
        Log.d(".......","new value:"+ String.valueOf(newAssignment));
    }

    private void textDialog(final int textId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_text_overview, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();
        ListView lvTexts = (ListView) layout.findViewById(R.id.lvTexts);
        Button bDialogCancel = (Button) layout.findViewById(R.id.bDialogCancel);
        Button bDialogAddToAssignment = (Button) layout.findViewById(R.id.bDialogAddToAssignment);
        final EditText etDialogPreview = (EditText) layout.findViewById(R.id.etDialogPreview);
        TextView tvDialogCurrentlyAssigned = (TextView) layout.findViewById(R.id.tvDialogCurrentlyAssigned);
        TextView tvDialogCurrentComplexity = (TextView) layout.findViewById(R.id.tvDialogCurrentComplexity);
        final TextView tvDialogComplexity = (TextView) layout.findViewById(R.id.tvDialogComplexity);
        lvTexts.setAdapter(textAdapter);

        if(textId != 0){
            int position = textListIds.get(textId);
            String text = textList.get(position).get("textcontent");
            etDialogPreview.setText(text);
            tvDialogCurrentlyAssigned.setText("Text currently assigned: " + textList.get(position).get("textname"));
            tvDialogCurrentComplexity.setText("Complexity of currently assigned text: "+textList.get(position).get("complexity"));
            dialogSelected = textId;
        } else {
            etDialogPreview.setText("");
            dialogSelected = 0;
        }

        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etDialogPreview.setText(textList.get(position).get("textcontent"));
                tvDialogComplexity.setText("Complexity of currently assigned text: "+textList.get(position).get("complexity"));
                dialogSelected = Integer.parseInt(textList.get(position).get("textid"));
            }
        });

        bDialogAddToAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAssignmentText.setText(textList.get(textListIds.get(dialogSelected)).get("textname"));
                if(assignmentLibTextId != dialogSelected) {
                    assignmentLibTextId = dialogSelected;
                    setChanged(true);
                }
                dialog.dismiss();
                Log.d("assignmentLibTextId", String.valueOf(assignmentLibTextId));
            }
        });
        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.d("assignmentLibTextId", String.valueOf(assignmentLibTextId));
            }
        });
    }
    public void confirm(final int position) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes - Save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(newAssignment){
                            Log.d("new assignment","true");
                            if(createAssignment()) {
                                setContentPane(position);
                            }
                        } else {
                            if(updateAssignment()){
                                setContentPane(position);
                            }
                            Log.d("update assignment", "true");
                        }
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setContentPane(position);
                        setChanged(false);
                        setNew(false);
                    }
                })
                .show();
    }
    private boolean createAssignment(){
        if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
            new AssignmentLibTask(new AssignmentLibCallback() {
                @Override
                public void AssignmentLibDone(HashMap<String, HashMap<String, String>> results) {
                    assignmentLibId = results.get("response").get("insertedId");
                    if(results.get("response").get("responseCode").equals("100")){
                        setChanged(false);
                        setNew(false);
                        getAssignmentLib();
                    }
                }
            },context).executeTask("create",teacherId,"",etAssignmentName.getText().toString(), String.valueOf(assignmentLibTextId));
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all relevant information";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }
    }
    private boolean updateAssignment(){
        if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
            new AssignmentLibTask(new AssignmentLibCallback() {
                @Override
                public void AssignmentLibDone(HashMap<String, HashMap<String, String>> results) {
                    if(results.get("response").get("responseCode").equals("100")){
                        setChanged(false);
                        setNew(false);
                        getAssignmentLib();
                    }
                }
            },context).executeTask("update",teacherId, assignmentLibId,etAssignmentName.getText().toString(), String.valueOf(assignmentLibTextId));
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all relevant information";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }

    }
    private boolean getStudents(String classId){
        new StudentTask(new StudentCallback() {
            @Override
            public void studentListDone(HashMap<String, HashMap<String, String>> students) {

                if (!studentList.isEmpty()) {
                    studentList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> student : students.entrySet()) {
                    Map<String, String> studentInfo = new HashMap<>();
                    String studentName = student.getValue().get("lastname") + ", " + student.getValue().get("firstname");
                    String studentId = student.getValue().get("studentId");
                    studentInfo.put("Name", studentName);
                    studentInfo.put("studentId",studentId);
                    studentList.add(studentInfo);
                }
                studentAdapter.notifyDataSetChanged();
            }
        }, context).execute(classId, teacherId);
        return true;
    }
    private void getClasses(){
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
    private boolean getAssignmentLib(){
        new AssignmentLibTask(new AssignmentLibCallback() {
            @Override
            public void AssignmentLibDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                assignmentLibList.clear();
                for (Map.Entry<String, HashMap<String, String>> assignment : results.entrySet()) {
                    final Map<String, String> assignmentInfo = new HashMap<>();
                    String assignmentId = assignment.getValue().get("id");
                    String assignmentName = assignment.getValue().get("name");
                    String assignmentText = assignment.getValue().get("textId");
                    String assignedStudents = assignment.getValue().get("assignedStudents");
                    String assignmentIds = assignment.getValue().get("assignmentIds");
                    String isComplete = assignment.getValue().get("isComplete");
                    String assignmentTimes = assignment.getValue().get("assignmentTimes");

                    assignmentInfo.put("assignmentLibId",assignmentId);
                    assignmentInfo.put("assignmentLibName",assignmentName);
                    assignmentInfo.put("assignmentText",assignmentText);
                    assignmentInfo.put("assignedStudents",assignedStudents);
                    assignmentInfo.put("assignmentIds",assignmentIds);
                    assignmentInfo.put("isComplete",isComplete);
                    assignmentInfo.put("assignmentTimes",assignmentTimes);
                    assignmentLibList.add(assignmentInfo);
                }
                assignmentAdapter.notifyDataSetChanged();
            }
        },context).executeTask("get",teacherId,"","","");
        return true;
    }
    private void getTexts(){
        new TextTask(new TextCallback() {
            @Override
            public void TextCallBack(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                textList.clear();
                int i = 0;
                for (Map.Entry<String, HashMap<String, String>> text : results.entrySet()) {
                    Map<String, String> textInfo = new HashMap<>();
                    String textId = text.getValue().get("id");
                    String textName = text.getValue().get("textname");
                    String textContent = text.getValue().get("textcontent");
                    String textBook = text.getValue().get("textbook");
                    String complexity = text.getValue().get("complexity");
                    textInfo.put("textname", textName);
                    textInfo.put("textcontent", textContent);
                    textInfo.put("textbook", textBook);
                    textInfo.put("complexity", "Complexity: " + complexity);
                    textInfo.put("textid", textId);
                    textList.add(textInfo);
                    textListIds.put(Integer.valueOf(textId),i);
                    i++;
                }
                textAdapter.notifyDataSetChanged();
            }
        },context).executeTask("get","","","",0);
    }
    private void datePicker(final Long offsetDate, final DatePickerCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_time, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();
        final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) layout.findViewById(R.id.timePicker);
        Button bDialogOk = (Button) layout.findViewById(R.id.bDialogOk);
        Button bDialogCancel = (Button) layout.findViewById(R.id.bDialogCancel);

        Long offset = offsetDate*1000; //timestamp converted to microseconds.

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(offset);

        final int offsetDay = calendar.get(Calendar.DATE);
        final int offsetMonth = calendar.get(Calendar.MONTH);
        final int offsetYear = calendar.get(Calendar.YEAR);
        final int offsetHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int offsetMinute = calendar.get(Calendar.MINUTE);
        datePicker.setMinDate(System.currentTimeMillis()-1000);
        datePicker.init(offsetYear,offsetMonth,offsetDay,null);

        Log.d("off day", String.valueOf(offsetDay));
        Log.d("off month", String.valueOf(offsetMonth));
        Log.d("off year", String.valueOf(offsetYear));
        Log.d("off hour", String.valueOf(offsetHour));
        Log.d("off min", String.valueOf(offsetMinute));


        bDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time;
                Log.d("clicked","true");
                int hour;
                int minute;
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                if (Build.VERSION.SDK_INT >= 23 ) {
                    hour = timePicker.getHour();
                } else {
                    hour = timePicker.getCurrentHour();
                }

                if(Build.VERSION.SDK_INT >= 23){
                    minute = timePicker.getMinute();
                } else {
                    minute = timePicker.getCurrentMinute();
                }

                Log.d("minute", String.valueOf(minute));
                Log.d("hour", String.valueOf(hour));
                Log.d("year", String.valueOf(year));
                Log.d("month",String.valueOf(month));
                Log.d("day",String.valueOf(day));

                if(day == offsetDay && month == offsetMonth && year == offsetYear && hour <= offsetHour && minute <= offsetMinute){
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "The time selected has passed";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                } else {
                    //converting all times and dates to Strings and adding leading zeroes
                    String monthString;
                    String dayString;
                    String hourString;
                    String minString;
                    String yearString = String.valueOf(year);

                    if(month < 10){
                        monthString = "0"+String.valueOf(month+1);
                    } else {
                        monthString = String.valueOf(month+1);
                    }

                    if(day < 10){
                        dayString = "0"+String.valueOf(day);
                    } else {
                        dayString = String.valueOf(day);
                    }

                    if(hour < 10){
                        hourString = "0"+String.valueOf(hour);
                    } else {
                        hourString = String.valueOf(hour);
                    }
                    if(minute < 10){
                        minString = "0"+String.valueOf(minute);
                    } else {
                        minString = String.valueOf(minute);
                    }

                    String dateAndTime = yearString+monthString+dayString+hourString+minString;
                    try {

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = dateFormatter.parse(dateAndTime);
                        time = date.getTime()/1000;
                        callback.dateSelected(time);
                        dialog.dismiss();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.dateSelected(null);
            }
        });
    }

    private void setContentPane(final int position){
        bAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignmentDialog(position);
            }
        });

        if(position >= 0) {
            assignmentLibTextId = Integer.parseInt(assignmentLibList.get(position).get("assignmentText"));
            int textListPos = textListIds.get(assignmentLibTextId);
            etAssignmentName.setText(assignmentLibList.get(position).get("assignmentLibName"));
            assignmentLibId = assignmentLibList.get(position).get("assignmentLibId");
            etAssignmentText.setText(textList.get(textListPos).get("textname"));
            assignmentLibName = assignmentLibList.get(position).get("assignmentLibName");
            assignedList.clear();
            studentsAssigned.clear();
            setChanged(false);
            setNew(false);

            assignments = new HashMap<>(getAssignments(assignmentLibId));
            assignments.remove("response");

           for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {

               HashMap<String, String> specificAssignment = assignment.getValue();
               for(Map<String, String> student : studentList){
                   if(specificAssignment.get("studentId").equals(student.get("studentId"))){
                       specificAssignment.put("Name",student.get("Name"));
                   }
               }
               assignedList.add(specificAssignment);
               assignedAdapter.notifyDataSetChanged();

           }
            Log.d("ASSIGNED LIST",assignedList.toString());
            Log.d("ASSIGNMENT HM",assignments.toString());
            if(!assignments.isEmpty()){
                etAssignmentText.setEnabled(false);
                etAssignmentName.setEnabled(false);
                bSave.setEnabled(false);
            } else {
                etAssignmentName.setEnabled(true);
                etAssignmentText.setEnabled(true);
                bSave.setEnabled(true);
            }
        } else {
            etAssignmentName.setText("");
            etAssignmentName.setEnabled(true);
            etAssignmentText.setText("");
            etAssignmentText.setEnabled(true);
            bSave.setEnabled(true);
            setChanged(false);
            setNew(true);
            assignmentLibName = "";
            assignmentLibTextId = 0;
            assignmentLibId = "";
        }

    }
    private void assignmentDialog(final int assignmentListPos){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_assign, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();
        ListView lvDialogClasses = (ListView) layout.findViewById(R.id.lvDialogClasses);
        final ListView lvDialogStudents = (ListView) layout.findViewById(R.id.lvDialogStudents);
        ListView lvDialogAssigned = (ListView) layout.findViewById(R.id.lvDialogAssigned);

        Button bDialogAssign = (Button) layout.findViewById(R.id.bDialogAssign);
        lvDialogClasses.setAdapter(classAdapter);
        lvDialogStudents.setAdapter(studentAdapter);
        lvDialogAssigned.setAdapter(assignedAdapter);
        getClasses();

        lvDialogClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> classData = classList.get(position);
                String classId = classData.get("ClassId");
                if(getStudents(classId)){
                    Log.d("students",studentList.toString());
                }

            }
        });

        lvDialogStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                for(Map<String, String> assignments : assignedList){
                    if(studentList.get(position).get("studentId").equals(assignments.get("studentId")) && assignments.get("isComplete").equals("0")){
                        int duration = Toast.LENGTH_LONG;
                        CharSequence alert = "Student already assigned - Please remove first";
                        Toast toast = Toast.makeText(context, alert, duration);
                        toast.show();
                        return;
                    }
                }
                datePicker((System.currentTimeMillis()-60000)/1000, new DatePickerCallback() {
                    @Override
                    public void dateSelected(final Long from) {
                        if(from != null){
                            datePicker(from, new DatePickerCallback() {
                                @Override
                                public void dateSelected(Long to) {
                                    if(to != null){
                                        HashMap<String, String> assignment = new HashMap<>();
                                        assignment.put("availableFrom", String.valueOf(from));
                                        assignment.put("availableTo", String.valueOf(to));
                                        assignment.put("studentId",studentList.get(position).get("studentId"));
                                        assignment.put("Name",studentList.get(position).get("Name"));
                                        assignment.put("assignmentLibName",assignmentLibName);
                                        assignment.put("assignmentLibId",assignmentLibId);
                                        assignment.put("isComplete","0");
                                        assignment.put("textId", String.valueOf(assignmentLibTextId));
                                        assignment.put("new","true");
                                        assignedList.add(assignment);
                                        assignedAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
            }

        });

        lvDialogAssigned.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm")
                        .setMessage("Are you sure that you want to delete the assignment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(assignedList.get(position).get("assignmentid") != null) {
                                    Log.d("assigned id", assignedList.get(position).get("assignmentid"));
                                    String assignmentId = assignedList.get(position).get("assignmentid");
                                    new AssignmentTask(new AssignmentCallback() {
                                        @Override
                                        public void assignmentDone(HashMap<String, HashMap<String, String>> assignments) {

                                        }
                                    }, context).executeTask("delete", "", "", "", "", assignmentId);
                                }
                                assignedList.remove(position);
                                assignedAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        bDialogAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i<assignedList.size();i++){
                    Map<String, String> assignment = assignedList.get(i);
                    if(assignment.get("new") != null){
                        new AssignmentTask(new AssignmentCallback() {
                            @Override
                            public void assignmentDone(HashMap<String, HashMap<String, String>> assignments) {

                            }
                        },context).executeTask("assign",assignment.get("studentId"),assignmentLibId,assignment.get("availableFrom"),assignment.get("availableTo"),"");
                    }
                }
                setContentPane(assignmentListPos);
                dialog.dismiss();
            }
        });
    }


    private HashMap<String, HashMap<String,String>> getAssignments(String assignmentLibId){
        try {
            return new AssignmentTask(new AssignmentCallback() {
                @Override
                public void assignmentDone(HashMap<String, HashMap<String, String>> assignments) {

                }
            },context).execute("get","",assignmentLibId,"","","").get(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}