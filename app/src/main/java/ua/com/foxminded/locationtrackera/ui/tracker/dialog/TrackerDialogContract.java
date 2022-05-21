package ua.com.foxminded.locationtrackera.ui.tracker.dialog;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenState;

public class TrackerDialogContract {

    public interface ViewModel extends FragmentContract.ViewModel<TrackerDialogScreenState, TrackerDialogScreenEffect> {
    }

    public interface View extends FragmentContract.View {
    }

    public interface Host extends FragmentContract.Host {
    }
}
