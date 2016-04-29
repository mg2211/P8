package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentActivity extends AppCompatActivity {

    Context context = this;
    UserInfo userInfo;
    HashMap<String, String> user;
    String teacherId;

    Button bAddAssignment;
    Button bSave;
    EditText etSearch;
    EditText etAssignmentText;
    EditText etAssignmentName;
    ListView lvAssignments;
    SimpleAdapter assignmentAdapter;
    List<Map<String, String>> assignmentList = new ArrayList<>();
    List<Map<String, String>> textList = new ArrayList<>();
    SimpleAdapter textAdapter;
    int assignmentTextId;
    String assignmentId;
    int dialogSelected;
    ArrayList<BarEntry> yVal = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    boolean newAssignment = true;
    boolean changed;
    HashMap<Integer, Integer> textListIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        userInfo = new UserInfo(context);
        user = userInfo.getUser();
        teacherId = user.get("teacherId");
        getTexts();
        getAssignments();
        barChart();
        addData(5);
        lvAssignments = (ListView) findViewById(R.id.lvAssignments);
        bAddAssignment = (Button) findViewById(R.id.bAddAssignment);
        etAssignmentName = (EditText) findViewById(R.id.etAssignmentName);
        etSearch = (EditText) findViewById(R.id.etSearch);
        etAssignmentText = (EditText) findViewById(R.id.etAssignmentText);
        bSave = (Button) findViewById(R.id.bSave);

        assignmentAdapter= new SimpleAdapter(this, assignmentList,
                android.R.layout.simple_list_item_1,
                new String[]{"assignmentName"},
                new int[]{android.R.id.text1});
        lvAssignments.setAdapter(assignmentAdapter);


        textAdapter = new SimpleAdapter(this, textList,
                android.R.layout.simple_list_item_1,
                new String[]{"textname"},
                new int[]{android.R.id.text1});

        //content pane
        etAssignmentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(assignmentTextId);
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(assignmentTextId != 0 && !etAssignmentName.getText().toString().equals("")) {
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
                //create new assignment
            }
        });
        lvAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//view assignment
                assignmentTextId = Integer.parseInt(assignmentList.get(position).get("assignmentText"));
                int textListPos = textListIds.get(assignmentTextId);
                etAssignmentName.setText(assignmentList.get(position).get("assignmentName"));
                assignmentId = assignmentList.get(position).get("assignmentId");
                etAssignmentText.setText(textList.get(textListPos).get("textname"));
            }
        });
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
                assignmentTextId = dialogSelected;
                dialog.dismiss();
                Log.d("assignmentTextId", String.valueOf(assignmentTextId));
            }
        });
        bDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.d("assignmentTextId", String.valueOf(assignmentTextId));
            }
        });
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

                    assignmentInfo.put("assignmentId",assignmentId);
                    assignmentInfo.put("assignmentName",assignmentName);
                    assignmentInfo.put("assignmentText",assignmentText);
                    assignmentList.add(assignmentInfo);
                }
                assignmentAdapter.notifyDataSetChanged();
            }
        },context).executeTask("get",teacherId,"","","");
    }
    private void addData(int i) {
        colors.clear();
        for(int n = 0; n<i; n++){
            int randomnumber = (int)(Math.random() * 101);
            yVal.add(new BarEntry(randomnumber, n));
            if(randomnumber >= 50 && randomnumber <= 75){
                colors.add(Color.rgb(255, 235, 69));
            } else if(randomnumber > 75){
                colors.add(Color.rgb(156,204,101));
            } else {
                colors.add(Color.rgb(239,83,80));
            }
            xVals.add("Assignment "+n);
        }
        BarDataSet set1 = new BarDataSet(yVal, "Assignments");
        set1.setColors(colors);
        dataSets.add(set1);
    }
    private void setChanged(boolean value){
        changed = value;
        Log.d("Changed value", String.valueOf(changed));
    }
    private void setNew(boolean value){
        newAssignment = value;
        Log.d("New", String.valueOf(newAssignment));
    }
    private void barChart(){
        //design barChart
        BarChart mChart = (BarChart) findViewById(R.id.chart);
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
    private void createAssignment(){
        new AssignmentLibTask(new AssignmentLibCallback() {
            @Override
            public void AssignmentLibDone(HashMap<String, HashMap<String, String>> results) {

            }
        },context).executeTask("create",teacherId,"",etAssignmentName.getText().toString(), String.valueOf(assignmentTextId));
    }
    private void updateAssignment(){
        new AssignmentLibTask(new AssignmentLibCallback() {
            @Override
            public void AssignmentLibDone(HashMap<String, HashMap<String, String>> results) {

            }
        },context).executeTask("update",teacherId,assignmentId,etAssignmentName.getText().toString(), String.valueOf(assignmentTextId));
    }
}
