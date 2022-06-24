package ua.com.foxminded.locationtrackera

import android.app.Application
import ua.com.foxminded.locationtrackera.di.AppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
        lateinit var component: AppComponent
    }
}