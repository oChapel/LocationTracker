package ua.com.foxminded.locationtrackera;

import android.app.Application;

public class App extends Application {

    private static App appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
    }

    public static App getInstance() {
        return appInstance;
    }
}
