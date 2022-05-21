package ua.com.foxminded.locationtrackera.ui.tracker;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

public class TrackerContract {

    public interface ViewModel extends FragmentContract.ViewModel<TrackerScreenState, TrackerScreenEffect> {
        void logout();

        void setDialogResponse(int code);

        void setSharedPreferencesServiceFlag(boolean flag);
    }

    public interface View extends FragmentContract.View {
        void proceedToSplashScreen();

        void changeGpsStatus(int gpsStatus);

        void changeServiceStatus(boolean isEnabled);

        void showDialogFragment(int argType, int message, int negativeButton, int positiveButton);
    }

    public interface Host extends FragmentContract.Host {
    }
}
