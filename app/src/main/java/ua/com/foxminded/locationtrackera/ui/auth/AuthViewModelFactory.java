package ua.com.foxminded.locationtrackera.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.di.AppComponent;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginViewModel;
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationViewModel;
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordViewModel;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerViewModel;

public class AuthViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppComponent component = App.getComponent();

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
        } else if (modelClass.isAssignableFrom(TrackerViewModel.class)) {
            return (T) new TrackerViewModel(authNetwork);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
