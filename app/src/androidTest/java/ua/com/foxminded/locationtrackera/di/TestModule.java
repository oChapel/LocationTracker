package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.models_impl.auth.TestAuthNetwork;
import ua.com.foxminded.locationtrackera.models_impl.usecase.SendLocationsUseCaseImpl;

@Module
public class TestModule {

    @Provides
    @Singleton
    public AuthNetwork provideTestAuthNetwork() {
        return new TestAuthNetwork();
    }

    @Provides
    public SendLocationsUseCase provideSendLocationsUseCase(LocationRepository repository) {
        return new SendLocationsUseCaseImpl(repository);
    }
}
