package com.example.stepncount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

public class ConfigActivity extends AppCompatActivity {

    /* ------------------------------- Shared preferences  ------------------------------- */

    public static final String CONFIG_PREFS = "configPrefs";
    public static final String GENDER = "genderPrefs";
    public static final String WEIGHT = "weightPrefs";
    public static final String BIRTH = "birthPrefs";
    public static final String HEIGHT = "heightPrefs";

    private String Gender;
    private int Weight;
    private int Birth;
    private int Height;

    /* ------------------------------- Global variables  ------------------------------- */

    private static String[] genderInp;
    private static int weightInp;
    private static int birthInp;
    private static int heightInp;

    /* ------------------------------- Layout object variables ------------------------------- */

    NumberPicker genderPck;
    NumberPicker weightPck;
    NumberPicker birthPck;
    NumberPicker heightPck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        SharedPreferences configPreferences = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE); // Mode private means no other app can use the saved data
        SharedPreferences.Editor editor = configPreferences.edit();

        /* ------------------------------- Guidelines ------------------------------- */

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int w = displayMetrics.widthPixels;

        Guideline horizontalG1 = findViewById(R.id.guideline5);
        Guideline horizontalG2 = findViewById(R.id.guideline6);
        horizontalG1.setGuidelineBegin(w/3); // Puts first guideline at 1/3 of the current running phone width
        horizontalG2.setGuidelineBegin(Math.round(w*2/3)); // Puts second guideline at 2/3 of the current running phone width

        /* ------------------------------- Number Pickers ------------------------------- */

        // Gender picker

        final String[] gen = {"Male","Female"};

        genderPck = findViewById(R.id.genderPick);
        genderPck.setMinValue(0);
        genderPck.setMaxValue(gen.length-1);
        genderPck.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                String [] auxStr=genderPck.getDisplayedValues();
                genderInp[0] = auxStr[newValue];
                if(genderInp[0] == "Female")
                {
                    genderInp[1] = "Male";
                }else
                {
                    genderInp[1] = "Female";
                }

            }
        });

        // Weight picker

        weightPck = findViewById(R.id.weightPick);
        weightPck.setMinValue(20);
        weightPck.setMaxValue(250);
        weightPck.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                weightInp = newValue;
            }
        });

        // Year of birth picker

        birthPck = findViewById(R.id.birthPick);
        birthPck.setMinValue(1940);
        birthPck.setMaxValue(2012);
        birthPck.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                birthInp = newValue;
            }
        });

        // Height picker

        heightPck = findViewById(R.id.heightPick);
        heightPck.setMinValue(60);
        heightPck.setMaxValue(250);
        heightPck.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                heightInp = newValue;
            }
        });

        boolean firstStart = configPreferences.getBoolean("firstStart",true);

        if(firstStart)
        {
            genderPck.setDisplayedValues(gen);
            weightPck.setValue(70);
            birthPck.setValue(1997);
            heightPck.setValue(165);
            genderInp = genderPck.getDisplayedValues();
            weightInp = weightPck.getValue(); // If the user does not choose a new value in the picker, this saves the default value
            birthInp = birthPck.getValue();
            heightInp = heightPck.getValue();
        }
        else
        {
            loadData();
        }

        Button bt = findViewById(R.id.btn); //represent object como uma interface view (element in the layout), capture our button from layout
        bt.setOnClickListener(new View.OnClickListener() { //defining a listener: register the onClick listener with the implementation
            //the parameters can be a function that will be called when the user clicks on the button
            @Override
            public void onClick(View view) {

                // Save info

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < genderInp.length; i++) {
                    sb.append(genderInp[i]).append(",");
                }

                editor.putString(GENDER, sb.toString());
                editor.putInt(WEIGHT, weightInp);
                editor.putInt(BIRTH, birthInp);
                editor.putInt(HEIGHT, heightInp);

                editor.putBoolean("firstStart",false); // Defined on the main activity
                editor.apply();

                Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();

                // Goes to main activity

                gotoMain();
            }
        });

    }

    /* ------------------------------- Save Button ------------------------------- */

    public void gotoMain(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public void loadData(){

        // Load goal shared preferences

        SharedPreferences configPreferences = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);

        Gender = configPreferences.getString(GENDER,"");
        Weight = configPreferences.getInt(WEIGHT,70);
        Birth = configPreferences.getInt(BIRTH, 1997);
        Height = configPreferences.getInt(HEIGHT,165);

        // Updating views

        String[] gend = Gender.split(",");

        genderPck.setDisplayedValues(gend);
        weightPck.setValue(Weight);
        birthPck.setValue(Birth);
        heightPck.setValue(Height);


    }

}

