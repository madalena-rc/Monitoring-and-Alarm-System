package com.example.g05part1;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

public class SensorRepository {

    private static final String PREFS_NAME = "SensorPrefs";
    private final SharedPreferences prefs;
    private static final int MAX_READINGS = 10;

    public SensorRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // saves sensor value with timestamp + keep last 10 readings
    public void saveSensorValue(String sensorType, float value) {
        // shifts old readings to make room for new ones
        for (int i = MAX_READINGS - 1; i > 0; i--) {
            long ts = prefs.getLong(sensorType + "_ts_" + (i - 1), 0);
            float val = prefs.getFloat(sensorType + "_val_" + (i - 1), 0f);
            prefs.edit()
                    .putLong(sensorType + "_ts_" + i, ts)
                    .putFloat(sensorType + "_val_" + i, val)
                    .apply();
        }
        // stores newest reading at index 0
        prefs.edit()
                .putLong(sensorType + "_ts_0", System.currentTimeMillis())
                .putFloat(sensorType + "_val_0", value)
                .apply();
    }

    //retrieves sensor history as formatted string
    public String getSensorHistory(String sensorType, String unit) {
        StringBuilder sb = new StringBuilder();

        boolean hasData = false;
        //newest readings on top
        for (int i = 0; i < MAX_READINGS; i++) {
            long ts = prefs.getLong(sensorType + "_ts_" + i, 0);
            float val = prefs.getFloat(sensorType + "_val_" + i, Float.MIN_VALUE);
            if (ts != 0) {
                hasData = true;
                String timeStr = DateFormat.format("dd MMM yyyy HH:mm:ss", ts).toString();
                sb.append(timeStr).append(" : ").append(val).append(" ").append(unit).append("\n");
            }
        }

        if (!hasData) {
            sb.append("No data\n");
        }

        return sb.toString();
    }
}
