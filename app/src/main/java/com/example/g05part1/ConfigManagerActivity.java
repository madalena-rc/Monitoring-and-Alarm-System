package com.example.g05part1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ConfigManagerActivity extends AppCompatActivity {

    private EditText editMinTemperature, editMaxTemperature, editMinHumidity, editMaxHumidity, editMinLuminosity, editMaxLuminosity;
    private static final String PREFS_NAME = "SensorPrefs";
    private static final String KEY_MIN_TEMPERATURE = "min_temperature";
    private static final String KEY_MAX_TEMPERATURE = "max_temperature";
    private static final String KEY_MIN_HUMIDITY = "min_humidity";
    private static final String KEY_MAX_HUMIDITY = "max_humidity";
    private static final String KEY_MIN_LUMINOSITY = "min_luminosity";
    private static final String KEY_MAX_LUMINOSITY = "max_luminosity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_manager);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbarConfig);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // default system back arrow
            getSupportActionBar().setTitle("Configurations Manager"); // set title here
        }


        // link UI elements
        editMinTemperature = findViewById(R.id.editMinTemperature);
        editMaxTemperature = findViewById(R.id.editMaxTemperature);

        editMinHumidity = findViewById(R.id.editMinHumidity);
        editMaxHumidity = findViewById(R.id.editMaxHumidity);

        editMinLuminosity = findViewById(R.id.editMinLuminosity);
        editMaxLuminosity = findViewById(R.id.editMaxLuminosity);
        Button btnSave = findViewById(R.id.btnSave);

        // load saved values
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        float savedMinT = prefs.getFloat(KEY_MIN_TEMPERATURE, 0);
        float savedMaxT = prefs.getFloat(KEY_MAX_TEMPERATURE, 0);

        float savedMinH = prefs.getFloat(KEY_MIN_HUMIDITY, 0);
        float savedMaxH = prefs.getFloat(KEY_MAX_HUMIDITY, 0);

        float savedMinL = prefs.getFloat(KEY_MIN_LUMINOSITY, 0);
        float savedMaxL = prefs.getFloat(KEY_MAX_LUMINOSITY, 0);
        editMinTemperature.setText(String.valueOf(savedMinT));
        editMaxTemperature.setText(String.valueOf(savedMaxT));

        editMinHumidity.setText(String.valueOf(savedMinH));
        editMaxHumidity.setText(String.valueOf(savedMaxH));

        editMinLuminosity.setText(String.valueOf(savedMinL));
        editMaxLuminosity.setText(String.valueOf(savedMaxL));

        // save button
        btnSave.setOnClickListener(v -> {
            String minStrT = editMinTemperature.getText().toString().trim();
            String maxStrT = editMaxTemperature.getText().toString().trim();

            String minStrH = editMinHumidity.getText().toString().trim();
            String maxStrH = editMaxHumidity.getText().toString().trim();

            String minStrL = editMinLuminosity.getText().toString().trim();
            String maxStrL = editMaxLuminosity.getText().toString().trim();

            //cannot leave empty fields
            if (minStrT.isEmpty() || maxStrT.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float minT = Float.parseFloat(minStrT);
            float maxT = Float.parseFloat(maxStrT);

            if (minStrH.isEmpty() || maxStrH.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float minH = Float.parseFloat(minStrH);
            float maxH = Float.parseFloat(maxStrH);

            if (minStrL.isEmpty() || maxStrL.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float minL = Float.parseFloat(minStrL);
            float maxL = Float.parseFloat(maxStrL);

            //guarantee min threshold is below max
            if (minT >= maxT) {
                Toast.makeText(this, "Invalid input! Min should be < Max", Toast.LENGTH_LONG).show();
                return;
            }

            if (minH >= maxH) {
                Toast.makeText(this, "Invalid input! Min should be < Max and between 0-100%", Toast.LENGTH_LONG).show();
                return;
            }

            if (minL >= maxL) {
                Toast.makeText(this, "Invalid input! Min should be < Max", Toast.LENGTH_LONG).show();
                return;
            }

            //save thresholds
            prefs.edit().putFloat(KEY_MIN_TEMPERATURE, minT).putFloat(KEY_MAX_TEMPERATURE, maxT).apply();
            Toast.makeText(this, "Temperature range saved: " + minT + "°C - " + maxT + "°C", Toast.LENGTH_LONG).show();

            prefs.edit().putFloat(KEY_MIN_HUMIDITY, minH).putFloat(KEY_MAX_HUMIDITY, maxH).apply();
            Toast.makeText(this, "Humidity range saved: " + minH + "% - " + maxH + "%", Toast.LENGTH_LONG).show();

            prefs.edit().putFloat(KEY_MIN_LUMINOSITY, minL).putFloat(KEY_MAX_LUMINOSITY, maxL).apply();
            Toast.makeText(this, "Luminosity range saved: " + minL + "lux - " + maxL + "lux", Toast.LENGTH_LONG).show();
        });
    }

    // handles back arrow in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish(); // back to MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
