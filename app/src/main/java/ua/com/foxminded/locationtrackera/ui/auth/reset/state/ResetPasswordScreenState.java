package ua.com.foxminded.locationtrackera.ui.auth.reset.state;

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState;
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordContract;

public abstract class ResetPasswordScreenState extends ScreenState<ResetPasswordContract.View> {

    public final boolean isProgressVisible;
    public final int emailError;

    public ResetPasswordScreenState(boolean isProgressVisible) {
        this.isProgressVisible = isProgressVisible;
        this.emailError = 0;
    }

    public ResetPasswordScreenState(boolean isProgressVisible, int emailError) {
        this.isProgressVisible = isProgressVisible;
        this.emailError = emailError;
    }

    @Override
    public void visit(ResetPasswordContract.View screen) {
        screen.setProgressVisibility(isProgressVisible);
        screen.showEmailError(emailError);
    }

    public static class ResetPasswordProgress extends ResetPasswordScreenState {
        public ResetPasswordProgress(boolean isProgressVisible) {
            super(isProgressVisible);
        }
    }

    public static class ResetPasswordError extends ResetPasswordScreenState {
        public ResetPasswordError(int emailError) {
            super(false, emailError);
        }
    }
}
