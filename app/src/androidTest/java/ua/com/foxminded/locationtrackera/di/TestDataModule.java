package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepositoryImpl;
import ua.com.foxminded.locationtrackera.model.locations.dao.TestLocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.network.TestLocationsNetwork;
import ua.com.foxminded.locationtrackera.model.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.network.LocationsNetwork;

@Module
public class TestDataModule {

    @Provides
    @Singleton
    public LocationRepository provideTestLocationRepository(
            LocationsDao localDataSource,
            LocationsNetwork remoteDataSource
    ) {
        return new LocationRepositoryImpl(localDataSource, remoteDataSource);
    }

    @Provides
    @Singleton
    public LocationsNetwork provideTestLocationsNetwork() {
        return new TestLocationsNetwork();
    }

    @Provides
    @Singleton
    public LocationsDao provideTestLocationsDao() {
        return new TestLocationsDao();
    }
}
