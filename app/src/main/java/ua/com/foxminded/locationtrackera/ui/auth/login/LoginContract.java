package ua.com.foxminded.locationtrackera.ui.auth.login;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;

public class LoginContract {

    public interface ViewModel extends FragmentContract.ViewModel<LoginScreenState> {
        void login(String email, String password);
    }

    public interface View extends FragmentContract.View {
        void showProgress();

        void proceedToNextScreen();

        void showFailureToastMessage();

        void showEmailAndPasswordError(int emailError, int passwordError);
    }

    public interface Host extends FragmentContract.Host {
    }
}
