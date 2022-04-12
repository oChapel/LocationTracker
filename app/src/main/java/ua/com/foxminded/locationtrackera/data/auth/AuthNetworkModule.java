package ua.com.foxminded.locationtrackera.data.auth;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthNetworkModule {

    @Provides
    public AuthNetwork provideFirebaseAuthNetwork() {
        return new FirebaseAuthNetwork(FirebaseAuth.getInstance());
    }
}
