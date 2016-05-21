package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

import com.example.svilen.p8.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

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

import callback.Callback;
import callback.DatePickerCallback;
import helper.AssignmentListAdapter;
import helper.QuestionListAdapter;
import helper.UserInfo;
import serverRequests.AssignmentLibTask;
import serverRequests.AssignmentTask;
import serverRequests.ClassTask;
import serverRequests.QuestionResultTask;
import serverRequests.QuestionTask;
import serverRequests.StudentTask;
import serverRequests.TextTask;

public class AssignmentActivity extends AppCompatActivity {

    private final Context context = this;
    private UserInfo userInfo;
    private HashMap<String, String> user;
    private String teacherId;

    private Button bAddAssignment;
    private Button bSave;
    private Button bAssign;
    private EditText etSearch;
    private EditText etAssignmentText;
    private EditText etAssignmentName;
    private ListView lvAssignments;
    private TextView tvStudentPerformance;
    private SimpleAdapter assignmentAdapter;
    private final List<Map<String, String>> assignmentLibList = new ArrayList<>();
    private final List<Map<String, String>> textList = new ArrayList<>();
    private HashMap<String,HashMap<String, String>> assignments;

    private SimpleAdapter textAdapter;
    private int assignmentLibTextId;
    private String assignmentLibId;
    private String assignmentLibName;
    private int dialogSelected;

    private boolean newAssignment;
    private boolean changed;

    private final HashMap<Integer, Integer> textListIds = new HashMap<>();
    private final List<Map<String, String>> studentList = new ArrayList<>();
    private SimpleAdapter studentAdapter;
    private final List<Map<String, String>> classList = new ArrayList<>();
    private SimpleAdapter classAdapter;
    private final List<Map<String, String>> assignedList = new ArrayList<>();
    private AssignmentListAdapter assignedAdapter;
    private final ArrayList<Integer> studentsAssigned = new ArrayList<>();
    private CombinedChart mChart;
    private final ArrayList<BarEntry> yVal = new ArrayList<>();
    private final ArrayList<String> xVals = new ArrayList<>();
    private final ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    private final ArrayList<Entry> lineY = new ArrayList<>();
    private final ArrayList<ILineDataSet> lineSets = new ArrayList<>();
    HashMap<String, HashMap<String, HashMap<String, String>>> result;

