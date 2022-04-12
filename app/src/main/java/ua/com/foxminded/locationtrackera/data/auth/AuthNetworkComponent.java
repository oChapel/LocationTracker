package ua.com.foxminded.locationtrackera.data.auth;

import dagger.Component;

import ua.com.foxminded.locationtrackera.ui.AuthViewModelFactory;

@Component(modules = AuthNetworkModule.class)
public interface AuthNetworkComponent {
    void inject(AuthViewModelFactory factory);
}
