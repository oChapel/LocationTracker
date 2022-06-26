package ua.com.foxminded.locationtrackera.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isServiceRunning =
            context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(SERVICE_FLAG_KEY, false)
        if (Intent.ACTION_BOOT_COMPLETED == intent.action && isServiceRunning) {
            val serviceIntent = Intent(context, LocationService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME =
            "ua.com.foxminded.locationtrackera.SERVICE_PREFERENCE_FILE_KEY"
        private const val SERVICE_FLAG_KEY = "service_running_flag"
    }
}
