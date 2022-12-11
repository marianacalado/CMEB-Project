package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class GraphPage extends AppCompatActivity {

    private int dayIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_page);

        // Importing color scheme from Resource Files: res/values/colors.xml

        int StepsDataPointsColor = ContextCompat.getColor(this, R.color.DataPointVal);
        int ProgBarColor         = ContextCompat.getColor(this,R.color.MainColor);
        int DistDataPointsColor = ContextCompat.getColor(this, R.color.DistDataPoint);
        int DistLineColor       = ContextCompat.getColor(this,R.color.DistLine);
        int TimeDataPointsColor = ContextCompat.getColor(this, R.color.TimeDataPoint);
        int TimeLineColor       = ContextCompat.getColor(this,R.color.TimeLine);

        /* ------------------------------- Guidelines ------------------------------- */

        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0); // Action bar height

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int h = displayMetrics.heightPixels - mActionBarSize;

        Guideline horizontalG1 = findViewById(R.id.guideline13);
        Guideline horizontalG2 = findViewById(R.id.guideline14);
        horizontalG1.setGuidelineBegin(Math.round(h/3)); // Puts first guideline at 1/3 of the current running phone width
        horizontalG2.setGuidelineBegin(Math.round(h*2/3)); // Puts second guideline at 2/3 of the current running phone width

        /* ------------------------------- Weekly Chart ------------------------------- */

        // Calories

        ArrayList<Entry> calories = new ArrayList<>();

        calories.add(new Entry(0, 26));
        calories.add(new Entry(1, 350));
        calories.add(new Entry(2, 135));
        calories.add(new Entry(3, 45));
        calories.add(new Entry(4, 4));
        calories.add(new Entry(5, 798));
        calories.add(new Entry(6, 278));

        // Distance

        ArrayList<Entry> distance = new ArrayList<>();

        distance.add(new Entry(0, (float) 0.4));
        distance.add(new Entry(1, (float) 2.7));
        distance.add(new Entry(2, (float) 1.40));
        distance.add(new Entry(3, (float) 0.8));
        distance.add(new Entry(4, (float) 0.11));
        distance.add(new Entry(5, (float) 5.8));
        distance.add(new Entry(6, (float) 0.37));

        // Time

        ArrayList<Entry> time = new ArrayList<>();

        time.add(new Entry(0, 2));
        time.add(new Entry(1, 3));
        time.add(new Entry(2, 1));
        time.add(new Entry(3, 4));
        time.add(new Entry(4, 4));
        time.add(new Entry(5, 5));
        time.add(new Entry(6, 3));

        LineChart calG = (LineChart) findViewById(R.id.calGraph);
        LineChart distG = (LineChart) findViewById(R.id.distGraph);
        LineChart timeG = (LineChart) findViewById(R.id.timeGraph);

        GraphDisplay graphCal = new GraphDisplay(this);
        LineDataSet weekCalories = graphCal.chartSetUp(calG,calories, StepsDataPointsColor, ProgBarColor);
        graphCal.graphFade(weekCalories,R.drawable.fade_red, StepsDataPointsColor);

        GraphDisplay graphDist = new GraphDisplay(this);
        LineDataSet weekDistance = graphDist.chartSetUp(distG,distance, DistDataPointsColor, DistLineColor);
        graphDist.graphFade(weekDistance,R.drawable.fade_blue, DistDataPointsColor);

        GraphDisplay graphTm = new GraphDisplay(this);
        LineDataSet weekTime = graphTm.chartSetUp(timeG,time,TimeDataPointsColor, TimeLineColor);
        graphTm.graphFade(weekTime,R.drawable.fade_green, TimeDataPointsColor);

    }

}