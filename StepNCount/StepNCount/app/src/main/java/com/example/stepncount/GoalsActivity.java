package com.example.stepncount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class GoalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        // Importing color scheme from Resource Files: res/values/colors.xml
        int BackgroundColor = ContextCompat.getColor(this, R.color.Background);
        int txtColor = ContextCompat.getColor(this, R.color.Text);


    }

    //when the user clicks go back saves the goals
    public void goToMainPage (View view) {
        TextView txtSteps = findViewById(R.id.textView1);
        TextView txtEE = findViewById(R.id.textView2);

        EditText edtTxTSteps= findViewById(R.id.editTxt1);
        EditText edtTxtEE = findViewById(R.id.editTxt2);

        Intent i = new Intent(getApplicationContext(), MainActivity.class); //create an intent and fill it with data from the second activity's class, this is built and sent by the main activity
        startActivity(i);
    }
}