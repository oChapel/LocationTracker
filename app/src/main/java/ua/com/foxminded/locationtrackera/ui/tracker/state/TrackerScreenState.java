package ua.com.foxminded.locationtrackera.ui.tracker.state;

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerContract;

public class TrackerScreenState extends ScreenState<TrackerContract.View> {

    private final int gpsStatus;
    private final boolean serviceStatus;

    public TrackerScreenState(int gpsStatus, boolean serviceStatus) {
        this.gpsStatus = gpsStatus;
        this.serviceStatus = serviceStatus;
    }

    @Override
    public void visit(TrackerContract.View screen) {
        screen.changeGpsStatus(gpsStatus);
        screen.changeServiceStatus(serviceStatus);
    }
}
