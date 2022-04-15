package ua.com.foxminded.locationtrackera.ui.auth;

import dagger.Component;
import ua.com.foxminded.locationtrackera.model.AppModule;

@Component(modules = AppModule.class)
public interface AuthNetworkComponent {
    void inject(AuthViewModelFactory factory);
}
