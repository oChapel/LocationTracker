package ua.com.foxminded.locationtrackera.ui.auth.login;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect;

@RunWith(MockitoJUnitRunner.class)
public class LoginScreenEffectTest {

    @Mock
    LoginContract.View view;

    @Test
    public void test_LoginSuccessfulEffect() {
        final LoginScreenEffect action = new LoginScreenEffect.LoginSuccessful();

        action.visit(view);

        verify(view, times(1)).proceedToNextScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_LoginFailedEffect() {
        final LoginScreenEffect action = new LoginScreenEffect.LoginFailed();

        action.visit(view);

        verify(view, times(1)).showFailureToastMessage();
        verifyNoMoreInteractions(view);
    }
}
