package ua.com.foxminded.locationtrackera.ui.tracker.dialog

import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenEffect
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenState

class TrackerDialogContract {
    interface ViewModel : FragmentContract.ViewModel<
            TrackerDialogScreenState,
            TrackerDialogScreenEffect>

    interface View : FragmentContract.View
    interface Host : FragmentContract.Host
}
