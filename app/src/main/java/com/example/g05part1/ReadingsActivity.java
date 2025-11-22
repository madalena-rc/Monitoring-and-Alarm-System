package com.example.g05part1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ReadingsActivity extends AppCompatActivity implements SensorEventListener {

    private static final String PREFS_NAME = "SensorPrefs";
    private static final int PERMISSION_REQUEST_CODE = 2001;

    private SensorManager sensorManager;
    private Sensor tempSensor, humiditySensor, lightSensor;

    private TextView textTemperature, textHumidity, textLuminosity, textTimestamp;
    private Button btnToggleNotifications;

    private float minTemp, maxTemp, minHum, maxHum, minLum, maxLum;
    private boolean notificationsEnabled = true;

    private SensorRepository repository;
    private SensorAlertManager alertManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbarReadings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sensor Readings");
        }

        // link UI elements
        textTemperature = findViewById(R.id.textTemperature);
        textHumidity = findViewById(R.id.textHumidity);
        textLuminosity = findViewById(R.id.textLuminosity);
        textTimestamp = findViewById(R.id.textTimestamp);
        btnToggleNotifications = findViewById(R.id.btnToggleNotifications);

        repository = new SensorRepository(this);
        alertManager = new SensorAlertManager(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // load thresholds
        minTemp = prefs.getFloat("min_temperature", Float.MIN_VALUE);
        maxTemp = prefs.getFloat("max_temperature", Float.MAX_VALUE);
        minHum = prefs.getFloat("min_humidity", Float.MIN_VALUE);
        maxHum = prefs.getFloat("max_humidity", Float.MAX_VALUE);
        minLum = prefs.getFloat("min_luminosity", Float.MIN_VALUE);
        maxLum = prefs.getFloat("max_luminosity", Float.MAX_VALUE);

        // sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        requestNotificationPermissionIfNeeded();

        //enable-disable notifs
        notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        btnToggleNotifications.setText(notificationsEnabled ? "Disable Notifications" : "Enable Notifications");

        btnToggleNotifications.setOnClickListener(v -> {
            notificationsEnabled = !notificationsEnabled;
            btnToggleNotifications.setText(notificationsEnabled ? "Disable Notifications" : "Enable Notifications");
            prefs.edit().putBoolean("notifications_enabled", notificationsEnabled).apply();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tempSensor != null)
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (humiditySensor != null)
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    //display current reading of each sensor
    public void onSensorChanged(SensorEvent event) {
        long timestamp = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float val = event.values[0];
            updateUI(textTemperature, "Temperature: " + val + " °C ", val, minTemp, maxTemp, "Temperature", "°C");
            repository.saveSensorValue("temperature", val);
        } else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            float val = event.values[0];
            updateUI(textHumidity, "Humidity: " + val + " % ", val, minHum, maxHum, "Humidity", "%");
            repository.saveSensorValue("humidity", val);
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float val = event.values[0];
            updateUI(textLuminosity, "Luminosity: " + val + " lux ", val, minLum, maxLum, "Luminosity", "lux");
            repository.saveSensorValue("luminosity", val);
        }

        textTimestamp.setText("Last updated: " +
                android.text.format.DateFormat.format("dd MMM yyyy HH:mm:ss", timestamp));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @SuppressLint("SetTextI18n")
    //display alerts and change color for below min above max readings + trigger notifs
    private void updateUI(TextView field, String label, float value,
                          float min, float max, String type, String unit) {
        if (value < min) {
            field.setText(label + "Below minimum");
            field.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            if (notificationsEnabled) {
                alertManager.showAlertNotification(type + " Alert",
                        type + " below min: " + value + " " + unit);
            }
        } else if (value > max) {
            field.setText(label + "Above maximum");
            field.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            if (notificationsEnabled) {
                alertManager.showAlertNotification(type + " Alert",
                        type + " above max: " + value + " " + unit);
            }
        } else {
            field.setText(label + "OK"); // inside thresholds, no notifs
            field.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    // handle back arrow in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
