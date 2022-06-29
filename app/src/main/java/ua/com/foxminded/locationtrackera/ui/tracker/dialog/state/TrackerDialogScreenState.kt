package ua.com.foxminded.locationtrackera.ui.tracker.dialog.state

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.TrackerDialogContract

sealed class TrackerDialogScreenState : ScreenState<TrackerDialogContract.View>() {

    override fun visit(screen: TrackerDialogContract.View) {}
}
