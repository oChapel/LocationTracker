package ua.com.foxminded.locationtrackera.ui.auth.login.state;

import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public abstract class LoginScreenEffect extends AbstractEffect<LoginContract.View> {

    public static class LoginSuccessful extends LoginScreenEffect {
        @Override
        public void handle(LoginContract.View screen) {
            screen.hideProgress();
            screen.proceedToNextScreen();
        }
    }

    public static class LoginFailed extends LoginScreenEffect {
        @Override
        public void handle(LoginContract.View screen) {
            screen.hideProgress();
            screen.showFailureToastMessage();
        }
    }
}
