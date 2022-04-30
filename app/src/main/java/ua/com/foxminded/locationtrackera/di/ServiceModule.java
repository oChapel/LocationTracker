package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.background.LocationServiceContract;
import ua.com.foxminded.locationtrackera.background.LocationServicePresenter;
import ua.com.foxminded.locationtrackera.model.bus.DefaultTrackerCache;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.gps.DefaultGpsModel;
import ua.com.foxminded.locationtrackera.model.gps.GpsSource;

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
