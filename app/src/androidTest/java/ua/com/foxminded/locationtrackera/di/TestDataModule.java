package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.models.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.models.locations.network.LocationsNetwork;
import ua.com.foxminded.locationtrackera.models_impl.locations.LocationRepositoryImpl;
import ua.com.foxminded.locationtrackera.models_impl.locations.dao.TestLocationsDao;
import ua.com.foxminded.locationtrackera.models_impl.locations.network.TestLocationsNetwork;

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
