package ua.com.foxminded.locationtrackera.di;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.auth.FirebaseAuthNetwork;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.shared_preferences.DefaultSharedPreferencesModel;
import ua.com.foxminded.locationtrackera.model.shared_preferences.SharedPreferencesModel;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCaseImpl;

@Module
public class AppModule {

    @Provides
    @Singleton
    public AuthNetwork provideFirebaseAuthNetwork() {
        return new FirebaseAuthNetwork(FirebaseAuth.getInstance());
    }

    @Provides
    public SendLocationsUseCase provideSendLocationsUseCase(LocationRepository repository) {
        return new SendLocationsUseCaseImpl(repository);
    }

    @Provides
    public SharedPreferencesModel provideSharedPreferenceModel() {
        return new DefaultSharedPreferencesModel(App.getInstance());
    }
}
