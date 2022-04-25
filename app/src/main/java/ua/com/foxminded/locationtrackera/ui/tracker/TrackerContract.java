package ua.com.foxminded.locationtrackera.ui.tracker;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

public class TrackerContract {

    public interface ViewModel extends FragmentContract.ViewModel<TrackerScreenState, TrackerScreenEffect> {
        void logout();
    }

    public interface View extends FragmentContract.View {
        void proceedToSplashScreen();

        void changeGpsStatus(int gpsStatus);

        void changeServiceStatus(boolean isEnabled);
    }

    public interface Host extends FragmentContract.Host {
    }
}
