package com.example.stepncount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GoalsActivity extends AppCompatActivity {

    /* ------------------------------- Shared preferences  ------------------------------- */

    public static final String GOALS_PREFS = "goalsPrefs";
    public static final String STEPS_GOAL = "stepsPrefs";
    public static final String CAL_GOAL = "calPrefs";
    public static final String TIME_GOAL= "timePrefs";
    public static final String DIST_GOAL = "distPrefs";
    public static final String TIME_GOAL_U = "timeUPrefs";
    public static final String DIST_GOAL_U = "distUPrefs";

    private int Steps;
    private int Calories;
    private int Distance;
    private int Time;
    private String TimeU; // U for the string Unit (e.g., h or min)
    private String DistU;

    /* ------------------------------- Layout object variables ------------------------------- */

    private ImageView stepMinus;
    private ImageView stepPlus;
    private TextView stepTxt;

    private ImageView calMinus;
    private ImageView calPlus;
    private TextView calTxt;

    private ImageView distMinus;
    private ImageView distPlus;
    private TextView distTxt;

    private ImageView timeMinus;
    private ImageView timePlus;
    private TextView timeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        SharedPreferences goalsPreferences = getSharedPreferences(GOALS_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = goalsPreferences.edit();

        /* ------------------------------- Accessing object variables ------------------------------- */

        stepMinus = findViewById(R.id.stepMinus);
        stepPlus  = findViewById(R.id.stepPlus);
        stepTxt = findViewById(R.id.stepGoal);

        calMinus = findViewById(R.id.calMinus);
        calPlus  = findViewById(R.id.calPlus);
        calTxt = findViewById(R.id.calGoal);

        distMinus = findViewById(R.id.distMinus);
        distPlus  = findViewById(R.id.distPlus);
        distTxt = findViewById(R.id.distGoal);

        timeMinus = findViewById(R.id.timeMinus);
        timePlus  = findViewById(R.id.timePlus);
        timeTxt = findViewById(R.id.timeGoal);

        /* ------------------------------- Changing layout objects ------------------------------- */

        // Steps

        stepTxt.setText("10000 steps");
        stepMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = stepTxt.getText().toString();
                String[] sepStr = extractDigits(numString);
                int num = Integer.parseInt(sepStr[0]);
                if (num > 0) {
                    num = num - 500;
                }
                stepTxt.setText(num + " " + sepStr[1]);
            }
        });

        stepPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = stepTxt.getText().toString();
                String[] sepStr = extractDigits(numString);
                int num = Integer.parseInt(sepStr[0]);
                num = num + 500;
                stepTxt.setText(num + " " + sepStr[1]);
            }
        });

        // Calories

        calTxt.setText("500 Cal");

        calMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = calTxt.getText().toString();
                String[] sepStr = extractDigits(numString);
                int num = Integer.parseInt(sepStr[0]);

                if (num > 0) {
                    if (num <= 100) {
                        num = num - 5;
                    }
                    else
                    {
                        num = num - 50;
                    }
                }
                calTxt.setText(num + " " + sepStr[1]);
            }
        });

        calPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = calTxt.getText().toString();
                String[] sepStr = extractDigits(numString);
                int num = Integer.parseInt(sepStr[0]);
                num = num + 50;
                calTxt.setText(num + " " + sepStr[1]);
            }
        });

        // Distance

        distTxt.setText("8 Km");

        distMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = distTxt.getText().toString();
                String[] sepStr = extractDigits(numString);
                String aux = sepStr[1];

                int num = Integer.parseInt(sepStr[0]);

                if (num > 0) {
                    if (num <= 1 || checkDist(aux)) {
                        if (num == 1) {
                            num = 1000;
                        }
                        aux = "m";
                        num = num - 100;
                    } else {
                        num = num - 1;
                        aux = "Km";
                    }
                }
                distTxt.setText(num +" "+ aux);
            }
        });

        distPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = distTxt.getText().toString();
                String[] sepStr = extractDigits(numString);

                String aux = sepStr[1];

                int num = Integer.parseInt(sepStr[0]);

                if(num >= 1000 || checkDist(aux) == false){
                    if(num == 1000){num = 1;}
                    aux = "Km";
                    num = num + 1;
                }
                else{
                    num = num + 100;
                    aux = "m";
                }
                distTxt.setText(num +" "+ aux);
            }
        });

        // Time

        timeTxt.setText("1 h");

        timeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = timeTxt.getText().toString();
                String[] sepStr = extractDigits(numString);
                String aux = sepStr[1];

                int num = Integer.parseInt(sepStr[0]);

                if (num > 0) {
                    if (num <= 1 || checkDist(aux)) {
                        if (num == 1) {
                            num = 60;
                        }
                        aux = "min";
                        num = num - 15;
                    } else {
                        num = num - 1;
                        aux = "h";
                    }
                }
                timeTxt.setText(num +" "+ aux);
            }
        });

        timePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString = timeTxt.getText().toString();
                String[] sepStr = extractDigits(numString);

                String aux = sepStr[1];

                int num = Integer.parseInt(sepStr[0]);

                if(num >= 60 || checkDist(aux) == false){
                    if(num == 60){num = 1;}
                    aux = "h";
                    num = num + 1;
                }
                else{
                    num = num + 15;
                    aux = "min";
                }
                timeTxt.setText(num +" "+ aux);
            }
        });

        Button bt = findViewById(R.id.btn); //represent object como uma interface view (element in the layout), capture our button fr
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class); //create an intent and fill it with data from the second activity's class, this is built and sent by the main activity
                startActivity(i);

                // Pass the saved goals to main

                final String[] auxSteps = extractDigits(stepTxt.getText().toString());
                final int steps = Integer.parseInt(auxSteps[0]);

                final String[] auxCal = extractDigits(calTxt.getText().toString());
                final int cals = Integer.parseInt(auxCal[0]);

                final String[] auxDist = extractDigits(distTxt.getText().toString());
                final int dist = Integer.parseInt(auxDist[0]);

                final String[] auxTime = extractDigits(timeTxt.getText().toString());
                final int time = Integer.parseInt(auxTime[0]);

                final String[] auxDistU = extractDigits(distTxt.getText().toString());
                final String distU = auxTime[1];

                final String[] auxTimeU = extractDigits(timeTxt.getText().toString());
                final String timeU = auxTime[1];

                /*The static keyword means the value is the same for every instance of the class.
                The final keyword means once the variable is assigned a value it can never be changed.
                The combination of static final in Java is how to create a constant value.*/

                editor.putInt(STEPS_GOAL, steps);
                editor.putInt(CAL_GOAL, cals);
                editor.putInt(DIST_GOAL, dist);
                editor.putInt(TIME_GOAL, time);
                editor.putString(DIST_GOAL_U, distU);
                editor.putString(TIME_GOAL_U, timeU);

                editor.apply();

                Toast.makeText(getApplicationContext(), "Goals saved", Toast.LENGTH_SHORT).show();


            }
        });

        loadData();

    }

    public static String[] extractDigits(String args){ // Substitutes all non-digit characters for empty chars

        String otherStr = args.replaceAll("[0-9]+", "");   // ^ means not within the brackets
        String digitsStr = args.replaceAll("[^0-9]+", ""); // + means more than one occurrence of the same char

        return new String[] {digitsStr.trim(), otherStr.trim()}; //.trim () to remove white spaces
    }

    public static boolean checkDist(String str) {
        if (str.equals("m") || str.equals("min"))
        {
            return true;
        }
        else if (str.equals("Km") || str.equals("h"))
        {
            return false;
        }
        else
        {
            return false;
        }
    }

    public void loadData(){

        // Load goal shared preferences

        SharedPreferences goalPreferences = getSharedPreferences(GOALS_PREFS, MODE_PRIVATE);
        Steps = goalPreferences.getInt(STEPS_GOAL,10000);
        Calories = goalPreferences.getInt(CAL_GOAL,500);
        Distance = goalPreferences.getInt(DIST_GOAL, 8);
        Time = goalPreferences.getInt(TIME_GOAL,1);
        DistU = goalPreferences.getString(DIST_GOAL_U, "Km");
        TimeU = goalPreferences.getString(TIME_GOAL_U,"h");

        // Updating views

        stepTxt.setText(String.valueOf(Steps));
        calTxt.setText(Calories + " Cal");
        distTxt.setText(Distance + " " + DistU);
        timeTxt.setText(Time + " " + TimeU);
    }
}

