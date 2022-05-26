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

    public static class ShowDialogFragment extends TrackerScreenEffect {

        public final int argType;
        public final int message;
        public final int negativeButton;
        public final int positiveButton;

        public ShowDialogFragment(int argType, int message, int negativeButton, int positiveButton) {
            this.argType = argType;
            this.message = message;
            this.negativeButton = negativeButton;
            this.positiveButton = positiveButton;
        }

        @Override
        public void handle(TrackerContract.View screen) {
            screen.showDialogFragment(argType, message, negativeButton, positiveButton);
        }
    }
}
