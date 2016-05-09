package com.example.svilen.p8;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    List<Map<String, String>> assignmentList = new ArrayList<>();
    List<Map<String, String>> textList = new ArrayList<>();
    ArrayList<String> studentsAssigned = new ArrayList<>();
    ArrayList<String> assignmentIds = new ArrayList<>();
    SimpleAdapter textAdapter;
    int assignmentLibTextId;
    String assignmentLibId;
    String assignmentLibName;
    int dialogSelected;
    ArrayList<BarEntry> yVal = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    Long assignmentFrom;
    Long assignmentTo;

    boolean newAssignment;
    boolean changed;
    HashMap<Integer, Integer> textListIds = new HashMap<>();
    BarChart mChart;

    List<Map<String, String>> studentList = new ArrayList<>();
    SimpleAdapter studentAdapter;
    List<Map<String, String>> classList = new ArrayList<>();
    SimpleAdapter classAdapter;

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

        assignmentAdapter= new SimpleAdapter(this, assignmentList,
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

        bAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignmentDialog();
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
        getAssignments();
        setNew(true);
        setContentPane(-1);
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
                        getAssignments();
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
                        getAssignments();
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
    private void setContentPane(int position){
        barChart();
        if(position >= 0) {
            assignmentLibTextId = Integer.parseInt(assignmentList.get(position).get("assignmentText"));
            int textListPos = textListIds.get(assignmentLibTextId);
            etAssignmentName.setText(assignmentList.get(position).get("assignmentLibName"));
            assignmentLibId = assignmentList.get(position).get("assignmentLibId");
            etAssignmentText.setText(textList.get(textListPos).get("textname"));
            assignmentLibName = assignmentList.get(position).get("assignmentLibName");
            setChanged(false);
            setNew(false);
            dataSets.clear();
            xVals.clear();
            yVal.clear();
            if (assignmentList.get(position).get("assigned").equals("true")) {
                etAssignmentName.setEnabled(false);
                etAssignmentText.setEnabled(false);
                bSave.setEnabled(false);
                mChart.setVisibility(View.VISIBLE);
                tvStudentPerformance.setVisibility(View.VISIBLE);
            } else {
                etAssignmentText.setEnabled(true);
                etAssignmentName.setEnabled(true);
                bSave.setEnabled(true);

                mChart.setVisibility(View.INVISIBLE);
                tvStudentPerformance.setVisibility(View.INVISIBLE);
            }
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        } else {
            studentsAssigned.clear();
            assignmentIds.clear();
            mChart.setVisibility(View.INVISIBLE);
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
    private void barChart(){
        //design barChart
        mChart = (BarChart) findViewById(R.id.chart);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.animateY(1250);
        mChart.getLegend().setEnabled(false);
        mChart.setDescription("");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        YAxis yaxisleft = mChart.getAxisLeft();
        YAxis yaxisright = mChart.getAxisRight();
        yaxisleft.setLabelCount(3, true);
        yaxisright.setLabelCount(3,true);
        yaxisleft.setAxisMaxValue(100);
        yaxisright.setAxisMaxValue(100);
        yaxisleft.setAxisMinValue(0);
        yaxisright.setAxisMinValue(0);


        mChart.getAxisLeft().setDrawGridLines(false);
        BarData data = new BarData(xVals, dataSets);

        mChart.setData(data);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
            }

            @Override
            public void onNothingSelected() {

            }
        });
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
        newAssignment = value;
        Log.d(".......","new value:"+ String.valueOf(newAssignment));
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
    private void getAssignments(){
        new AssignmentLibTask(new AssignmentLibCallback() {
            @Override
            public void AssignmentLibDone(HashMap<String, HashMap<String, String>> results) {

                results.remove("response");
                assignmentList.clear();
                for (Map.Entry<String, HashMap<String, String>> assignment : results.entrySet()) {
                    Map<String, String> assignmentInfo = new HashMap<>();
                    String assignmentId = assignment.getValue().get("id");
                    String assignmentName = assignment.getValue().get("name");
                    String assignmentText = assignment.getValue().get("textId");
                    String assigned = assignment.getValue().get("assigned");

                    assignmentInfo.put("assignmentLibId",assignmentId);
                    assignmentInfo.put("assignmentLibName",assignmentName);
                    assignmentInfo.put("assignmentText",assignmentText);
                    assignmentInfo.put("assigned",assigned);
                    assignmentList.add(assignmentInfo);
                }
                assignmentAdapter.notifyDataSetChanged();
            }
        },context).executeTask("get",teacherId,"","","");
    }
    private void assignmentDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_assign, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();
        ListView lvDialogClasses = (ListView) layout.findViewById(R.id.lvDialogClasses);
        ListView lvDialogStudents = (ListView) layout.findViewById(R.id.lvDialogStudents);
        final EditText etDialogDateFrom = (EditText) layout.findViewById(R.id.etDialogDateFrom);
        final EditText etDialogDateTo = (EditText) layout.findViewById(R.id.etDialogDateTo);


        Button bDialogAssign = (Button) layout.findViewById(R.id.bDialogAssign);
        lvDialogClasses.setAdapter(classAdapter);
        lvDialogStudents.setAdapter(studentAdapter);

        getClasses();

        lvDialogClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> classData = classList.get(position);
                String classId = classData.get("ClassId");
                getStudents(classId);
            }
        });

        etDialogDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignmentFrom == null) {
                    assignmentFrom = (System.currentTimeMillis() / 1000) - 1000;
                }
                datePicker("from", assignmentFrom, new DatePickerCallback() {
                    @Override
                    public void dateSelected(Long timestamp) {
                       assignmentFrom = timestamp;
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                        Date date = new Date(timestamp*1000);
                        String formattedDate = dateFormatter.format(date);
                        etDialogDateFrom.setText(formattedDate);
                    }
                });
            }
        });

        etDialogDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignmentFrom == null){
                    assignmentFrom = (System.currentTimeMillis()/1000)-1000;
                }
                    datePicker("to",assignmentFrom, new DatePickerCallback() {
                        @Override
                        public void dateSelected(Long timestamp) {
                            assignmentTo = timestamp;
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                            Date date = new Date(timestamp*1000);
                            String formattedDate = dateFormatter.format(date);
                            etDialogDateTo.setText(formattedDate);
                        }
                    });
            }
        });


        bDialogAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    private void getStudents(String classId){
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
    private void datePicker(String mode, final Long offsetDate, final DatePickerCallback callback) {
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
        if(mode.equals("from")) {
            datePicker.setMinDate(System.currentTimeMillis()-1000);
        } else {
            datePicker.setMinDate(offset);
        }
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
                            monthString = "0"+String.valueOf(month);
                        } else {
                            monthString = String.valueOf(month);
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
            }
        });
    }
}