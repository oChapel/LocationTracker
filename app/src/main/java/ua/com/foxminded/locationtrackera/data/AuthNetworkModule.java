package ua.com.foxminded.locationtrackera.data;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthNetworkModule {

    @Provides
    public FirebaseAuthNetwork provideFirebaseNetworkAuth() {
        return new FirebaseAuthNetwork(FirebaseAuth.getInstance());
    }
}
