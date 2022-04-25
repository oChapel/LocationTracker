package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.data.LocationRepository;
import ua.com.foxminded.locationtrackera.model.service.Cache;
import ua.com.foxminded.locationtrackera.model.service.GpsServicesModel;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    public LocationServiceContract.Repository provideLocationRepository() {
        return new LocationRepository();
    }

    @Provides
    @Singleton
    public LocationServiceContract.GpsServices provideGpsServicesModel() {
        return new GpsServicesModel(App.getInstance());
    }

    @Provides
    @Singleton
    public LocationServiceContract.Cache provideCache() {
        return new Cache();
    }
}
