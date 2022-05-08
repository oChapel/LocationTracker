package ua.com.foxminded.locationtrackera.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Intent serviceIntent = new Intent(context, LocationService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}