package com.example.svilen.p8;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        BarChart mChart = (BarChart) findViewById(R.id.chart);
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);

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


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();
        yVals1.add(new BarEntry(10, 0));
        yVals1.add(new BarEntry(20, 1));
        yVals1.add(new BarEntry(100,2));
        yVals1.add(new BarEntry(50, 3));
        yVals1.add(new BarEntry(75, 4));
        xVals.add("assignment 1");
        xVals.add("assignemnt 2");
        xVals.add("assignemnt 3");
        xVals.add("assignemnt 4");
        xVals.add("assignemnt 5");



        BarDataSet set1 = new BarDataSet(yVals1, "Data Set");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setDrawValues(true);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        mChart.setData(data);


    }
}