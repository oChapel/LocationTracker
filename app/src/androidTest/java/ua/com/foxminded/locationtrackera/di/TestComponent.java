package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Component;
import ua.com.foxminded.locationtrackera.background.LocationService;
import ua.com.foxminded.locationtrackera.background.jobs.LocationsUploader;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;

@Component(modules = {TestModule.class, ServiceModule.class, TestDataModule.class})
@Singleton
public interface TestComponent extends AppComponent {
    void inject(AuthViewModelFactory factory);
    void inject(LocationService service);
    void inject(LocationsUploader worker);
}
