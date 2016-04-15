package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;

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

public class TeacherActivity extends AppCompatActivity {

    Button bClasses;
    Button bAssignments;
    Button bTexts;
    Button bUsers;
    Button bLogOut;
    Context context = this;
    UserInfo userinfo;
    HashMap<String, String> user;
    ArrayList<Integer> assignments = new ArrayList<>();
    ArrayList<BarEntry> yVal = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        setButtons();
        addData(5);
        bClasses = (Button) findViewById(R.id.bClasses);
        bAssignments = (Button) findViewById(R.id.bAssignments);
        bTexts = (Button) findViewById(R.id.bTexts);
        bUsers = (Button) findViewById(R.id.bUsers);
        bLogOut = (Button) findViewById(R.id.bLogOut);

        bClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassActivity.class);
                startActivity(intent);
            }
        });

        bAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent (TeacherActivity.this, AssignmentActivity.class);
                startActivity(intent);

            }
        });

        bTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TextActivity.class);
                startActivity(intent);
            }
        });

        bUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, Register.class);
                startActivity(intent);
                //new UserTask().execute("");
            }
        });


        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserInfo(context).logOut();
            }
        });

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

    private void setButtons(){
        ArrayList<Integer> buttonIds = new ArrayList<>();
        buttonIds.add(R.id.bClasses);
        buttonIds.add(R.id.bAssignments);
        buttonIds.add(R.id.bLogOut);
        buttonIds.add(R.id.bUsers);
        buttonIds.add(R.id.bTexts);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        for(int i = 0; i < buttonIds.size(); i++){
            Button button = (Button) findViewById(buttonIds.get(i));
            int buttonHeight = height/buttonIds.size();
            button.setHeight(buttonHeight);
        }
    }
    @Override
    public void onBackPressed() {
        Log.d("Back button pressed", " -Disabled");
    }

}