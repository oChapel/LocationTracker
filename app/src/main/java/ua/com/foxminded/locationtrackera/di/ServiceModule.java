package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.background.LocationServiceContract;
import ua.com.foxminded.locationtrackera.background.LocationServicePresenter;
import ua.com.foxminded.locationtrackera.background.jobs.DefaultUploadWorkModel;
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel;
import ua.com.foxminded.locationtrackera.model.bus.DefaultTrackerCache;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.gps.DefaultGpsModel;
import ua.com.foxminded.locationtrackera.model.gps.GpsSource;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    public GpsSource provideGpsServicesModel() {
        return new DefaultGpsModel(App.getInstance());
    }

    @Provides
    @Singleton
    public TrackerCache provideCache() {
        return new DefaultTrackerCache();
    }

    @Provides
    @Singleton
    public UploadWorkModel provideUploadWorkModel() {
        return new DefaultUploadWorkModel(App.getInstance());
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
