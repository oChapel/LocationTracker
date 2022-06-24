package ua.com.foxminded.locationtrackera.ui.tracker.dialog

import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenEffect
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenState
import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract

class TrackerDialogContract {
    interface ViewModel : FragmentContract.ViewModel<
            TrackerDialogScreenState,
            TrackerDialogScreenEffect>

    interface View : FragmentContract.View
    interface Host : FragmentContract.Host
}