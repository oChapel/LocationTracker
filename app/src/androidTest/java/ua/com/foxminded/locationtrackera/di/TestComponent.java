package ua.com.foxminded.locationtrackera.di;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Component;
import ua.com.foxminded.locationtrackera.background.LocationService;
import ua.com.foxminded.locationtrackera.background.jobs.LocationsUploader;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;

@Component(modules = {TestModule.class, TestServiceModule.class, TestDataModule.class})
@Singleton
public interface TestComponent extends AppComponent {
    void inject(@NotNull AuthViewModelFactory factory);
    void inject(@NotNull LocationService service);
    void inject(@NotNull LocationsUploader worker);
}
