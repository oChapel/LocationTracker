package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Component;
import ua.com.foxminded.locationtrackera.services.LocationService;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;

@Component(modules = {AppModule.class, ServiceModule.class, DataModule.class})
@Singleton
public interface AppComponent {
    void inject(AuthViewModelFactory factory);
    void inject(LocationService service);
}
