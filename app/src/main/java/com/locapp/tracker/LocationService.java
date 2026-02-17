package com.locapp.tracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String PREFS_NAME = "LocAPPPrefs";
    private static final int SMS_MAX_LENGTH = 160;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        requestLocationUpdate();
        return START_STICKY;
    }

    private void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            stopSelf();
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdates(1)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    handleLocationUpdate(location);
                }
                
                // Stop service after getting location
                stopSelf();
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void handleLocationUpdate(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        
        if (!phoneNumber.isEmpty()) {
            String message = buildMessage(latitude, longitude);
            sendSMS(phoneNumber, message);
        }
    }

    private String buildMessage(double latitude, double longitude) {
        String basicMessage = String.format(Locale.US, "Position: %.6f, %.6f\nGoogle Maps: https://maps.google.com/?q=%.6f,%.6f", 
                latitude, longitude, latitude, longitude);
        
        // Check if we should include city name (for withcity flavor)
        if (BuildConfig.FLAVOR.equals("withcity")) {
            String cityName = getCityName(latitude, longitude);
            if (cityName != null && !cityName.isEmpty()) {
                return String.format(Locale.US, "Position: %.6f, %.6f\nVille: %s\nGoogle Maps: https://maps.google.com/?q=%.6f,%.6f",
                        latitude, longitude, cityName, latitude, longitude);
            }
        }
        
        return basicMessage;
    }

    private String getCityName(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                if (city != null && !city.isEmpty()) {
                    return city;
                }
                // Fallback to subAdminArea if locality is null
                String subAdmin = address.getSubAdminArea();
                if (subAdmin != null && !subAdmin.isEmpty()) {
                    return subAdmin;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
        }
        return null;
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            
            // Split message if it's too long
            if (message.length() > SMS_MAX_LENGTH) {
                smsManager.sendMultipartTextMessage(
                        phoneNumber, 
                        null, 
                        smsManager.divideMessage(message), 
                        null, 
                        null
                );
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
            
            Log.d(TAG, "SMS sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "SMS failed", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Service de suivi de localisation");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LocAPP Tracker")
                .setContentText("Suivi de localisation actif")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
