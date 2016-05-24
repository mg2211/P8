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
    private String teacherId;

    private Button bSave;
    private Button bAssign;
    private EditText etAssignmentText;
    private EditText etAssignmentName;
    private TextView tvStudentPerformance;


    /*Declaring adapters*/
    private SimpleAdapter assignmentAdapter;
    private SimpleAdapter textAdapter;
    private SimpleAdapter studentAdapter;
    private SimpleAdapter classAdapter;
    private AssignmentListAdapter assignedAdapter;

    /*Variables for holding assignmentLib information*/
    private int assignmentLibTextId;
    private String assignmentLibId;
    private String assignmentLibName;
    private int dialogSelected;

    /*Booleans for checking if an assignmentLib is new or changed - or both*/
    private boolean newAssignment;
    private boolean changed;

    /*Various lists and HashMaps*/
    private final HashMap<Integer, Integer> textListIds = new HashMap<>();
    private final List<Map<String, String>> studentList = new ArrayList<>();
    private final List<Map<String, String>> classList = new ArrayList<>();
    private final List<Map<String, String>> assignedList = new ArrayList<>();
    private final ArrayList<Integer> studentsAssigned = new ArrayList<>();
    private final ArrayList<String> assignmentIds = new ArrayList<>();
    private HashMap<String,HashMap<String, String>> questions = new HashMap<>();
    private HashMap<String, HashMap<String, HashMap<String, String>>> result;
    private final List<Map<String, String>> assignmentLibList = new ArrayList<>();
    private final List<Map<String, String>> textList = new ArrayList<>();

    /*BarChart and data for that chart*/
    private CombinedChart mChart;
    private final ArrayList<BarEntry> yVal = new ArrayList<>();
    private final ArrayList<String> xVals = new ArrayList<>();
    private final ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    private final ArrayList<Entry> lineY = new ArrayList<>();
    private final ArrayList<ILineDataSet> lineSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        /*Creating a new UserInfo Object to get the id of the teacher currently logged in*/
        UserInfo userInfo = new UserInfo(context);
        HashMap<String, String> user = userInfo.getUser();
        teacherId = user.get("teacherId");

        /*Setting up UI elements*/
        ListView lvAssignments = (ListView) findViewById(R.id.lvAssignments);
        Button bAddAssignment = (Button) findViewById(R.id.bAddAssignment);
        EditText etSearch = (EditText) findViewById(R.id.etSearch);
        bSave = (Button) findViewById(R.id.bSave);
        bAssign = (Button) findViewById(R.id.bAssign);
        tvStudentPerformance = (TextView) findViewById(R.id.tvStudentPerformance);
        etAssignmentName = (EditText) findViewById(R.id.etAssignmentName);
        etAssignmentText = (EditText) findViewById(R.id.etAssignmentText);
        mChart = (CombinedChart) findViewById(R.id.chart);

        /*Creating and setting adapters*/
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

        /*Setting a TextChangedListener for the search input field to filter assignmentLibList real time*/
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

        /* setting an onClickListener which calls the textDialog method with the id of the assignmentLibrary entry*/
        etAssignmentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(assignmentLibTextId);
            }
        });

        /*onCLickListener for the save button*/
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Checking if all inputs are filled in*/
                if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
                    /*Calling the appropriate method depending on the state of the assignmentLibrary entry*/
                    if(newAssignment){
                        createAssignment();
                    } else {
                        updateAssignment();
                    }
                } else {
                    /*If one or more inputs are empty, a toast is shown saying so*/
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill in all relevant information";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
            }
        });

        /*onClickListener for the Add Assignment button*/
        bAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Checking wheter the currently shown assignment has been changed. if so, the confirm method is called with the parameter -1.*/
                if(changed){
                    confirm(-1);
                } else {
                    /*If no changes has been detected the setContentPane is called with parameter -1(new assignment)*/
                    setContentPane(-1);
                }
            }
        });
        /*onItemClickLister for the listView*/
        lvAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Checking wheter the currently shown assignment has been changed. if so, the confirm method is called with the position selected from the listview.*/
                if(changed) {
                    confirm(position);
                } else {
                    /*if no changes has been detected the setContentPane is called with the position of the selection in the listview*/
                    setContentPane(position);
                }
            }
        });

        /*adding a TextChangedListener for the assignmentName editText - used for setting the changed boolean when changing this editText*/
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

        /*Calling methods to get data from the database*/
        getTexts();
        getAssignmentLib();
        getStudents("");

        /*Setting the newAssignment to true(default)*/
        setNew(true);
    }

    /**
     * Setting the changed boolean and related UI elements
     * @param value - boolean with the new value
     */
    private void setChanged(boolean value){
        changed = value;
        if(value) {
            bAssign.setEnabled(false);
            bAssign.setText("Please save before assigning to students");
        } else {
            bAssign.setEnabled(true);
            bAssign.setText("Assign to students");
        }
    }

    /**
     * Setting the newAssignment boolean and related UI elements
     * @param value - boolean with the new value
     */
    private void setNew(boolean value){
        newAssignment = value;
        if(value){
            bAssign.setEnabled(false);
        } else {
            bAssign.setEnabled(true);
        }

    }

    /**
     * Method for creating a textDialog
     * @param textId - if the assignment has already been assigned a text.
     */
    private void textDialog(final int textId) {
        /*Creating the dialog*/
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_text_overview, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();

        /*Setting up UI elemets*/
        ListView lvTexts = (ListView) layout.findViewById(R.id.lvTexts);
        Button bDialogCancel = (Button) layout.findViewById(R.id.bDialogCancel);
        Button bDialogAddToAssignment = (Button) layout.findViewById(R.id.bDialogAddToAssignment);
        final EditText etDialogPreview = (EditText) layout.findViewById(R.id.etDialogPreview);
        TextView tvDialogCurrentlyAssigned = (TextView) layout.findViewById(R.id.tvDialogCurrentlyAssigned);
        TextView tvDialogCurrentComplexity = (TextView) layout.findViewById(R.id.tvDialogCurrentComplexity);
        final TextView tvDialogComplexity = (TextView) layout.findViewById(R.id.tvDialogComplexity);
        lvTexts.setAdapter(textAdapter);

        /*If textId has been set i.e. not 0 the dialog is set for that specific text*/
        if(textId != 0){
            int position = textListIds.get(textId);
            String text = textList.get(position).get("textcontent");
            etDialogPreview.setText(text);
            tvDialogCurrentlyAssigned.setText("Text currently assigned: " + textList.get(position).get("textname"));
            tvDialogCurrentComplexity.setText("Complexity of currently assigned text: "+textList.get(position).get("complexity"));
            dialogSelected = textId;
        } else {
            /*if no text has been assigned yet, no preview is shown when initially opening the dialog*/
            etDialogPreview.setText("");
            dialogSelected = 0;
        }

        /*onItemClickListener for the list of texts*/
        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etDialogPreview.setText(textList.get(position).get("textcontent"));
                tvDialogComplexity.setText("Complexity of currently assigned text: "+textList.get(position).get("complexity"));
                dialogSelected = Integer.parseInt(textList.get(position).get("textid"));
            }
        });

        /*onClickListener for the Add to Assignment Button - Setting the assignmentLibTextId to the text chosen in the dialog
        * and setting the changed boolean to true and dismisses the dialog
        * */
        bDialogAddToAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAssignmentText.setText(textList.get(textListIds.get(dialogSelected)).get("textname"));
                if(assignmentLibTextId != dialogSelected) {
                    assignmentLibTextId = dialogSelected;
                    setChanged(true);
                }
                dialog.dismiss();
            }
        });

        /*onClickListener for the Cancel button - Dismisses the dialog*/
        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Creating a confirmation dialog and calling methods afterwards
     * @param position - The position to send the setContentPane method after confirming
     */
    private void confirm(final int position) {
        /*Creating the dialog*/
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes - Save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*If the response is positive i.e. changes should be saved. The appropiate method is called*/
                        if(newAssignment){
                            createAssignment();
                        } else {
                            if(updateAssignment()){
                                setContentPane(position);
                            }
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

    /**
     * Method for creating assignments and creating AssignmentLibTasks
     */
    private void createAssignment(){
        /*Checking if all information is provided*/
        if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
            try {
                /*This way of calling the AssignmentLibTask freezes the UI to wait for the response - The result Hashmap
                * is containing the result from Task*/
              HashMap<String, HashMap<String, String>> result= new AssignmentLibTask(new Callback() {
                    @Override
                    public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                    }
                },context).execute("create",teacherId,"",etAssignmentName.getText().toString(), String.valueOf(assignmentLibTextId)).get(30,TimeUnit.SECONDS);

                /*Checking the response from the AssignmentLibTask*/
                if(result.get("response").get("responseCode").equals("101")){
                    setChanged(false);
                    setNew(false);
                    String insertedId = result.get("response").get("insertedId");

                    /*Calling the getAssignmentLib method*/
                    if(getAssignmentLib()){
                        /*If the method returns true - the assignmentLibList is being iterated to find the position of the assignment
                         put in in the previous part of this method and setting the content pane accordingly*/
                        for(int i=0; i<assignmentLibList.size();i++){
                            if(assignmentLibList.get(i).get("assignmentLibId").equals(insertedId)){
                                setContentPane(i);
                            }
                        }
                    }
                }

                /*Handles various exceptions*/
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        } else {
            /*If one or more input fields are empty, a toast saying so is shown*/
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all relevant information";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
    }

    /**
     * Method for handling AssignmentLibTasks for updating an assignmentLib
     * @return boolean - true if no errors are found
     */
    private boolean updateAssignment(){
        /*Checking that all information is provided*/
        if(assignmentLibTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
            new AssignmentLibTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                    /*Checking the result from the AssignmentLibTask and setting the variables accordingly*/
                    if(results.get("response").get("responseCode").equals("101")){
                        setChanged(false);
                        setNew(false);
                        getAssignmentLib();
                    }
                }
            },context).executeTask(teacherId, assignmentLibId,etAssignmentName.getText().toString(), String.valueOf(assignmentLibTextId));
            return true;
        } else {
            /*If one or more input fields are empty, a toast saying so is shown*/
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all relevant information";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }

    }

    /**
     * getStudents method
     * @param classId - if set, only students for that specific class is fetched
     */
    private void getStudents(String classId){

        /*Creating a new StudentTask*/
        new StudentTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> asyncResults) {
                /*When the task has finished - the list of students is cleared and the new information is added*/
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
    }

    /**
     * getClasses method
     */
    private void getClasses(){
        /*Creating a new ClassTask with the parameter FETCH and teacherId to only get classes associated with the teacher currently logged in*/
        new ClassTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                /*When the Task is done the list of classes is cleared and new information is being put in*/
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

    /**
     * getAssignmentLib method - used for getting the assignmentLibrary
     * @return boolean - True if no problems are encountered.
     */
    private boolean getAssignmentLib(){
        try {
            /*Creating  a new assignmentLibTask with the parameters get and teacherId and putting the results into HashMap assignments
             * This way of calling the AssignmentLibTask freezes the UI to wait for the results
             * However necessary for making sure that the assignments are fetched before continuing */
            HashMap<String, HashMap<String, String>> assignments = new HashMap<>(new AssignmentLibTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                }
            },context).execute("get",teacherId,"","","").get(30,TimeUnit.SECONDS));

            /*Removing the response from the HashMap and iterating the rest of the HashMap to populate the list of assignmentLibrary entries*/
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

            /*If an exeption is encountered the method returns false*/
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * getText method - used for getting text to populate the textDialog and getting the name of a textid connected to an assignmentLibrary entry
     */
    private void getTexts(){

        /*Creating a new TextTask*/
        new TextTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                /*Removing the response from the results HashMap and itereting the rest to populate the list of texts*/
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

    /**
     * Creating a datePicker dialog for setting date and time when assigning an assignmentLibrary entry to a student.
     * @param offsetDate - Long - the unix timestamp that is the earliest date and time the picker allows
     * @param callback - DatePickerCallback interface for sending the selected date back to the caller.
     */
    private void datePicker(final Long offsetDate, final DatePickerCallback callback) {
        /*Creating the dialog*/
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_time, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();

        /*Setting up UI elements*/
        final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) layout.findViewById(R.id.timePicker);
        Button bDialogOk = (Button) layout.findViewById(R.id.bDialogOk);
        Button bDialogCancel = (Button) layout.findViewById(R.id.bDialogCancel);

        /*converting the offset date to milliseconds*/
        Long offset = offsetDate*1000;

        /*Getting an instance of the systems calendar and setting the time to the offset*/
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(offset);

        /*Getting day, month, year, minute and hour of the calendar*/
        final int offsetDay = calendar.get(Calendar.DATE);
        final int offsetMonth = calendar.get(Calendar.MONTH);
        final int offsetYear = calendar.get(Calendar.YEAR);
        final int offsetHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int offsetMinute = calendar.get(Calendar.MINUTE);

        /*Setting the minimum date for the datePicker to a second ago - needs to be less than now*/
        datePicker.setMinDate(System.currentTimeMillis()-1000);
        /*Setting up the datePicker with the offset year, month and day - onDateChangedListener is null*/
        datePicker.init(offsetYear,offsetMonth,offsetDay,null);

        /*Setting an OnClickListener for the OK button*/
        bDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Declaring variables*/
                long time;
                int hour;
                int minute;
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                /*API Version handling - if the version is lower than 23 getCurrentHour() is used - otherwise getHour()*/
                if (Build.VERSION.SDK_INT >= 23 ) {
                    hour = timePicker.getHour();
                } else {
                    hour = timePicker.getCurrentHour();
                }
                /*API Version handling - if the version is lower than 23 getCurrentMinute() is used - otherwise getMinute()*/
                if(Build.VERSION.SDK_INT >= 23){
                    minute = timePicker.getMinute();
                } else {
                    minute = timePicker.getCurrentMinute();
                }
                /*Checking wheter the selected time and date has passed compared with the offset date*/
                if(day == offsetDay && month == offsetMonth && year == offsetYear && hour <= offsetHour && minute <= offsetMinute){
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "The time selected has passed";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                } else {
                    /*converting all times and dates to Strings and adding leading zeroes*/
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
                    /*Putting all information into a string for converting to long*/
                    String dateAndTime = yearString+monthString+dayString+hourString+minString;
                    try {
                        /*Formatting the string*/
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = dateFormatter.parse(dateAndTime);
                        /*Convering the time to unix timestamp, sending it to the callback interface and dismisses dialog*/
                        time = date.getTime()/1000;
                        callback.dateSelected(time);
                        dialog.dismiss();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*Cancel button OnClickListener - dismisses dialog and sending null to callback*/
        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.dateSelected(null);
            }
        });
    }

    /**
     * Setting up the content pane
     * @param position - the position in the listView for the assignment - pass -1 for new assignmentLibrary entry
     */
    private void setContentPane(final int position){
        /*Setting OnClickListener for the assign to students button*/
        bAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignmentDialog(position);
            }
        });

        if(position >= 0) {
            /*If the assignmentLib entry is saved in the database - setting up the content pane*/
            Map<String, String> assignmentLibData = (Map) assignmentAdapter.getItem(position);
            assignmentLibTextId = Integer.parseInt(assignmentLibData.get("assignmentText"));
            assignmentLibName = assignmentLibData.get("assignmentLibName");
            assignmentLibId = assignmentLibData.get("assignmentLibId");
            etAssignmentName.setText(assignmentLibName);
            /*Checking if the text is avaiable, if not the first available text is set as the text for the assignment*/
            try {
                int textListPos = textListIds.get(assignmentLibTextId);
                etAssignmentText.setText(textList.get(textListPos).get("textname"));
            } catch (NullPointerException e){
                int textListPos = 0;
                assignmentLibTextId = Integer.parseInt(textList.get(textListPos).get("textid"));
                etAssignmentText.setText(textList.get(textListPos).get("textname"));
            }


            /*Clearing the assignedList and studentsAssigned*/
            assignedList.clear();
            studentsAssigned.clear();

            /*Setting new and changed to false*/
            setChanged(false);
            setNew(false);

            /*Getting questions associated with the assignmentLibrary's text*/
            getQuestions(assignmentLibTextId);

            /*Getting assignments for this specific assignmentLibraryId*/
            HashMap<String, HashMap<String, String>> assignments = new HashMap<>(getAssignments(assignmentLibId));

            /*Removing response and iterating through the list of results*/
            assignments.remove("response");
           for (Map.Entry<String, HashMap<String, String>> assignment : assignments.entrySet()) {

               HashMap<String, String> specificAssignment = assignment.getValue();

               /*Iterating through the studentList*/
               for(Map<String, String> student : studentList){
                   /*If the student has an assignment, the assignment is assigned the student's name*/
                   if(specificAssignment.get("studentId").equals(student.get("studentId"))){
                       specificAssignment.put("Name",student.get("Name"));
                   }
               }
               /*if the specific assignment has a name the assignment is being added to the assignedList*/
               if(specificAssignment.get("Name") != null) {
                   assignedList.add(specificAssignment);
               }
               /*Notifying the assignedAdapter*/
               assignedAdapter.notifyDataSetChanged();

           }
            /*Checking whether an assignmentLib entry has been assigned*/
            if(!assignments.isEmpty()){
                /*If assignments is found no changes should be allowed and the calculation of stats is being run*/
                etAssignmentText.setEnabled(false);
                etAssignmentName.setEnabled(false);
                bSave.setEnabled(false);
                mChart.setVisibility(View.VISIBLE);
                tvStudentPerformance.setVisibility(View.VISIBLE);
                barChart();
                calculateStats();
            } else {
                /*If no assignments is found, changes should be allowed and no stats are calculated or shown*/
                etAssignmentName.setEnabled(true);
                etAssignmentText.setEnabled(true);
                bSave.setEnabled(true);
                mChart.setVisibility(View.INVISIBLE);
                tvStudentPerformance.setVisibility(View.INVISIBLE);
            }
        } else {
            /*If the position is <0 the content pane is set up for a new assignment*/
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

    /**
     * getting all question related to a specific text
     * @param assignmentLibTextId - the textId currently assigned to an assignment
     */
    private void getQuestions(int assignmentLibTextId) {
        new QuestionTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                questions = results;
            }
        },context).executeTask("get","", String.valueOf(assignmentLibTextId),"","");
    }

    /**
     * Calculating stats for assignments
     */
    private void calculateStats() {
        /*Clearing all lists*/
        yVal.clear();
        xVals.clear();
        dataSets.clear();
        lineSets.clear();
        lineY.clear();
        assignmentIds.clear();

        /*Calling the getResult method and copying the results in to a HashMap*/
        result = new HashMap<>(getResult(assignmentLibId));

        /*Removing the response from the HashMap*/
        result.remove("response");

        /*Setting up vars for handling new data*/

        /*List of colors to color each bar column*/
        ArrayList<Integer> colors = new ArrayList<>();

        /*The index to assign a column to*/
        int index = 0;

        /*The total amount of right answered questions*/
        double total = 0;

        /*Iterating through the list of assignments*/
        for(int i=0; i<assignedList.size(); i++){
            /*Getting the specific assignment's data*/
            Map<String, String> assignment = assignedList.get(i);

            /*Only if the assignment is completed, stats should be calculated*/
            if(assignment.get("isComplete").equals("1")){
                String assignmentId = assignment.get("assignmentid");
                String studentName = assignment.get("Name");

                /*Getting the results for the specific assignment*/
                HashMap<String, HashMap<String, String>> assignmentResult = result.get(assignmentId);

                /*Checking if the results are actually there*/
                if(assignmentResult != null) {
                    /*Adding the id to the ArrayList of completed assignments*/
                    assignmentIds.add(assignmentId);

                    /*setting up variables for calculating an assignments stats*/
                    int numberOfQuestions = 0;
                    int correctAnswers = 0;

                    /*iterating through the data for a specific assignment*/
                    for (Map.Entry<String, HashMap<String, String>> questionResults : assignmentResult.entrySet()) {
                        /*If the key in the dataSet is not time*/
                        if(!questionResults.getKey().equals("time")) {
                            /*If the question is answered correctly the correctAnswers is increased by 1*/
                            if(questionResults.getValue().get("correct").equals("1")){
                                correctAnswers++;
                            }
                            /*Regardless of correctness the numberOfQuestions is increased by 1*/
                            numberOfQuestions++;
                        }
                    }
                    /*Calculating the percentage of correctly answered questions*/
                    double percentage = ((double) correctAnswers / (double) numberOfQuestions) * 100;

                    /*Adding the data to the barChart*/
                    yVal.add(new BarEntry((float) percentage, index));
                    xVals.add(studentName);

                    /*Calculting the color of the barChart*/

                    if (percentage >= 50 && percentage <= 75) {
                        /*If the student has answered between 50 and 75% correctly*/
                        colors.add(Color.rgb(255, 235, 69)); //Yellow
                    } else if (percentage > 75) {
                        /*If the student has answered more than 75% correctly*/
                        colors.add(Color.rgb(156, 204, 101));//Green
                    } else {
                        /*If the student has answered less than 50% correctly*/
                        colors.add(Color.rgb(239, 83, 80));//Red
                    }
                    /*Adding the students percentage to the total*/
                    total = total + percentage;
                    /*Increasing the index by 1*/
                    index++;
                }
            }
        }
        /*Going through the index adding an entry to the average line*/
        for (int i = 0; i < index; i++) {
            lineY.add(new Entry((float) (total / xVals.size()), i));
        }

        /*Adding data to the dataSets*/
        BarDataSet set = new BarDataSet(yVal, "Students");
        LineDataSet lineSet = new LineDataSet(lineY, "Average");

        /*Designing the lineSet*/
        lineSet.enableDashedLine(5, 5, 0);
        lineSet.setCircleColor(Color.GRAY);
        lineSet.setColor(Color.GRAY);
        lineSet.setValueTextColor(Color.GRAY);
        lineSet.setValueTextSize(10f);
        lineSet.setDrawCircles(false);

        /*Setting the colors for the barChart*/
        set.setColors(colors);

        /*Adding the data to the datasets*/
        dataSets.add(set);
        lineSets.add(lineSet);
        /*Adding datasets to the BarData and LineData*/
        BarData data = new BarData(xVals, dataSets);
        LineData lineData = new LineData(xVals, lineSets);
        /*Creaing a new combinedData and setting the barData*/
        CombinedData combinedData = new CombinedData(xVals);
        combinedData.setData(data);

        /*If more than one assignments has been completed an average is shown*/
        if (xVals.size() > 1) {
            combinedData.setData(lineData);
        }
        if (xVals.size() > 0){
            mChart.setData(combinedData);
        } else {
            mChart.setData(null);
        }

        /*Redrawing the barChart*/
        mChart.notifyDataSetChanged();
        mChart.invalidate();

    }

    /**
     *Creating an assignmentDialog for seeing and assigning students
     * @param assignmentListPos - the list position for the assignmentLibrary entry
     */
    private void assignmentDialog(final int assignmentListPos){
        /*Creating the dialog*/
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_assign, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();

        /*Setting up UI elements*/
        ListView lvDialogClasses = (ListView) layout.findViewById(R.id.lvDialogClasses);
        final ListView lvDialogStudents = (ListView) layout.findViewById(R.id.lvDialogStudents);
        ListView lvDialogAssigned = (ListView) layout.findViewById(R.id.lvDialogAssigned);
        Button bDialogAssign = (Button) layout.findViewById(R.id.bDialogAssign);

        /*Setting adapters*/
        lvDialogClasses.setAdapter(classAdapter);
        lvDialogStudents.setAdapter(studentAdapter);
        lvDialogAssigned.setAdapter(assignedAdapter);

        /*Getting all classes for the teacher logged in*/
        getClasses();

        lvDialogClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> classData = classList.get(position);
                String classId = classData.get("ClassId");

                /*Getting students*/
                getStudents(classId);

            }
        });

        /*Adding an OnItemClickListener for the student listview - used for adding students to the assigned list*/
        lvDialogStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                /*Getting all currently assigned students, the teacher is "controlling" and iterating through the list*/
                for(Map<String, String> assignments : assignedList){
                    /*If the student selected is already assigned and hasn't completed the assignment a toast is shown
                    * saying the student can't be assigned the same assignment more than once*/
                    if(studentList.get(position).get("studentId").equals(assignments.get("studentId")) && assignments.get("isComplete").equals("0")){
                        int duration = Toast.LENGTH_LONG;
                        CharSequence alert = "Student already assigned - Please remove first";
                        Toast toast = Toast.makeText(context, alert, duration);
                        toast.show();
                        return;
                    }
                }
                /*If the student is not assigned or has completed previous assignments the datePicker method is called
                * with parameters offset: currentTime minus a minute and the DatePickerCallback interface*/
                datePicker((System.currentTimeMillis()-60000)/1000, new DatePickerCallback() {
                    @Override
                    public void dateSelected(final Long from) {
                        if(from != null){
                            /*When the date has been selected and is not null another dialog is shown with the selected date as offset*/
                            datePicker(from, new DatePickerCallback() {
                                @Override
                                public void dateSelected(Long to) {
                                    if(to != null){
                                        /*if the second datepicker is completed the student is added to the assigned list*/
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

        /*Setting OnItemClickListener for the assigned ListView
        * When clicking a student, the student is removed from the list and the assignment is deleted*/
        lvDialogAssigned.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                /*Creating a confirmation dialog*/
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm")
                        .setMessage("Are you sure that you want to delete the assignment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*If the dialogresponse is positive i.e. the assignment should be deleted*/
                                if(assignedList.get(position).get("assignmentid") != null) {
                                    /*if the assignment hasn't been assigned an ID yet i.e. it is new, it is removed from the database*/
                                    String assignmentId = assignedList.get(position).get("assignmentid");
                                    new AssignmentTask(new Callback() {
                                        @Override
                                        public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

                                        }
                                    }, context).executeTask("delete", "", "", "", "", assignmentId);
                                }
                                /*the assignment is removed from the listview and the adapter is notified*/
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
        /*Setting an OnClickListener for the Save button*/
        bDialogAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Iterating through the list of assignments*/
                for(int i = 0; i<assignedList.size();i++){
                    /*Getting information for the position in the list*/
                    Map<String, String> assignment = assignedList.get(i);
                    if(assignment.get("new") != null){
                        /*If the assignment is new, an AssignmentTask is created, adding it to the Database*/
                        new AssignmentTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

                            }
                        },context).executeTask("assign",assignment.get("studentId"),assignmentLibId,assignment.get("availableFrom"),assignment.get("availableTo"),"");
                    }
                }
                /*Reloading the contentPane*/
                setContentPane(assignmentListPos);
                /*Dismissing the dialog*/
                dialog.dismiss();
            }
        });
    }

    /**
     * Getting all assignments related to an assignmentLibraryId
     * @param assignmentLibId the assignmentLibraryId to get assignments for.
     * @return HashMap<String, HashMap<String, String>> of assignments containing the information needed to know which assignments has been completed etc.
     */
    private HashMap<String, HashMap<String,String>> getAssignments(String assignmentLibId){
        try {
           /*Creating an AssignmentTask which returns the HashMap - This method freezes the UI to wait for response for 15 seconds
           * Afterwards it throws an exception */
            return new AssignmentTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> assignments) {

                }
            },context).execute("get","",assignmentLibId,"","","").get(15, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method for designing the BarChart - Setting various design elements within the chart
     * e.g. animation time, background grid etc.
     */
    private void barChart(){
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
        /*Setting an OnChartValueSelectedListener*/
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {

                /*Getting the assignmentId from the xVals*/
                String assignmentId = assignmentIds.get(entry.getXIndex());

                /*Calling the statDialog method with the assignmentId as parameter*/
                statDialog(assignmentId);
            }

            @Override
            public void onNothingSelected() {
                //Auto generated stub
            }
        });
    }

    /**
     *getResult method - used for getting the results for every assignment associated with an assignmentLibrary entry
     * @param assignmentLibId - the assignmentLibraryId to get results for.
     * @return a HashMap of results for the assignment
     * The HashMap contains a HashMap which contains a third HashMap
     * The structure for the maps is:
     * AssignmentID->HashMap containing results for each question in the assignment->HashMap containing information about the question answered e.g. correctness and answer chosen
     */
    private HashMap<String, HashMap<String, HashMap<String, String>>> getResult(String assignmentLibId){
        try {
            /*Returns the HashMap - This method freezes the UI to make sure that all results are in before continuing */
            return new QuestionResultTask(context).execute("","","","","","","","get",assignmentLibId).get(30,TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * statDialog - used for showing detailed statistics for an assignment compared with other assignments
     * @param assignmentId - the id to show statistics for
     */
    private void statDialog(String assignmentId){
        /*Create the dialog*/
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_stats, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        dialog.show();

        /*Setting up UI elements*/
        ListView lvDialogQuestions = (ListView) layout.findViewById(R.id.lvDialogQuestions);
        final PieChart chartDialog = (PieChart) layout.findViewById(R.id.chartDialog);
        TextView tvDialogTime = (TextView) layout.findViewById(R.id.tvDialogTime);
        TextView tvDialogAverageTime = (TextView) layout.findViewById(R.id.tvDialogAverageTime);
        TextView tvDialogCorrect = (TextView) layout.findViewById(R.id.tvDialogCorrect);
        TextView tvDialogCorrectAverage = (TextView) layout.findViewById(R.id.tvDialogCorrectAverage);

        /*creating a list for containing the questions*/
        final List<Map<String, String>> questionList = new ArrayList<>();
        /*Creating a new adapter from the custom QuestionListAdapterClass
        * Used for coloring the answer in the listview depending on the correctness*/
        QuestionListAdapter questionListAdapter = new QuestionListAdapter(this,questionList);
        /*Setting the adapter*/
        lvDialogQuestions.setAdapter(questionListAdapter);

        /*Designing the PieChart - setting various variables e.g. center hole size, animation etc.*/
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

        /*Setting variables used for calculating student's average*/
        int studentCorrect = 0;
        int studentAnswers = 0;
        /*Getting the result from the HashMap of all results*/
        HashMap<String, HashMap<String, String>> studentResult = result.get(assignmentId);
        /*Itereating through the assignment's results*/
        for(Map.Entry<String, HashMap<String, String>> studentResults : studentResult.entrySet()){
            /*If the key in the result is not time*/
            if(!studentResults.getKey().equals("time")){
                /*Checking the correctness of the answer*/
                if(studentResults.getValue().get("correct").equals("1")){
                    /*If the answer is correct, increase studentCorrect by 1*/
                    studentCorrect++;
                }
                /*Increase studentAnswers by 1 regardless of correctness*/
                studentAnswers++;
            }
        }
        /*Calculate studentAverage*/
        double studentAverage = ((double) studentCorrect / (double) studentAnswers) * 100;

        /*Format the average to one decimal and adding a %-sign */
        String studentAverageString = String.format("%.1f",studentAverage) + "%";


        /*Setting variables for calculating the average time and average correctness*/
        double totalTime = 0;
        double total = 0;
        double assignments = assignmentIds.size();

        /*Iterating the list of assignments to calculate from*/
        for(int i=0; i<assignmentIds.size(); i++){
            /*Getting information for a specific assignment*/
            HashMap<String, HashMap<String, String>> assignmentResult = result.get(assignmentIds.get(i));

            /*Adding the time to the totalTime*/
            totalTime = totalTime+Double.parseDouble(assignmentResult.get("time").get("time"));

            /*Settign variables for calculating an assignment's answers*/
            int assignmentAnswers = 0;
            int assignmentCorrect = 0;

            /*Iterating through the assignments result*/
            for(Map.Entry<String, HashMap<String, String>> result : assignmentResult.entrySet()){
                /*if the key is not time*/
                if(!result.getKey().equals("time")){
                    if(result.getValue().get("correct").equals("1")){
                        /*If the result is correct, increase assignmentCorret by 1*/
                        assignmentCorrect++;
                    }
                    /*Increases assignmentAnswers by 1*/
                    assignmentAnswers++;
                }
            }
            /*Calculating the average for a specific assignment*/
            double assignmentAverage = ((double) assignmentCorrect / (double) assignmentAnswers) * 100;

            /*Adding to the total*/
            total = total+assignmentAverage;
        }
        /*Calculating the average correctness and average time*/
        double average = total/assignments;
        double averageTime = totalTime/assignments;

        /*Setting the student's average TextView*/
        tvDialogCorrect.setText(studentAverageString);

        /*Getting the student's time and setting the TextView after the time is converted to a string in the convertTime method*/
        int studentTime = Integer.parseInt(studentResult.get("time").get("time"));
        tvDialogTime.setText(convertTime(studentTime));

        /*Calculating the differnce between the student's time and the average time and setting TextView accordingly*/
        if(studentTime < averageTime){
            double difference = ((averageTime-studentTime)/studentTime)*100;
            tvDialogAverageTime.setText(Math.round(difference)+"% below average");
        } else if(studentTime > averageTime){
            double difference = ((studentTime-averageTime)/averageTime)*100;
            tvDialogAverageTime.setText(Math.round(difference)+"% above average");
        } else {
            tvDialogAverageTime.setText("On average");
        }

        /*Calculating the difference between the student's average and the general average  and setting TextView accordingly*/
        if(studentAverage < average){
            double difference = ((average-studentAverage)/studentAverage)*100;
            tvDialogCorrectAverage.setText(Math.round(difference)+"% below average");
        } else if(studentAverage > average){
            double difference = ((studentAverage-average)/average)*100;
            tvDialogCorrectAverage.setText(Math.round(difference)+"% above average");
        } else {
            tvDialogCorrectAverage.setText("On average");
        }

        /*Iterating the questionList to add the questions to the list of questions and correlate the student's results*/
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
        /*Setting an OnItemClickListener for the QuestionList - used for calculating the number of correct answers for a specific question
        * and setting the PieChart accordingly*/
        lvDialogQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Getting the questionId and setting the variables for calculating*/
                String questionId = questionList.get(position).get("id");
                int totalAnswers = 0;
                int correctAnswers = 0;

                /*Iterating through the assignments and getting the correctness for the question chosen*/
                for(int i=0; i<assignmentIds.size(); i++){
                    HashMap<String, HashMap<String, String>> assignmentResult = result.get(assignmentIds.get(i));
                    if(assignmentResult.get(questionId).get("correct").equals("1")){
                        correctAnswers++;
                    }
                    totalAnswers++;
                }
                /*Calculating the percentage of correct and wrong answers*/
                double correct = ((double) correctAnswers / (double) totalAnswers) * 100;
                double wrong = 100-correct;

                /*Creating ArrayList for containing PieChart Data*/
                ArrayList<Entry> pieValues = new ArrayList<>();
                ArrayList<String> pieNames = new ArrayList<>();

                /*Adding the correct answers to the chart*/
                pieValues.add(new Entry((float) correct,0));
                pieNames.add(0,"Correct answers");

                /*Adding the wrong answers to the chart*/
                pieValues.add(new Entry((float) wrong,1));
                pieNames.add(1,"Wrong answers");

                /*Adding the data to the chart*/
                PieDataSet dataSet = new PieDataSet(pieValues, "");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                /*Adding red as the color for wrong answers and green for right answers*/
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.GREEN);
                colors.add(Color.RED);
                dataSet.setColors(colors);

                /*Setting the data and various design variables*/
                PieData pieData = new PieData(pieNames, dataSet);
                pieData.setValueFormatter(new PercentFormatter());
                pieData.setValueTextSize(11f);
                pieData.setValueTextColor(Color.WHITE);

                /*Sending the data to the Chart and redraw it*/
                chartDialog.setData(pieData);

                chartDialog.highlightValues(null);

                chartDialog.invalidate();

            }
        });
    }

    /**
     *
     * @param time - time in seconds
     * @return String - time converted to format HH:MM:SS
     */
    private String convertTime(int time){

        /*Calculating the number of hours, minutes and seconds*/
        int hour = time/3600;
        int remainder = time - hour*3600;
        int minute = remainder/60;
        remainder = remainder - minute * 60;
        int second = remainder;

        /*Converting to strings and adding leading zeros if necessary*/
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
        return hours+":"+minutes+":"+seconds;
    }

}