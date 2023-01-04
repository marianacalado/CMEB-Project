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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GraphDisplay extends ContextWrapper {

    // Class to hold all the graph formatting

    private int BackgroundColor = ContextCompat.getColor(this, R.color.Background);
    private int GridColor       = ContextCompat.getColor(this,R.color.Grid);

    private LineData lineData;
    private YAxis LeftyAx;

    public GraphDisplay(Context base) { // Adds context to the class
        super(base);
    } // Sets the context of the class

    public LineDataSet chartSetUp(LineChart chart, ArrayList<Entry> data, int dataPointsColor, int lineColor, int int_OR_float){


        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        // Disable legend and description below the graph

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        chart.getDescription().setEnabled(false);

        LineDataSet week = new LineDataSet(data, "");
        //To make the smooth line
        week.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //To enable the cubic density : if 1 then it will be sharp curve
        week.setCubicIntensity(0.2f);

        // Text point values

        week.setValueTextSize(14);
        week.setValueTextColor(dataPointsColor);

        // Line

        week.enableDashedLine(10,10,0);
        week.setLineWidth(6);

        week.setColor(lineColor);

        // Inner circle of the points

        week.setDrawCircles(true);
        week.setCircleRadius(8f);
        week.setCircleColor(dataPointsColor);

        // Outer circle of the points

        week.setDrawCircleHole(true);
        week.setCircleHoleRadius(5f);
        week.setCircleHoleColor(Color.BLACK);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(week);

        lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.setExtraTopOffset(2);

        // Axis formatting

        // X Axis

        XAxis xAx = chart.getXAxis();
        xAx.setTextSize(17);
        xAx.setTextColor(dataPointsColor);
        xAx.setAvoidFirstLastClipping(true);
        xAx.setGridColor(GridColor);
        xAx.setGridLineWidth(1);
        xAx.setDrawGridLinesBehindData(true);
        xAx.setDrawLimitLinesBehindData(true);

        // Right Y Axis

        YAxis RightyAx = chart.getAxisRight();
        RightyAx.setDrawAxisLine(false);
        RightyAx.setDrawLabels(false);
        RightyAx.setGridColor(BackgroundColor);

        // Left Y Axis

        LeftyAx = chart.getAxisLeft();
        LeftyAx.setDrawAxisLine(false);
        LeftyAx.setDrawLabels(false);
        LeftyAx.setGridColor(BackgroundColor);

        // Week strings for the xAxis

        DateFormat format = new SimpleDateFormat("EE, dd/MM/yyyy", Locale.UK);
        Calendar today = Calendar.getInstance();

        String[] days = new String[7];
        int idx = format.format(today.getTime()).indexOf(",");
        for (int i = 6; i >= 0; i--)
        {
            days[i] = format.format(today.getTime()).substring(0,idx);
            today.add(Calendar.DAY_OF_MONTH, -1); // -1 means it will go from the current day one by one to seven days in the past.
                                                     // 1 would do the same but in the future.
                                                     // Because of this the for loop has to be in reverse
            //System.out.println(days[i]);
        }

        xAx.setValueFormatter(new IndexAxisValueFormatter(days)); // Sets the upper X labels

        if (int_OR_float == 1) // Condition to sets the values on the chart int values (Steps)
        {
            ValueFormatter vf = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "" + (int) value;
                }
            };

            week.setValueFormatter(vf);
        }

        return week;


    }

    public void graphFade(LineDataSet week, int fadeDrawable, int defaultColor){

        week.setDrawFilled(true);

        // Fade of fill

        if(Utils.getSDKInt() >= 18){
            System.out.println("SDK version: " + Utils.getSDKInt());

            // Fill  drawable only supported since level 18 API
            // Gradient color file: fade_red.xml

            Drawable fadeFill = ContextCompat.getDrawable(this, fadeDrawable);
            try {
                fadeFill.setAlpha(80);
                week.setFillDrawable(fadeFill);

            }catch(NumberFormatException ex){
                System.out.println("Exception: " + ex.getMessage()); // To print exception error message
            }
        }else{
            week.setFillColor(defaultColor);
        }
    }

    public void updateUpperThreshold(int currMax, int dataPointsColor) {

        float perc = (float) 0.35;
        float aux = lineData.getYMax() + Math.round(perc * lineData.getYMax());
        float threshAbove;


        if (currMax >= aux - currMax * perc) {
            threshAbove = currMax + currMax * perc;
        } else {
            threshAbove = aux;
        }


        LimitLine upperLim = new LimitLine(threshAbove);
        upperLim.setLineWidth(3);
        upperLim.setLineColor(dataPointsColor);
        upperLim.enableDashedLine(10f, 10f, 0f);
        upperLim.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        LeftyAx.setAxisMaximum(threshAbove);
        LeftyAx.removeAllLimitLines();
        LeftyAx.addLimitLine(upperLim);

        float threshBelow = lineData.getYMin() - Math.round(0.10 * lineData.getYMax()); // Y axis lower threshold is always 10% smaller than the max value
        LeftyAx.setAxisMinimum(threshBelow);

    }

}
