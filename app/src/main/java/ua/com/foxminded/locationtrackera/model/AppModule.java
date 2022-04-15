package ua.com.foxminded.locationtrackera.model;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.auth.FirebaseAuthNetwork;

@Module
public class AppModule {

    @Provides
    public AuthNetwork provideFirebaseAuthNetwork() {
        return new FirebaseAuthNetwork(FirebaseAuth.getInstance());
    }
}
