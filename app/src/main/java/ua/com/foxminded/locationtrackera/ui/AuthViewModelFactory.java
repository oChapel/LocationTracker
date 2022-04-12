package ua.com.foxminded.locationtrackera.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import ua.com.foxminded.locationtrackera.data.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.data.auth.AuthNetworkComponent;
import ua.com.foxminded.locationtrackera.data.auth.DaggerAuthNetworkComponent;
import ua.com.foxminded.locationtrackera.ui.login.LoginViewModel;
import ua.com.foxminded.locationtrackera.ui.login.ResetPasswordViewModel;
import ua.com.foxminded.locationtrackera.ui.registration.RegistrationViewModel;

public class AuthViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AuthNetworkComponent component = DaggerAuthNetworkComponent.create();

    @Inject
    AuthNetwork authNetwork;

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        component.inject(this);
        if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
            return (T) new RegistrationViewModel(authNetwork);
        } else if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(authNetwork);
        } else if (modelClass.isAssignableFrom(ResetPasswordViewModel.class)) {
            return (T) new ResetPasswordViewModel(authNetwork);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
