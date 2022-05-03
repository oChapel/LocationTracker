package ua.com.foxminded.locationtrackera.di;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.auth.FirebaseAuthNetwork;

@Module
public class AppModule {

    @Provides
    @Singleton
    public AuthNetwork provideFirebaseAuthNetwork() {
        return new FirebaseAuthNetwork(FirebaseAuth.getInstance());
    }
}
