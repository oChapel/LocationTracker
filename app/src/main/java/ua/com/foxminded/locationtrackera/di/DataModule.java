package ua.com.foxminded.locationtrackera.di;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepositoryImpl;
import ua.com.foxminded.locationtrackera.model.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.dao.RoomLocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.network.FirebaseLocationsNetwork;
import ua.com.foxminded.locationtrackera.model.locations.network.LocationsNetwork;

@Module
public class DataModule {

    @Provides
    @Singleton
    public LocationRepository provideLocationRepository(
            LocationsDao localDataSource,
            LocationsNetwork remoteDataSource
    ) {
        return new LocationRepositoryImpl(localDataSource, remoteDataSource);
    }

    @Provides
    @Singleton
    public LocationsDao provideLocalDataSource() {
        return new RoomLocationsDao(App.getInstance());
    }

    @Provides
    @Singleton
    public LocationsNetwork provideFirebaseDataSource() {
        return new FirebaseLocationsNetwork(
                FirebaseAuth.getInstance()
        );
    }
}
