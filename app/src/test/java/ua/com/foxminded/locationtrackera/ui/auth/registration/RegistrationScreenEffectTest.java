package ua.com.foxminded.locationtrackera.ui.auth.registration;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationScreenEffectTest {

    @Mock
    RegistrationContract.View view;

    @Test
    public void test_RegistrationSuccessfulEffect() {
        final RegistrationScreenEffect action = new RegistrationScreenEffect.RegistrationSuccessful();

        action.visit(view);

        verify(view, times(1)).proceedToNextScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_RegistrationFailedEffect() {
        final RegistrationScreenEffect action = new RegistrationScreenEffect.RegistrationFailed();

        action.visit(view);

        verify(view, times(1)).showFailureToastMessage();
        verifyNoMoreInteractions(view);
    }
}
