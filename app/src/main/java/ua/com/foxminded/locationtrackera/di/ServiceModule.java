package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.service.DefaultTrackerCache;
import ua.com.foxminded.locationtrackera.model.service.DefaultGpsModel;
import ua.com.foxminded.locationtrackera.model.service.GpsSource;
import ua.com.foxminded.locationtrackera.model.service.TrackerCache;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;
import ua.com.foxminded.locationtrackera.services.LocationServicePresenter;

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
    public LocationServiceContract.Presenter provideServicePresenter(
            GpsSource gpsSource,
            LocationServiceContract.Repository repository,
            TrackerCache cache
    ) {
        return new LocationServicePresenter(gpsSource, repository, cache);
    }
}
