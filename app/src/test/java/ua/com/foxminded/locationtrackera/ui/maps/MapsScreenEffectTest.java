package ua.com.foxminded.locationtrackera.ui.maps;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect;

@RunWith(MockitoJUnitRunner.class)
public class MapsScreenEffectTest {

    @Mock
    MapsContract.View view;

    @Test
    public void test_LogoutEffect() {
        final MapsScreenEffect action = new MapsScreenEffect.Logout();
        action.visit(view);

        verify(view, times(1)).proceedToSplashScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_PlaceMarkersEffect() {
        final List<UserLocation> locationList = new ArrayList<>();
        final MapsScreenEffect action = new MapsScreenEffect.PlaceMarkers(locationList);
        action.visit(view);

        verify(view, times(1)).placeLocationMarkers(locationList);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_ShowToastEffect() {
        final MapsScreenEffect action = new MapsScreenEffect.ShowToast(100);
        action.visit(view);

        verify(view, times(1)).showToastMessage(100);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void test_ShowDialogFragmentEffect() {
        final MapsScreenEffect action = new MapsScreenEffect.ShowDialogFragment();
        action.visit(view);

        verify(view, times(1)).showDialogFragment();
        verifyNoMoreInteractions(view);
    }
}
