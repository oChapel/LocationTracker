package ua.com.foxminded.locationtrackera.ui.tracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TrackerScreenEffectTest {

    @Mock
    TrackerContract.View view;

    @Test
    public void test_LogoutEffect() {
        final TrackerScreenEffect action = new TrackerScreenEffect.Logout();
        action.visit(view);

        verify(view, times(1)).proceedToSplashScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_ShowDialogFragmentEffect() {
        final TrackerScreenEffect action = new TrackerScreenEffect.ShowDialogFragment(100, 101, 102, 103);
        action.visit(view);

        verify(view, times(1)).showDialogFragment(100, 101, 102, 103);
        verifyNoMoreInteractions(view);
    }
}
