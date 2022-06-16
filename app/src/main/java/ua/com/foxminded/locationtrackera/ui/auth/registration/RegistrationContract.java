package ua.com.foxminded.locationtrackera.ui.auth.registration;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState;

public class RegistrationContract {

    public interface ViewModel extends FragmentContract.ViewModel<RegistrationScreenState, RegistrationScreenEffect> {
        void registerUser(String username, String email, String password);

        void registrationDataChanged(String username, String email, String password);
    }

    public interface View extends FragmentContract.View {
        void setProgressVisibility(boolean isProgressVisible);

        void showErrors(int usernameError, int emailError, int passwordError);

        void proceedToNextScreen();

        void showFailureToastMessage(int resId);
    }

    public interface Host extends FragmentContract.Host{
    }
}
