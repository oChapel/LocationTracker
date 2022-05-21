package ua.com.foxminded.locationtrackera.model.shared_preferences;

import android.content.Context;

public class DefaultSharedPreferencesModel implements SharedPreferencesModel {

    private static final String SHARED_PREFERENCES_FILE_NAME = "ua.com.foxminded.locationtrackera.SERVICE_PREFERENCE_FILE_KEY";
    private static final String SERVICE_FLAG_KEY = "service_running_flag";

    private final Context context;

    public DefaultSharedPreferencesModel(Context context) {
        this.context = context;
    }

    @Override
    public void setSharedPreferencesServiceFlag(boolean flag) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SERVICE_FLAG_KEY, flag)
                .apply();
    }
}
