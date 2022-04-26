package ua.com.foxminded.locationtrackera.di;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.data.source.LocalDataSource;
import ua.com.foxminded.locationtrackera.data.source.LocationDataSource;
import ua.com.foxminded.locationtrackera.data.LocationRepository;
import ua.com.foxminded.locationtrackera.data.source.FirebaseDataSource;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;

@Module
public class DataModule {

    @Provides
    @Singleton
    public LocationServiceContract.Repository provideLocationRepository(
            @Named("local") LocationDataSource localDataSource,
            @Named("remote") LocationDataSource remoteDataSource
    ) {
        return new LocationRepository(localDataSource, remoteDataSource);
    }

    @Provides
    @Singleton
    @Named("local")
    public LocationDataSource provideLocalDataSource() {
        return new LocalDataSource(App.getInstance());
    }

    @Provides
    @Singleton
    @Named("remote")
    public LocationDataSource provideFirebaseDataSource() {
        return new FirebaseDataSource(FirebaseFirestore.getInstance());
    }
}
