package ua.com.foxminded.locationtrackera.di;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.data.source.RoomLocationsDao;
import ua.com.foxminded.locationtrackera.data.source.LocationsDao;
import ua.com.foxminded.locationtrackera.data.source.LocationsNetwork;
import ua.com.foxminded.locationtrackera.data.LocationRepository;
import ua.com.foxminded.locationtrackera.data.source.FirebaseLocationsNetwork;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;

@Module
public class DataModule {

    @Provides
    @Singleton
    public LocationServiceContract.Repository provideLocationRepository(
            LocationsDao localDataSource,
            LocationsNetwork remoteDataSource
    ) {
        return new LocationRepository(localDataSource, remoteDataSource);
    }

    @Provides
    @Singleton
    public LocationsDao provideLocalDataSource() {
        return new RoomLocationsDao(App.getInstance());
    }

    @Provides
    @Singleton
    public LocationsNetwork provideFirebaseDataSource() {
        return new FirebaseLocationsNetwork(FirebaseFirestore.getInstance());
    }
}
