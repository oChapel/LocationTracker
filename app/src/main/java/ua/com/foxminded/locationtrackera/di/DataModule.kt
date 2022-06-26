package ua.com.foxminded.locationtrackera.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.locations.dao.LocationsDao
import ua.com.foxminded.locationtrackera.models.locations.network.LocationsNetwork
import ua.com.foxminded.locationtrackera.models_impl.locations.LocationRepositoryImpl
import ua.com.foxminded.locationtrackera.models_impl.locations.dao.RoomLocationsDao
import ua.com.foxminded.locationtrackera.models_impl.locations.network.FirebaseLocationsNetwork
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideLocationRepository(
        localDataSource: LocationsDao,
        remoteDataSource: LocationsNetwork
    ): LocationRepository = LocationRepositoryImpl(localDataSource, remoteDataSource)

    @Provides
    @Singleton
    fun provideLocalDataSource(): LocationsDao = RoomLocationsDao(App.instance)

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): LocationsNetwork = FirebaseLocationsNetwork(FirebaseAuth.getInstance())
}
