package com.example.stepncount;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class GraphDisplay extends ContextWrapper {


    public GraphDisplay(Context base) {
        super(base);
    }

    // Class to hold all the graph formatting

    public LineDataSet chartSetUp(LineChart chart, ArrayList<Entry> data){

        int BackgroundColor = ContextCompat.getColor(this, R.color.Background);
        int DataPointsColor = ContextCompat.getColor(this, R.color.DataPointVal);
        int ProgBarColor    = ContextCompat.getColor(this,R.color.MainColor);
        int GridColor       = ContextCompat.getColor(this,R.color.Grid);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        // Disable legend below the graph

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        LineDataSet week = new LineDataSet(data, "");
        //To make the smooth line
        week.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //To enable the cubic density : if 1 then it will be sharp curve
        week.setCubicIntensity(0.2f);

        // Text point values

        week.setValueTextSize(16);
        week.setValueTextColor(DataPointsColor);

        // Line

        week.enableDashedLine(10,10,0);
        week.setLineWidth(6);

        week.setColor(ProgBarColor);

        // Inner circle of the points

        week.setDrawCircles(true);
        week.setCircleRadius(8f);
        week.setCircleColor(DataPointsColor);

        // Outer circle of the points

        week.setDrawCircleHole(true);
        week.setCircleHoleRadius(5f);
        week.setCircleHoleColor(Color.BLACK);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(week);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
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

        float threshAbove = (float) lineData.getYMax() + Math.round(0.30*lineData.getYMax()); // Y axis upper threshold is always 22% higher than the max value
        float threshBelow = (float) lineData.getYMin() - Math.round(0.10*lineData.getYMax()); // Y axis lower threshold is always 10% smaller than the max value

        LimitLine upperLim = new LimitLine(threshAbove);
        upperLim.setLineWidth(3);
        upperLim.setLineColor(DataPointsColor);
        upperLim.enableDashedLine(10f,10f,0f);
        upperLim.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        YAxis LeftyAx = chart.getAxisLeft();
        LeftyAx.setDrawAxisLine(false);
        LeftyAx.setDrawLabels(false);
        LeftyAx.setGridColor(BackgroundColor);
        LeftyAx.setAxisMaximum(threshAbove);
        LeftyAx.setAxisMinimum(threshBelow);
        LeftyAx.addLimitLine(upperLim);

        // Right Y Axis

        YAxis RightyAx = chart.getAxisRight();
        RightyAx.setDrawAxisLine(false);
        RightyAx.setDrawLabels(false);
        RightyAx.setGridColor(BackgroundColor);

        // Area under the graph

        week.setDrawFilled(true);

        // Fade of fill

        if(Utils.getSDKInt() >= 18){
            System.out.println("SDK version: " + Utils.getSDKInt());

            // Fill  drawable only supported since level 18 API
            // Gradient color file: fade_red.xml

            Drawable fadeFill = ContextCompat.getDrawable(this, R.drawable.fade_red);
            try {
                fadeFill.setAlpha(80);
                week.setFillDrawable(fadeFill);

            }catch(NumberFormatException ex){
                System.out.println("Exception: " + ex.getMessage()); // To print exception error message
            }
        }else{
            week.setFillColor(DataPointsColor);
        }

        return week;

    }


}
