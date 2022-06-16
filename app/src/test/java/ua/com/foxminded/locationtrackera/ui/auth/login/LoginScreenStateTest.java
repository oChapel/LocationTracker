package ua.com.foxminded.locationtrackera.ui.auth.login;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState;

@RunWith(MockitoJUnitRunner.class)
public class LoginScreenStateTest {

    @Mock
    LoginContract.View view;

    @Test
    public void test_LoginProgressState() {
        final LoginScreenState state = new LoginScreenState.LoginProgress(true);

        state.visit(view);

        verify(view, times(1)).setProgressVisibility(true);
        verify(view, times(1)).showEmailAndPasswordError(0, 0);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_LoginErrorState() {
        final LoginScreenState state = new LoginScreenState.LoginError(100, 101);

        state.visit(view);

        verify(view, times(1)).setProgressVisibility(false);
        verify(view, times(1)).showEmailAndPasswordError(100, 101);
        verifyNoMoreInteractions(view);
    }
}
