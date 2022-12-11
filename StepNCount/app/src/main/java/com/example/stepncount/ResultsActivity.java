package com.example.stepncount;

import static com.example.stepncount.GoalsActivity.CAL_GOAL;
import static com.example.stepncount.GoalsActivity.DIST_GOAL;
import static com.example.stepncount.GoalsActivity.GOALS_PREFS;
import static com.example.stepncount.GoalsActivity.STEPS_GOAL;
import static com.example.stepncount.GoalsActivity.TIME_GOAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private ProgressBar stepsBar;
    private ProgressBar calsBar;
    private ProgressBar distBar;
    private ProgressBar timeBar;

    private int stepGoal;
    private int calGoal;
    private int distGoal;
    private int timeGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Goals
        SharedPreferences goalPref   = getSharedPreferences(GOALS_PREFS, MODE_PRIVATE);

        stepGoal = goalPref.getInt(STEPS_GOAL,10000);
        calGoal = goalPref.getInt(CAL_GOAL, 500);
        distGoal = goalPref.getInt(DIST_GOAL, 8);
        timeGoal = goalPref.getInt(TIME_GOAL, 1);

        // Importing color scheme from Resource Files: res/values/colors.xml

        int StepsDataPointsColor = ContextCompat.getColor(this, R.color.DataPointVal);
        int ProgBarColor         = ContextCompat.getColor(this,R.color.MainColor);

        /* ------------------------------- ProgressBar Views ------------------------------- */

        stepsBar = findViewById(R.id.progBarSteps);
        calsBar = findViewById(R.id.progBarCal);
        distBar = findViewById(R.id.progBarDist);
        timeBar = findViewById(R.id.progBarTime);

        /* ------------------------------- Week data ------------------------------- */

        // Steps

        ArrayList<Entry> steps = new ArrayList<>();

        steps.add(new Entry(0, 200));
        steps.add(new Entry(1, 3000));
        steps.add(new Entry(2, 1000));
        steps.add(new Entry(3, 400));
        steps.add(new Entry(4, 40));
        steps.add(new Entry(5, 5000));
        steps.add(new Entry(6, 350));

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

        /* ------------------------------- Text Views ------------------------------- */

        TextView stepsT = (TextView) findViewById(R.id.stepsTxt);
        TextView calT   = (TextView) findViewById(R.id.calTxt);
        TextView distT  = (TextView) findViewById(R.id.distTxt);
        TextView timeT  = (TextView) findViewById(R.id.timeTxt);

        // Setting TextViews and Progress with the latest day values

        int arraySz = steps.size()-1;

        float dayStep = steps.get(arraySz).getY();
        float dayCal  = calories.get(arraySz).getY();
        float dayDist = distance.get(arraySz).getY();
        float dayTime = time.get(arraySz).getY();

        stepsT.setText(String.valueOf(dayStep));
        calT.setText(String.valueOf(dayCal));
        distT.setText(String.valueOf( dayDist));
        timeT.setText(String.valueOf(dayTime));

        updateBarProgress(dayStep,dayCal,dayDist,dayTime);

        /* ------------------------------- Weekly Chart ------------------------------- */

        LineChart chart = (LineChart) findViewById(R.id.graph);

        GraphDisplay graph = new GraphDisplay(this);
        LineDataSet weekSteps = graph.chartSetUp(chart,steps,StepsDataPointsColor,ProgBarColor);
        graph.graphFade(weekSteps,R.drawable.fade_red, StepsDataPointsColor);


        // Click listener for the value selected on the graph

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float dayStep = e.getY();

                int idx = 0;
                for (int i = 0; i <= arraySz; i++) {
                    if (dayStep == steps.get(i).getY()) {
                        idx = i;
                    }
                }

                float dayCal  = calories.get(idx).getY();
                float dayDist = distance.get(idx).getY();
                float dayTime = time.get(idx).getY();

                stepsT.setText(String.valueOf((int) dayStep));
                calT.setText(String.valueOf(dayCal));
                distT.setText(String.valueOf(dayDist));
                timeT.setText(String.valueOf(dayTime));

                updateBarProgress(dayStep,dayCal,dayDist,dayTime);

            }

            @Override
            public void onNothingSelected() {

            }
        });

        /* ------------------------------- Goal Button ------------------------------- */

        Button goalsButton = (Button) findViewById(R.id.goalsBtn);
        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGoals = new Intent(getApplicationContext(), GoalsActivity.class);
                startActivity(openGoals);
            }
        });

        /* ------------------------------- Config Button ------------------------------- */

        Button configButton = (Button) findViewById(R.id.configBtn);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openConfig = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(openConfig);

            }
        });

        /* ------------------------------- Expand Button ------------------------------- */

        Button expandBtn = (Button) findViewById(R.id.expandBtn);
        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent opengraphPage = new Intent(getApplicationContext(), GraphPage.class);
                startActivity(opengraphPage);

            }
        });

    }

    public void updateBarProgress(float dayStep, float dayCal, float dayDist, float dayTime){

        // Updates ProgressBars based on previously set goals

        stepsBar.setProgress(Math.round(dayStep/stepGoal * 100));
        calsBar.setProgress(Math.round(dayCal/calGoal * 100));
        distBar.setProgress(Math.round(dayDist/distGoal * 100));
        timeBar.setProgress(Math.round(dayTime/timeGoal * 100));

    }

}