package ua.com.foxminded.locationtrackera.models_impl.shared_preferences

import android.content.Context
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel

class DefaultSharedPreferencesModel(context: Context) : SharedPreferencesModel {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    override fun setSharedPreferencesServiceFlag(flag: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(SERVICE_FLAG_KEY, flag)
            .apply()
    }

    override fun getSharedPreferencesServiceFlag(): Boolean =
        sharedPreferences.getBoolean(SERVICE_FLAG_KEY, false)

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME =
            "ua.com.foxminded.locationtrackera.SERVICE_PREFERENCE_FILE_KEY"
        private const val SERVICE_FLAG_KEY = "service_running_flag"
    }
}