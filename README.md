# Monitoring-and-Alarm-System
Android application developed in Android Studio that manages local (smartphone) environmental sensors: temperature, humidity and luminosity

## Software Requirements
- Android 8.0 (API Level 26) or higher
- Java 8-compatible Android environment
- Android SDK components

## Software 
### ReadingsActivity
- Manages and reads environmental sensors showing their current values (temperature,
humidity and luminosity) using Android's **SensorManager**
- Displays the readings in the UI and reads thresholds from **SharedPreferences** (defined in **ConfigManagerActivity**), triggering notifications when values exceed the configured thresholds resorting to the **SensorAlertManager**
- Sends readings to **SensorRepository** to maintain a history of the last 10 readings
per sensor type

> **SensorAlertmanager** is a dedicated module for managing the threshold-based alerts in the application and to keep alert logic separate from from UI updates and sensor readings in **ReadingsActivity** and works in conjunction with a toggle in **ReadingsActivity** to enable or disable alerts

### ConfigManagerActivity
- Manages configuration parameters (modify low and high thresholds associated with alarms, reset maximums and minimums) and preserves configuration information and maximums and minimums values between app activations;
- Reads and writes these values to persistent storage using **SharedPreferences**,
which are then utilized by **ReadingsActivity** and the alert system to trigger notifications
when readings exceed thresholds

### CheckRepoActivity
- Displays historical sensor data previously recorded by the environmental monitoring system
by retrieving up to 10 of the locally stored readings of each sensor type from **SensorRepository**

> **SensorRepository** is a data management module responsible for storing historical sensor readings received from **ReadingsActivity**, which maintains a fixed-length history (10 most recent readings per sensor and respective timestamps) by automatically shifting older values and formats the data for display in UI components with **CheckRepoActivity**
