package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Importing color scheme from Resource Files: res/values/colors.xml

        int BackgroundColor = ContextCompat.getColor(this, R.color.Background);
        int DataPointsColor = ContextCompat.getColor(this, R.color.DataPointVal);
        int ProgBarColor    = ContextCompat.getColor(this,R.color.ProgBar);
        int GridColor       = ContextCompat.getColor(this,R.color.Grid);

        // ------------------------------- Progress Bar -------------------------------

        ProgressBar stepsBar = (ProgressBar) findViewById(R.id.progBarSteps);
        ProgressBar calsBar = (ProgressBar) findViewById(R.id.progBarCal);
        ProgressBar distBar = (ProgressBar) findViewById(R.id.progBarDist);
        ProgressBar timeBar = (ProgressBar) findViewById(R.id.progBarTime);

        TextView stepsT = (TextView) findViewById(R.id.stepsTxt);

        stepsBar.setProgress(65);
        calsBar.setProgress(50);
        distBar.setProgress(70);
        timeBar.setProgress(30);

        // ------------------------------- Weekly Chart -------------------------------

        LineChart chart = (LineChart) findViewById(R.id.graph);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 20));
        yValues.add(new Entry(1, 30));
        yValues.add(new Entry(2, 10));
        yValues.add(new Entry(3, 40));
        yValues.add(new Entry(4, 40));
        yValues.add(new Entry(5, 50));
        yValues.add(new Entry(6, 30));

        LineDataSet weekSteps = new LineDataSet(yValues, "Weekly Steps");

        // Text point values

        weekSteps.setValueTextSize(16);
        weekSteps.setValueTextColor(DataPointsColor);

        // Line

        weekSteps.enableDashedLine(10,10,0);
        weekSteps.setLineWidth(6);

        weekSteps.setColor(ProgBarColor);

        // Inner circle of the points

        weekSteps.setDrawCircles(true);
        weekSteps.setCircleRadius(8f);
        weekSteps.setCircleColor(DataPointsColor);

        // Outer circle of the points

        weekSteps.setDrawCircleHole(true);
        weekSteps.setCircleHoleRadius(5f);
        weekSteps.setCircleHoleColor(Color.BLACK);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(weekSteps);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.setExtraTopOffset(2);

        // Axis formatting

            // X Axis

        XAxis xAx = chart.getXAxis();
        xAx.setTextSize(20);
        xAx.setTextColor(DataPointsColor);
        xAx.setAvoidFirstLastClipping(true);
        xAx.setGridColor(GridColor);
        xAx.setGridLineWidth(1);
        xAx.setDrawGridLinesBehindData(true);
        xAx.setDrawLimitLinesBehindData(true);

            // Left Y Axis

        float thresh = (float) data.getYMax()+10; // Y axis threshold

        LimitLine upperLim = new LimitLine(thresh);
        upperLim.setLineWidth(3);
        upperLim.setLineColor(DataPointsColor);
        upperLim.enableDashedLine(10f,10f,0f);
        upperLim.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        YAxis LeftyAx = chart.getAxisLeft();
        LeftyAx.setDrawAxisLine(false);
        LeftyAx.setDrawLabels(false);
        LeftyAx.setGridColor(BackgroundColor);
        LeftyAx.setAxisMaximum(thresh);
        LeftyAx.setAxisMinimum(data.getYMin()-3);
        LeftyAx.addLimitLine(upperLim);


            // Right Y Axis

        YAxis RightyAx = chart.getAxisRight();
        RightyAx.setDrawAxisLine(false);
        RightyAx.setDrawLabels(false);
        RightyAx.setGridColor(BackgroundColor);

        // Area under the graph

        weekSteps.setDrawFilled(true);

            // Fade of fill

        if(Utils.getSDKInt() >= 18){
            System.out.println("SDK version: " + Utils.getSDKInt());

            // Fill  drawable only supported since level 18 API
            // Gradient color file: fade_red.xml

            Drawable fadeFill = ContextCompat.getDrawable(this, R.drawable.fade_red);
            try {
                fadeFill.setAlpha(40);
                weekSteps.setFillDrawable(fadeFill);
            }catch(NumberFormatException ex){
                System.out.println("Exception: " + ex.getMessage()); // To print exception error message
            }
        }else{
            weekSteps.setFillColor(DataPointsColor);
        }

    }
    public void openGoalsPage(View view){
        Intent i = new Intent(getApplicationContext(), GoalsActivity.class);
        startActivity(i);
    }
}