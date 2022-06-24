package ua.com.foxminded.locationtrackera.ui.tracker.state

import ua.com.foxminded.locationtrackera.ui.tracker.TrackerContract
import ua.com.foxminded.locationtrackera.mvi.states.ScreenState

class TrackerScreenState(
    private val gpsStatus: Int,
    private val serviceStatus: Boolean
    ) : ScreenState<TrackerContract.View>() {

    override fun visit(screen: TrackerContract.View) {
        screen.changeGpsStatus(gpsStatus)
        screen.changeServiceStatus(serviceStatus)
    }
}