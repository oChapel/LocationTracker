package ua.com.foxminded.locationtrackera;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

/**
 * ViewModel provider factory to instantiate RegistrationViewModel.
 * Required given RegistrationViewModel has a non-empty constructor
 */
public class RegistrationViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
            return (T) new RegistrationViewModel(FirebaseAuth.getInstance());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}