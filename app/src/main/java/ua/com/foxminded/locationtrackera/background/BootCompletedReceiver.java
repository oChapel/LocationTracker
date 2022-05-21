package ua.com.foxminded.locationtrackera.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String SHARED_PREFERENCES_FILE_NAME = "ua.com.foxminded.locationtrackera.SERVICE_PREFERENCE_FILE_KEY";
    private static final String SERVICE_FLAG_KEY = "service_running_flag";

    @Override
    public void onReceive(Context context, Intent intent) {
        final boolean isServiceRunning = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(SERVICE_FLAG_KEY, false);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && isServiceRunning) {
            final Intent serviceIntent = new Intent(context, LocationService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}
