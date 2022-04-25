package ua.com.foxminded.locationtrackera.ui.tracker.state;

import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerContract;

public abstract class TrackerScreenEffect extends AbstractEffect<TrackerContract.View> {

    public static class Logout extends TrackerScreenEffect {
        @Override
        public void handle(TrackerContract.View screen) {
            screen.proceedToSplashScreen();
        }
    }
}
