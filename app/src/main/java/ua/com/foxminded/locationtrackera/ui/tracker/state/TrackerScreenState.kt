package ua.com.foxminded.locationtrackera.ui.tracker.state

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerContract

sealed class TrackerScreenState : ScreenState<TrackerContract.View>() {

    class ProvideStatus(
        private val gpsStatus: Int,
        private val serviceStatus: Boolean
    ) : TrackerScreenState() {
        override fun visit(screen: TrackerContract.View) {
            screen.changeGpsStatus(gpsStatus)
            screen.changeServiceStatus(serviceStatus)
        }
    }
}
