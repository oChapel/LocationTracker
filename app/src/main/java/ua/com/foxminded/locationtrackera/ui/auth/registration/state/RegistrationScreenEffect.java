package ua.com.foxminded.locationtrackera.ui.auth.registration.state;

import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect;
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationContract;

public abstract class RegistrationScreenEffect extends AbstractEffect<RegistrationContract.View> {

    public static class RegistrationSuccessful extends RegistrationScreenEffect {
        @Override
        public void handle(RegistrationContract.View screen) {
            screen.proceedToNextScreen();
        }
    }

    public static class RegistrationFailed extends RegistrationScreenEffect {
        @Override
        public void handle(RegistrationContract.View screen) {
            screen.showFailureToastMessage();
        }
    }
}
