package ua.com.foxminded.locationtrackera.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import ua.com.foxminded.locationtrackera.models_impl.auth.FirebaseAuthNetwork
import ua.com.foxminded.locationtrackera.models_impl.usecase.SendLocationsUseCaseImpl
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthNetwork(): AuthNetwork = FirebaseAuthNetwork(FirebaseAuth.getInstance())

    @Provides
    fun provideSendLocationsUseCase(repository: LocationRepository): SendLocationsUseCase =
        SendLocationsUseCaseImpl(repository)
}