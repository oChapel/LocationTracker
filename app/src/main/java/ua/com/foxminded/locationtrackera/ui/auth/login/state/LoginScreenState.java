package ua.com.foxminded.locationtrackera.ui.auth.login.state;

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public abstract class LoginScreenState extends ScreenState<LoginContract.View> {

    public final boolean isProgressVisible;
    public final int emailError;
    public final int passwordError;

    public LoginScreenState(boolean isProgressVisible) {
        this.isProgressVisible = isProgressVisible;
        this.emailError = 0;
        this.passwordError = 0;
    }

    public LoginScreenState(boolean isProgressVisible, int emailError, int passwordError) {
        this.isProgressVisible = isProgressVisible;
        this.emailError = emailError;
        this.passwordError = passwordError;
    }

    @Override
    public void visit(LoginContract.View screen) {
        screen.setProgressVisibility(isProgressVisible);
        screen.showEmailAndPasswordError(emailError, passwordError);
    }

    public static class LoginProgress extends LoginScreenState {
        public LoginProgress(boolean isProgressVisible) {
            super(isProgressVisible);
        }
    }

    public static class LoginError extends LoginScreenState {
        public LoginError(int emailError, int passwordError) {
            super(false, emailError, passwordError);
        }
    }
}
