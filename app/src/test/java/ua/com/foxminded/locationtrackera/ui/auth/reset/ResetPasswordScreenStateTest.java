package ua.com.foxminded.locationtrackera.ui.auth.reset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordScreenStateTest {

    @Mock
    ResetPasswordContract.View view;

    @Test
    public void test_ResetPasswordProgressState() {
        final ResetPasswordScreenState state = new ResetPasswordScreenState.ResetPasswordProgress(true);

        state.visit(view);

        verify(view, times(1)).setProgressVisibility(true);
        verify(view, times(1)).showEmailError(0);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_ResetPasswordErrorState() {
        final ResetPasswordScreenState state = new ResetPasswordScreenState.ResetPasswordError(101);

        state.visit(view);

        verify(view, times(1)).setProgressVisibility(false);
        verify(view, times(1)).showEmailError(101);
        verifyNoMoreInteractions(view);
    }
}
