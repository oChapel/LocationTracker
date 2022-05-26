package ua.com.foxminded.locationtrackera.ui.auth.registration;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationScreenStateTest {

    @Mock
    RegistrationContract.View view;

    @Test
    public void test_RegistrationProgressState() {
        final RegistrationScreenState state = new RegistrationScreenState.RegistrationProgress(true);

        state.visit(view);

        verify(view, times(1)).setProgressVisibility(true);
        verify(view, times(1)).showErrors(0, 0, 0);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_RegistrationErrorState() {
        final RegistrationScreenState state = new RegistrationScreenState.RegistrationError(100, 101, 102);

        state.visit(view);

        verify(view, times(1)).setProgressVisibility(false);
        verify(view, times(1)).showErrors(100, 101, 102);
        verifyNoMoreInteractions(view);
    }
}
