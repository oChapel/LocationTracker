package ua.com.foxminded.locationtrackera.ui.auth.login.state;

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public abstract class LoginScreenState extends ScreenState<LoginContract.View, LoginScreenState> {

    private final boolean isProgressVisible;
    protected final int emailError;
    protected final int passwordError;

    public LoginScreenState(boolean isProgressVisible) {
        this.isProgressVisible = isProgressVisible;
        this.emailError = -1;
        this.passwordError = -1;
    }

    public LoginScreenState(boolean isProgressVisible, int emailError, int passwordError) {
        this.isProgressVisible = isProgressVisible;
        this.emailError = emailError;
        this.passwordError = passwordError;
    }

    @Override
    public void visit(LoginContract.View screen) {
        if (isProgressVisible) {
            screen.showProgress();
        } else {
            screen.hideProgress();
        }
    }

    public static class LoginInProgress extends LoginScreenState {
        public LoginInProgress() {
            super(true);
        }

        @Override
        public void merge(LoginScreenState prevState) {
        }
    }

    public static class LoginError extends LoginScreenState {
        public LoginError(int emailError, int passwordError) {
            super(false, emailError, passwordError);
        }

        @Override
        public void merge(LoginScreenState prevState) {
        }

        @Override
        public void visit(LoginContract.View screen) {
            super.visit(screen);
            screen.showEmailAndPasswordError(emailError, passwordError);
        }
    }
}
