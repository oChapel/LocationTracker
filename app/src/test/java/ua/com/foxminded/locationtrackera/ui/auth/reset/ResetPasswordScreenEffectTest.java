package ua.com.foxminded.locationtrackera.ui.auth.reset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordScreenEffectTest {

    @Mock
    ResetPasswordContract.View view;

    @Test
    public void test_ResetPasswordShowStatusEffect() {
        final ResetPasswordScreenEffect action = new ResetPasswordScreenEffect.ResetPasswordShowStatus(101);
        action.visit(view);

        verify(view, times(1)).showToastMessage(101);
        verifyNoMoreInteractions(view);
    }
}
