package com.example.g05part1;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CheckRepoActivity extends AppCompatActivity {

    private TextView textTemperature, textHumidity, textLuminosity;
    private SensorRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_repo);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarRepo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sensor History");
        }

        repository = new SensorRepository(this);

        //link UI elements
        textTemperature = findViewById(R.id.textTemperature);
        textHumidity = findViewById(R.id.textHumidity);
        textLuminosity = findViewById(R.id.textLuminosity);

        textTemperature.setText(repository.getSensorHistory("temperature", "°C"));
        textHumidity.setText(repository.getSensorHistory("humidity", "%"));
        textLuminosity.setText(repository.getSensorHistory("luminosity", "lux"));
    }

    //handles back arrow in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish(); // back to MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
