package ua.com.foxminded.locationtrackera.di;

import javax.inject.Singleton;

import dagger.Component;
import ua.com.foxminded.locationtrackera.services.LocationServicePresenter;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerViewModel;

@Component(modules = {AppModule.class, ServiceModule.class})
@Singleton
public interface AppComponent {
    void inject(AuthViewModelFactory factory);
    void inject(LocationServicePresenter presenter);
    void inject(TrackerViewModel viewModel);
}
