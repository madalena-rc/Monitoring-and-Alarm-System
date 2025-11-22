package com.example.g05part1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // link buttons
        Button buttonStartReadings = findViewById(R.id.buttonStartReadings);
        Button buttonConfigManager = findViewById(R.id.buttonConfigManager);
        Button buttonAccessRepo = findViewById(R.id.buttonAccessRepo);


        // start readings button
        buttonStartReadings.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Starting Sensor Readings...", Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(MainActivity.this, ReadingsActivity.class));
        });


        // configurations manager button
        buttonConfigManager.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Opening Configurations Manager...", Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(MainActivity.this, ConfigManagerActivity.class));
        });

        // repo button
        buttonAccessRepo.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Accessing Repository...", Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(MainActivity.this, CheckRepoActivity.class));
        });

    }


}
