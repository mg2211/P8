package com.example.svilen.p8;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    Button bClasses;
    Button bAssignments;
    Button bTexts;
    Button bUsers;
    Button bLogOut;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setButtons();
        bClasses = (Button) findViewById(R.id.bClasses);
        bAssignments = (Button) findViewById(R.id.bAssignments);
        bTexts = (Button) findViewById(R.id.bTexts);
        bUsers = (Button) findViewById(R.id.bUsers);
        bLogOut = (Button) findViewById(R.id.bLogOut);
        context = this;


        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserInfo(context).logOut();
            }
        });



        BarChart mChart = (BarChart) findViewById(R.id.chart);
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setScaleEnabled(false);

        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        mChart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        mChart.animateY(1250);

        mChart.getLegend().setEnabled(false);


       /* ArrayList<BarEntry> ass1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> ass2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> ass3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> ass4 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> ass5 = new ArrayList<BarEntry>();


        ass1.add(new BarEntry(10, 0));
        ass2.add(new BarEntry(20, 1));
        ass3.add(new BarEntry(100,2));
        ass4.add(new BarEntry(50, 3));
        ass5.add(new BarEntry(75, 4));*/


        ArrayList<BarEntry> yVal = new ArrayList<>();
        yVal.add(new BarEntry(10, 0));
        yVal.add(new BarEntry(20, 1));
        yVal.add(new BarEntry(100, 2));
        yVal.add(new BarEntry(50, 3));
        yVal.add(new BarEntry(75, 4));

        BarDataSet set1 = new BarDataSet(yVal, "Assignments");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(255,0,0));
        colors.add(Color.rgb(0,0,0));
        colors.add(Color.rgb(0,255,0));
        colors.add(Color.rgb(0,0,255));
        colors.add(Color.rgb(255,255,255));


        set1.setColors(colors);

        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("Assignment 1");
        xVals.add("Assignment 2");
        xVals.add("Assignment 3");
        xVals.add("Assignment 4");
        xVals.add("Assignment 5");


/*
        BarDataSet set1 = new BarDataSet(ass1, "Data Set");
        BarDataSet set2 = new BarDataSet(ass2, "Data Set");
        BarDataSet set3 = new BarDataSet(ass3, "Data Set");
        BarDataSet set4 = new BarDataSet(ass4, "Data Set");
        BarDataSet set5 = new BarDataSet(ass5, "Data Set");


        set1.setColor(Color.parseColor("#EF5350"));
        set2.setColor(Color.parseColor("#EF5350"));
        set3.setColor(Color.parseColor("#8BC34A"));
        set4.setColor(Color.parseColor("#FFF176"));
        set5.setColor(Color.parseColor("#FFF176"));*/

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        mChart.setData(data);


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