package ua.com.foxminded.locationtrackera.ui.auth.reset;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState;

public class ResetPasswordContract {

    public interface ViewModel extends FragmentContract.ViewModel<ResetPasswordScreenState, ResetPasswordScreenEffect> {
        void resetPassword(String email);
    }

    public interface View extends FragmentContract.View {
        void setProgressVisibility(boolean isProgressVisible);

        void showToastMessage(int idStringResource);

        void showEmailError(int emailError);
    }

    public interface Host extends FragmentContract.Host {
    }
}
