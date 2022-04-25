package ua.com.foxminded.locationtrackera;

import android.app.Application;

import ua.com.foxminded.locationtrackera.di.AppComponent;
import ua.com.foxminded.locationtrackera.di.DaggerAppComponent;

public class App extends Application {

    private static App appInstance;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        appComponent = DaggerAppComponent.create();
    }

    public static App getInstance() {
        return appInstance;
    }

    public static AppComponent getComponent() {
        return appComponent;
    }
}
