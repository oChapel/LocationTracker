package ua.com.foxminded.locationtrackera.di

import dagger.Component
import ua.com.foxminded.locationtrackera.background.LocationService
import ua.com.foxminded.locationtrackera.background.jobs.LocationsUploader
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import javax.inject.Singleton

@Component(modules = [AppModule::class, ServiceModule::class, DataModule::class])
@Singleton
interface AppComponent {
    fun inject(factory: AuthViewModelFactory)
    fun inject(service: LocationService)
    fun inject(worker: LocationsUploader)
}
