package ua.com.foxminded.locationtrackera.ui.auth.registration.state;

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState;
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationContract;

public abstract class RegistrationScreenState extends ScreenState<RegistrationContract.View> {

    public final boolean isProgressVisible;
    public final int usernameError;
    public final int emailError;
    public final int passwordError;

    public RegistrationScreenState(boolean isProgressVisible) {
        this.isProgressVisible = isProgressVisible;
        this.usernameError = 0;
        this.emailError = 0;
        this.passwordError = 0;
    }

    public RegistrationScreenState(boolean isProgressVisible, int usernameError, int emailError, int passwordError) {
        this.isProgressVisible = isProgressVisible;
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
    }

    @Override
    public void visit(RegistrationContract.View screen) {
        screen.setProgressVisibility(isProgressVisible);
        screen.showErrors(usernameError, emailError, passwordError);
    }

    public static class RegistrationProgress extends RegistrationScreenState {
        public RegistrationProgress(boolean isProgressVisible) {
            super(isProgressVisible);
        }
    }

    public static class RegistrationError extends RegistrationScreenState {
        public RegistrationError(int usernameError, int emailError, int passwordError) {
            super(false, usernameError, emailError, passwordError);
        }
    }
}
