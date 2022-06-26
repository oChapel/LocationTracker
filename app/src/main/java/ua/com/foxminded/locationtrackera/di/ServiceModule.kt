package ua.com.foxminded.locationtrackera.di

import dagger.Module
import dagger.Provides
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.background.LocationServiceContract.Presenter
import ua.com.foxminded.locationtrackera.background.LocationServicePresenter
import ua.com.foxminded.locationtrackera.background.jobs.DefaultUploadWorkModel
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache
import ua.com.foxminded.locationtrackera.models.gps.GpsSource
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import ua.com.foxminded.locationtrackera.models_impl.bus.DefaultTrackerCache
import ua.com.foxminded.locationtrackera.models_impl.gps.DefaultGpsModel
import ua.com.foxminded.locationtrackera.models_impl.shared_preferences.DefaultSharedPreferencesModel
import javax.inject.Singleton

@Module
class ServiceModule {
    @Provides
    @Singleton
    fun provideGpsServicesModel(): GpsSource = DefaultGpsModel(App.instance)

    @Provides
    @Singleton
    fun provideCache(): TrackerCache = DefaultTrackerCache()

    @Provides
    @Singleton
    fun provideUploadWorkModel(): UploadWorkModel = DefaultUploadWorkModel(App.instance)

    @Provides
    @Singleton
    fun provideSharedPreferenceModel(): SharedPreferencesModel = DefaultSharedPreferencesModel(App.instance)

    @Provides
    @Singleton
    fun provideServicePresenter(
        gpsSource: GpsSource,
        repository: LocationRepository,
        cache: TrackerCache,
        sendLocationsUseCase: SendLocationsUseCase,
        workModel: UploadWorkModel
    ): Presenter = LocationServicePresenter(
        gpsSource, repository, cache, sendLocationsUseCase, workModel
    )
}
