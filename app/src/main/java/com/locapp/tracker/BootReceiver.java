package com.locapp.tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final String PREFS_NAME = "LocAPPPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Verify intent action to prevent processing of unintended intents
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.w(TAG, "Received non-BOOT_COMPLETED intent, ignoring");
            return;
        }
        
        Log.d(TAG, "Boot completed");
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean trackingActive = prefs.getBoolean("trackingActive", false);
        
        if (trackingActive) {
            int frequency = prefs.getInt("frequency", 15);
            scheduleLocationUpdates(context, frequency);
        }
    }

    private void scheduleLocationUpdates(Context context, int frequencyMinutes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long intervalMillis = frequencyMinutes * 60 * 1000L;
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    intervalMillis,
                    pendingIntent
            );
        }
    }
}
