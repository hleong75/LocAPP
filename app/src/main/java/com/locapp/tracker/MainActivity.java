package com.locapp.tracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "LocAPPPrefs";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_TRACKING_ACTIVE = "trackingActive";

    private EditText phoneNumberInput;
    private EditText frequencyInput;
    private TextView statusText;
    private Button startButton;
    private Button stopButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadSettings();
        checkPermissions();
        updateUI();
    }

    private void initViews() {
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        frequencyInput = findViewById(R.id.frequencyInput);
        statusText = findViewById(R.id.statusText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(v -> startTracking());
        stopButton.setOnClickListener(v -> stopTracking());
    }

    private void loadSettings() {
        String phoneNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, "");
        int frequency = sharedPreferences.getInt(KEY_FREQUENCY, 15);
        
        phoneNumberInput.setText(phoneNumber);
        frequencyInput.setText(String.valueOf(frequency));
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }

        // For Android 13+ (API 33+), request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                        PERMISSION_REQUEST_CODE + 1);
            }
        }
    }

    private void startTracking() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String frequencyStr = frequencyInput.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, R.string.invalid_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        int frequency;
        try {
            frequency = Integer.parseInt(frequencyStr);
            if (frequency < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_frequency, Toast.LENGTH_SHORT).show();
            return;
        }

        // Save settings
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putInt(KEY_FREQUENCY, frequency);
        editor.putBoolean(KEY_TRACKING_ACTIVE, true);
        editor.apply();

        // Schedule periodic location updates
        scheduleLocationUpdates(frequency);

        // Start foreground service immediately
        Intent serviceIntent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void stopTracking() {
        // Save tracking state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_TRACKING_ACTIVE, false);
        editor.apply();

        // Cancel scheduled alarms
        cancelLocationUpdates();

        // Stop service
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);

        Toast.makeText(this, "Suivi arrêté", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void scheduleLocationUpdates(int frequencyMinutes) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long intervalMillis = frequencyMinutes * 60 * 1000L;
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        // Use setRepeating for periodic alarms
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    intervalMillis,
                    pendingIntent
            );
        }
    }

    private void cancelLocationUpdates() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void updateUI() {
        boolean isTracking = sharedPreferences.getBoolean(KEY_TRACKING_ACTIVE, false);
        
        if (isTracking) {
            statusText.setText(R.string.tracking_active);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            statusText.setText(R.string.tracking_inactive);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }
}
