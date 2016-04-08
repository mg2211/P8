package com.example.svilen.p8;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        BarChart testChart = new BarChart(this);
        RelativeLayout content = (RelativeLayout) findViewById(R.id.chartLayout);
        testChart.setBackgroundColor(Color.BLUE);
        testChart.setLogEnabled(true);
        content.addView(testChart);


    }
}