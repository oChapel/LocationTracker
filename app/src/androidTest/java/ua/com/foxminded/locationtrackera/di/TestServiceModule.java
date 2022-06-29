package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.background.LocationServiceContract;
import ua.com.foxminded.locationtrackera.background.LocationServicePresenter;
import ua.com.foxminded.locationtrackera.background.jobs.DefaultUploadWorkModel;
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel;
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.models.gps.GpsSource;
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel;
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.models_impl.bus.DefaultTrackerCache;
import ua.com.foxminded.locationtrackera.models_impl.gps.TestGpsModel;
import ua.com.foxminded.locationtrackera.models_impl.shared_preferences.DefaultSharedPreferencesModel;

@Module
public class TestServiceModule {

    @Provides
    @Singleton
    public GpsSource provideTestGpsServicesModel() {
        return new TestGpsModel(App.instance);
    }

    @Provides
    @Singleton
    public TrackerCache provideCache() {
        return new DefaultTrackerCache();
    }

    @Provides
    @Singleton
    public UploadWorkModel provideUploadWorkModel() {
        return new DefaultUploadWorkModel(App.instance);
    }

    @Provides
    @Singleton
    public SharedPreferencesModel provideSharedPreferenceModel() {
        return new DefaultSharedPreferencesModel(App.instance);
    }

    @Provides
    @Singleton
    public LocationServiceContract.Presenter provideServicePresenter(
            GpsSource gpsSource,
            LocationRepository repository,
            TrackerCache cache,
            SendLocationsUseCase sendLocationsUseCase,
            UploadWorkModel workModel
    ) {
        return new LocationServicePresenter(gpsSource, repository, cache, sendLocationsUseCase, workModel);
    }
}
