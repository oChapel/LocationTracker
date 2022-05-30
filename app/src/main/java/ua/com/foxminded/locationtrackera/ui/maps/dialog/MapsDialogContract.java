package ua.com.foxminded.locationtrackera.ui.maps.dialog;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.maps.dialog.state.MapsDialogScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.dialog.state.MapsDialogScreenState;

public class MapsDialogContract {

    public interface ViewModel extends FragmentContract.ViewModel<MapsDialogScreenState, MapsDialogScreenEffect> {
    }

    public interface View extends FragmentContract.View {
    }

    public interface Host extends FragmentContract.Host {
    }
}