    ArrayList<String> assignmentIds = new ArrayList<>();
    HashMap<String,HashMap<String, String>> questions = new HashMap<>();
    HashMap<String,HashMap<String,String>> generalResults = new HashMap<>();


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
        mChart = (CombinedChart) findViewById(R.id.chart);

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
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                assignmentAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Auto generated stub
            }
        });
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

    private void confirm(final int position) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes - Save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(newAssignment){
                            createAssignment();
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
            try {
              HashMap<String, HashMap<String, String>> result= new AssignmentLibTask(new Callback() {
                    @Override
                    public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                    }
                },context).execute("create",teacherId,"",etAssignmentName.getText().toString(), String.valueOf(assignmentLibTextId)).get(30,TimeUnit.SECONDS);

                if(result.get("response").get("responseCode").equals("101")){
                    setChanged(false);
                    setNew(false);
                    String insertedId = result.get("response").get("insertedId");
                    Log.d("LIST BEFORE", String.valueOf(assignmentLibList.size()));
                    if(getAssignmentLib()){
                        for(int i=0; i<assignmentLibList.size();i++){
                            if(assignmentLibList.get(i).get("assignmentLibId").equals(insertedId)){
                                setContentPane(i);
                            }
                        }
                    }
                }

            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
               return false;
            } catch (TimeoutException e) {
               return false;
            }
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all relevant information";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }
        return false;
    }
    private boolean updateAssignment(){
        if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
            new AssignmentLibTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
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

        new StudentTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> asyncResults) {
                if (!studentList.isEmpty()) {
                    studentList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> student : asyncResults.entrySet()) {
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
        new ClassTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                if(!classList.isEmpty()){
                    classList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> classId : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String specificClassname = classId.getValue().get("className");
                    String specificClassStudents = classId.getValue().get("numOfStudents");
                    String specificClassId = classId.getValue().get("classId");
                    classInfo.put("ClassId", specificClassId);
                    classInfo.put("Class", specificClassname);
                    classInfo.put("Number of students", "Number of students: "+ specificClassStudents);
                    classList.add(classInfo);
                }
                classAdapter.notifyDataSetChanged();
            }
        },context).execute("FETCH", "", teacherId, "", "", "");
    }
    private boolean getAssignmentLib(){
        try {
            HashMap<String, HashMap<String, String>> assignments = new HashMap<>(new AssignmentLibTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                }
            },context).execute("get",teacherId,"","","").get(30,TimeUnit.SECONDS));

            assignments.remove("response");
            assignmentLibList.clear();
            for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {
                final Map<String, String> assignmentInfo = new HashMap<>();
                String assignmentId = assignment.getValue().get("id");
                String assignmentName = assignment.getValue().get("name");
                String assignmentText = assignment.getValue().get("textId");

                assignmentInfo.put("assignmentLibId",assignmentId);
                assignmentInfo.put("assignmentLibName",assignmentName);
                assignmentInfo.put("assignmentText",assignmentText);
                assignmentLibList.add(assignmentInfo);
            }
            assignmentAdapter.notifyDataSetChanged();
            return true;
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        } catch (TimeoutException e) {
            return false;
        }
    }
    private void getTexts(){
        new TextTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
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
            Map<String, String> assignmentLibData =(Map) assignmentAdapter.getItem(position);
            assignmentLibTextId = Integer.parseInt(assignmentLibData.get("assignmentText"));
            assignmentLibName = assignmentLibData.get("assignmentLibName");
            Log.d("assignmentLibName", assignmentLibName);
            assignmentLibId = assignmentLibData.get("assignmentLibId");
            Log.d("assignmentLibId", assignmentLibId);
            int textListPos = textListIds.get(assignmentLibTextId);
            etAssignmentName.setText(assignmentLibName);
            etAssignmentText.setText(textList.get(textListPos).get("textname"));

            assignedList.clear();
            studentsAssigned.clear();
            setChanged(false);
            setNew(false);
            getQuestions(assignmentLibTextId);

            assignments = new HashMap<>(getAssignments(assignmentLibId));
            assignments.remove("response");

           for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {

               HashMap<String, String> specificAssignment = assignment.getValue();
               for(Map<String, String> student : studentList){
                   if(specificAssignment.get("studentId").equals(student.get("studentId"))){
                       specificAssignment.put("Name",student.get("Name"));
                   }
               }
               if(specificAssignment.get("Name") != null) {
                   assignedList.add(specificAssignment);
               }
               assignedAdapter.notifyDataSetChanged();

           }
            Log.d("ASSIGNED LIST",assignedList.toString());
            Log.d("ASSIGNMENT HM",assignments.toString());
            if(!assignments.isEmpty()){
                etAssignmentText.setEnabled(false);
                etAssignmentName.setEnabled(false);
                bSave.setEnabled(false);
                mChart.setVisibility(View.VISIBLE);
                tvStudentPerformance.setVisibility(View.VISIBLE);
                barChart();
                calculateStats();
            } else {
                etAssignmentName.setEnabled(true);
                etAssignmentText.setEnabled(true);
                bSave.setEnabled(true);
                mChart.setVisibility(View.INVISIBLE);
                tvStudentPerformance.setVisibility(View.INVISIBLE);
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
            mChart.setVisibility(View.INVISIBLE);
            tvStudentPerformance.setVisibility(View.INVISIBLE);
        }

    }

    private void getQuestions(int assignmentLibTextId) {
        new QuestionTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                questions = results;
            }
        },context).executeTask("get","", String.valueOf(assignmentLibTextId),"","");
    }

    private void calculateStats() {
        yVal.clear();
        xVals.clear();
        dataSets.clear();
        lineSets.clear();
        lineY.clear();
        assignmentIds.clear();

        result = new HashMap<>(getResult(assignmentLibId));

        result.remove("response");
        Log.d("RESULT RESPONSE",result.toString());


        ArrayList<Integer> colors = new ArrayList<>();
        int index = 0;
        double total = 0;

        for(int i=0; i<assignedList.size(); i++){
            Map<String, String> assignment = assignedList.get(i);
            if(assignment.get("isComplete").equals("1")){
                String assignmentId = assignment.get("assignmentid");
                String studentName = assignment.get("Name");

                HashMap<String, HashMap<String, String>> assignmentResult = result.get(assignmentId);
                if(assignmentResult != null) {
                    assignmentIds.add(assignmentId);
                    Log.d("assignment result", assignmentResult.toString());
                    int numberOfQuestions = 0;
                    int correctAnswers = 0;
                    for (Map.Entry<String, HashMap<String, String>> questionResults : assignmentResult.entrySet()) {
                        if(!questionResults.getKey().equals("time")) {
                            if(questionResults.getValue().get("correct").equals("1")){
                                correctAnswers++;
                            }
                            numberOfQuestions++;
                        }
                    }
                    double percentage = ((double) correctAnswers / (double) numberOfQuestions) * 100;
                    yVal.add(new BarEntry((float) percentage, index));
                    xVals.add(studentName);

                    if (percentage >= 50 && percentage <= 75) {
                        colors.add(Color.rgb(255, 235, 69));
                    } else if (percentage > 75) {
                        colors.add(Color.rgb(156, 204, 101));
                    } else {
                        colors.add(Color.rgb(239, 83, 80));
                    }
                    total = total + percentage;
                    index++;
                    Log.d("Questions/correct ", String.valueOf(numberOfQuestions) + String.valueOf(correctAnswers));
                }
            }
        }

        for (int i = 0; i < index; i++) {
            lineY.add(new Entry((float) (total / xVals.size()), i));
        }

        BarDataSet set = new BarDataSet(yVal, "Students");
        LineDataSet lineSet = new LineDataSet(lineY, "Average");
        lineSet.enableDashedLine(5, 5, 0);
        lineSet.setCircleColor(Color.GRAY);
        lineSet.setColor(Color.GRAY);
        lineSet.setValueTextColor(Color.GRAY);
        lineSet.setValueTextSize(10f);
        lineSet.setDrawCircles(false);

        set.setColors(colors);

        Log.d("data", set.toString());
        Log.d("lineset", lineSet.toString());
        Log.d("XVALS", String.valueOf(xVals.size()));
        dataSets.add(set);
        lineSets.add(lineSet);
        BarData data = new BarData(xVals, dataSets);
        LineData lineData = new LineData(xVals, lineSets);
        CombinedData combinedData = new CombinedData(xVals);
        combinedData.setData(data);


        if (xVals.size() > 1) {
            combinedData.setData(lineData);
        }
        if (xVals.size() > 0){
            mChart.setData(combinedData);
        } else {
            mChart.setData(null);
        }
        mChart.notifyDataSetChanged();
        mChart.invalidate();

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
                                    new AssignmentTask(new Callback() {
                                        @Override
                                        public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

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
                    //Log.d("assignmentLibId", assignmentLibId);
                    if(assignment.get("new") != null){
                        new AssignmentTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

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
            return new AssignmentTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

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

    private void barChart(){
        //design barChart
        mChart = (CombinedChart) findViewById(R.id.chart);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.animateY(1250);
        mChart.getLegend().setEnabled(true);
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
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                Log.d("entry", String.valueOf(entry.getVal()));
                Log.d("xval", String.valueOf(entry.getXIndex()));
                Log.d("studentid", assignmentIds.get(entry.getXIndex()));
                String assignmentId = assignmentIds.get(entry.getXIndex());
                statDialog(assignmentId);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
    private HashMap<String, HashMap<String, HashMap<String, String>>> getResult(String assignmentLibId){
        try {
            return new QuestionResultTask(context).execute("","","","","","","","get",assignmentLibId).get(30,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void statDialog(String assignmentId){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_stats, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();

        ListView lvDialogQuestions = (ListView) layout.findViewById(R.id.lvDialogQuestions);
        final PieChart chartDialog = (PieChart) layout.findViewById(R.id.chartDialog);
        TextView tvDialogTime = (TextView) layout.findViewById(R.id.tvDialogTime);
        TextView tvDialogAverageTime = (TextView) layout.findViewById(R.id.tvDialogAverageTime);
        TextView tvDialogCorrect = (TextView) layout.findViewById(R.id.tvDialogCorrect);
        TextView tvDialogCorrectAverage = (TextView) layout.findViewById(R.id.tvDialogCorrectAverage);
        final List<Map<String, String>> questionList = new ArrayList<>();
        QuestionListAdapter questionListAdapter = new QuestionListAdapter(this,questionList);
        lvDialogQuestions.setAdapter(questionListAdapter);

        chartDialog.setUsePercentValues(true);
        chartDialog.setDescription("");
        chartDialog.setExtraOffsets(5, 10, 5, 5);

        chartDialog.setDragDecelerationFrictionCoef(0.95f);

        chartDialog.setDrawHoleEnabled(true);
        chartDialog.setHoleColor(Color.WHITE);

        chartDialog.setTransparentCircleColor(Color.WHITE);
        chartDialog.setTransparentCircleAlpha(110);

        chartDialog.setHoleRadius(58f);
        chartDialog.setTransparentCircleRadius(61f);

        chartDialog.setRotationAngle(0);
        chartDialog.setRotationEnabled(true);
        chartDialog.setHighlightPerTapEnabled(true);
        chartDialog.animateY(1400, Easing.EasingOption.EaseInOutQuad);


        int studentCorrect = 0;
        int studentAnswers = 0;
        HashMap<String, HashMap<String, String>> studentResult = result.get(assignmentId);
        for(Map.Entry<String, HashMap<String, String>> studentResults : studentResult.entrySet()){
            if(!studentResults.getKey().equals("time")){
                Log.d("studentResult",studentResults.toString());
                if(studentResults.getValue().get("correct").equals("1")){
                    studentCorrect++;
                }
                studentAnswers++;
            }
        }
        double studentAverage = ((double) studentCorrect / (double) studentAnswers) * 100;
        String studentAverageString = String.format("%.1f",studentAverage) + "%";

        double totalTime = 0;
        double total = 0;
        double assignments = assignmentIds.size();

        for(int i=0; i<assignmentIds.size(); i++){
            HashMap<String, HashMap<String, String>> assignmentResult = result.get(assignmentIds.get(i));
            totalTime = totalTime+Double.parseDouble(assignmentResult.get("time").get("time"));

            int assignmentAnswers = 0;
            int assignmentCorrect = 0;

            for(Map.Entry<String, HashMap<String, String>> result : assignmentResult.entrySet()){
                if(!result.getKey().equals("time")){
                    Log.d("studentResult",result.toString());
                    if(result.getValue().get("correct").equals("1")){
                        assignmentCorrect++;
                    }
                    assignmentAnswers++;
                }
            }
            double assignmentAverage = ((double) assignmentCorrect / (double) assignmentAnswers) * 100;
            total = total+assignmentAverage;
        }

        double average = total/assignments;
        double averageTime = totalTime/assignments;

        tvDialogCorrect.setText(studentAverageString);

        int studentTime = Integer.parseInt(studentResult.get("time").get("time"));
        tvDialogTime.setText(convertTime(studentTime));

        if(studentTime < averageTime){
            double difference = ((averageTime-studentTime)/studentTime)*100;
            tvDialogAverageTime.setText(Math.round(difference)+"% below average");
        } else if(studentTime > averageTime){
            double difference = ((studentTime-averageTime)/averageTime)*100;
            tvDialogAverageTime.setText(Math.round(difference)+"% above average");
        } else {
            tvDialogAverageTime.setText("On average");
        }

        if(studentAverage < average){
            double difference = ((average-studentAverage)/studentAverage)*100;
            tvDialogCorrectAverage.setText(Math.round(difference)+"% below average");
        } else if(studentAverage > average){
            double difference = ((studentAverage-average)/average)*100;
            tvDialogCorrectAverage.setText(Math.round(difference)+"% above average");
        } else {
            tvDialogCorrectAverage.setText("On average");
        }

        for(Map.Entry<String, HashMap<String, String>> question : questions.entrySet()){

            Map<String, String> specificQuestion = question.getValue();
            String questionContent = specificQuestion.get("questionContent");
            String questionId = specificQuestion.get("questionId");
            String answer = studentResult.get(questionId).get("answerContent");
            String correct = studentResult.get(questionId).get("correct");

            Map<String, String> questionInfo = new HashMap<>();
            questionInfo.put("id",questionId);
            questionInfo.put("questionContent",questionContent);
            questionInfo.put("answer",answer);
            questionInfo.put("correct",correct);

            questionList.add(questionInfo);
            questionListAdapter.notifyDataSetChanged();
        }

        lvDialogQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String questionId = questionList.get(position).get("id");
                int totalAnswers = 0;
                int correctAnswers = 0;

                for(int i=0; i<assignmentIds.size(); i++){
                    HashMap<String, HashMap<String, String>> assignmentResult = result.get(assignmentIds.get(i));
                    if(assignmentResult.get(questionId).get("correct").equals("1")){
                        correctAnswers++;
                    }
                    totalAnswers++;
                }
                double correct = ((double) correctAnswers / (double) totalAnswers) * 100;
                double wrong = 100-correct;

                Log.d("correct %", String.valueOf(correct));
                Log.d("wrong", String.valueOf(wrong));

                //PIE CHART

                ArrayList<Entry> pieValues = new ArrayList<>();
                ArrayList<String> pieNames = new ArrayList<>();

                pieValues.add(new Entry((float) correct,0));
                pieNames.add(0,"Correct answers");

                pieValues.add(new Entry((float) wrong,1));
                pieNames.add(1,"Wrong answers");

                PieDataSet dataSet = new PieDataSet(pieValues, "");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.GREEN);
                colors.add(Color.RED);
                dataSet.setColors(colors);

                PieData pieData = new PieData(pieNames, dataSet);
                pieData.setValueFormatter(new PercentFormatter());
                pieData.setValueTextSize(11f);
                pieData.setValueTextColor(Color.WHITE);

                chartDialog.setData(pieData);

                chartDialog.highlightValues(null);

                chartDialog.invalidate();

            }
        });
    }

    private String convertTime(int time){
        int hour = time/3600;
        int remainder = time - hour*3600;
        int minute = remainder/60;
        remainder = remainder - minute * 60;
        int second = remainder;

        String hours,minutes,seconds;
        if(hour < 10){
            hours = "0"+hour;
        } else {
            hours = String.valueOf(hour);
        }

        if(minute<10){
            minutes = "0"+minute;
        } else {
            minutes = String.valueOf(minute);
        }

        if(second<10){
            seconds = "0"+second;
        } else {
            seconds = String.valueOf(second);
        }
        String timeConverted = hours+":"+minutes+":"+seconds;
        return timeConverted;
    }

}