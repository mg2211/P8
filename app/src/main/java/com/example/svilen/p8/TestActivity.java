package com.example.svilen.p8;

import android.content.Context;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    Button bClasses;
    Button bAssignments;
    Button bTexts;
    Button bUsers;
    Button bLogOut;
    Context context;
    ArrayList<Integer> assignments = new ArrayList<>();
    ArrayList<BarEntry> yVal = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setButtons();
        addData(5);
        bClasses = (Button) findViewById(R.id.bClasses);
        bAssignments = (Button) findViewById(R.id.bAssignments);
        bTexts = (Button) findViewById(R.id.bTexts);
        bUsers = (Button) findViewById(R.id.bUsers);
        bLogOut = (Button) findViewById(R.id.bLogOut);
        context = this;

        bClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code for opening class list activity
            }
        });

        bAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open assignments activity
            }
        });

        bTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open text activity
            }
        });

        bUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open user management activity
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

        mChart.getAxisLeft().setDrawGridLines(false);

        yVal.add(new BarEntry(10, 0));
        yVal.add(new BarEntry(20, 1));
        yVal.add(new BarEntry(100, 2));
        yVal.add(new BarEntry(50, 3));
        yVal.add(new BarEntry(75, 4));

        BarDataSet set1 = new BarDataSet(yVal, "Assignments");
        colors.add(Color.rgb(255, 0, 0));
        colors.add(Color.rgb(0, 0, 0));
        colors.add(Color.rgb(0, 255, 0));
        colors.add(Color.rgb(0, 0, 255));
        colors.add(Color.rgb(255, 255, 255));


        set1.setColors(colors);


        xVals.add("Assignment 1");
        xVals.add("Assignment 2");
        xVals.add("Assignment 3");
        xVals.add("Assignment 4");
        xVals.add("Assignment 5");

        assignments.add(1);
        assignments.add(2);
        assignments.add(3);
        assignments.add(4);
        assignments.add(5);

        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        mChart.setData(data);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                Log.d("...", String.valueOf(assignments.get(entry.getXIndex())));
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
            if(randomnumber >= 50){
                colors.add(Color.rgb(255, 235, 69));
            } else if(randomnumber == 100){
                colors.add(Color.rgb(156,204,101));
            } else {
                colors.add(Color.rgb(239,83,80));
            }
            xVals.add("Assignment "+n);
        }


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
}