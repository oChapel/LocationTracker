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
    }

    public static App getInstance() {
        return appInstance;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static void setAppComponent(AppComponent appComponent) {
        App.appComponent = appComponent;
    }

    public static AppComponent getComponent() {
        return appComponent;
    }
}
