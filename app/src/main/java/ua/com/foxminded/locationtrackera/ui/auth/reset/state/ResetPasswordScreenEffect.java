package ua.com.foxminded.locationtrackera.ui.auth.reset.state;

import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect;
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordContract;

public abstract class ResetPasswordScreenEffect extends AbstractEffect<ResetPasswordContract.View> {

    public static class ResetPasswordShowStatus extends ResetPasswordScreenEffect {

        public final int idStringResource;

        public ResetPasswordShowStatus(int idStringResource) {
            this.idStringResource = idStringResource;
        }

        @Override
        public void handle(ResetPasswordContract.View screen) {
            screen.showToastMessage(idStringResource);
        }
    }
}
