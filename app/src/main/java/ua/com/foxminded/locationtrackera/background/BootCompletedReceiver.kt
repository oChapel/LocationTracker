package ua.com.foxminded.locationtrackera.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel
import javax.inject.Inject

class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sharedPreferencesModel: SharedPreferencesModel

    init {
        App.component.inject(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (
            Intent.ACTION_BOOT_COMPLETED == intent.action &&
            sharedPreferencesModel.getSharedPreferencesServiceFlag()
        ) {
            val serviceIntent = Intent(context, LocationService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
