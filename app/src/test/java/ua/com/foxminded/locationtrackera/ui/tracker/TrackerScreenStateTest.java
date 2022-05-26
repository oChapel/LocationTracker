package ua.com.foxminded.locationtrackera.ui.tracker;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

@RunWith(MockitoJUnitRunner.class)
public class TrackerScreenStateTest {

    @Mock
    TrackerContract.View view;

    @Test
    public void test_TrackerScreenState() {
        final TrackerScreenState state = new TrackerScreenState(101, true);

        state.visit(view);

        verify(view, times(1)).changeGpsStatus(101);
        verify(view, times(1)).changeServiceStatus(true);
        verifyNoMoreInteractions(view);
    }
}
