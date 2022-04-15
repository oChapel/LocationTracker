package ua.com.foxminded.locationtrackera.ui.auth.login;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState;

public class LoginContract {

    public interface ViewModel extends FragmentContract.ViewModel<LoginScreenState, LoginScreenEffect> {
        void login(String email, String password);
    }

    public interface View extends FragmentContract.View {
        void showProgress();

        void hideProgress();

        void proceedToNextScreen();

        void showFailureToastMessage();

        void showEmailAndPasswordError(int emailError, int passwordError);
    }

    public interface Host extends FragmentContract.Host {
    }
}
