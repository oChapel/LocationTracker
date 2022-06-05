package ua.com.foxminded.locationtrackera.ui.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.TrampolineSchedulerRule;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

@RunWith(MockitoJUnitRunner.class)
public class MapsViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public TrampolineSchedulerRule schedulerRule = new TrampolineSchedulerRule();

    @Mock
    AuthNetwork authNetwork;
    @Mock
    LocationRepository repository;
    @Mock
    Observer<MapsScreenState> stateObserver;
    @Mock
    Observer<MapsScreenEffect> effectObserver;
    private MapsViewModel model;
    private ArgumentCaptor<MapsScreenEffect> actionCaptor;

    @Before
    public void setUp() {
        this.model = new MapsViewModel(authNetwork, repository);
        model.getStateObservable().observeForever(stateObserver);
        model.getEffectObservable().observeForever(effectObserver);

        actionCaptor = ArgumentCaptor.forClass(MapsScreenEffect.class);
        model.onStateChanged(null, Lifecycle.Event.ON_RESUME);
    }

    @After
    public void tearDown() {
        model.getStateObservable().removeObserver(stateObserver);
        model.getEffectObservable().removeObserver(effectObserver);
    }

    private void verifyNoMore() {
        verifyNoMoreInteractions(stateObserver, effectObserver);
        verifyNoMoreInteractions(authNetwork, repository);
    }

    private void checkRetrievingFailed() {
        model.retrieveLocationsByDate(100, 101);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(repository, times(1)).retrieveLocations(anyDouble(), anyDouble());
        assertTrue(actionCaptor.getValue() instanceof MapsScreenEffect.ShowToast);
        assertEquals(R.string.retrieve_failed, ((MapsScreenEffect.ShowToast) actionCaptor.getValue()).resId);
        verifyNoMore();
    }

    @Test
    public void retrieveLocationsTest_InvalidTimeline() {
        model.retrieveLocationsByDate(101, 100);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        assertTrue(actionCaptor.getValue() instanceof MapsScreenEffect.ShowToast);
        assertEquals(R.string.invalid_time_period, ((MapsScreenEffect.ShowToast) actionCaptor.getValue()).resId);
        verifyNoMore();
    }

    @Test
    public void retrieveLocationsTest_ThrowError() {
        when(repository.retrieveLocations(anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("some error"));
        checkRetrievingFailed();
    }

    @Test
    public void retrieveLocationsTest_NoLocations() {
        when(repository.retrieveLocations(anyDouble(), anyDouble()))
                .thenReturn(Single.just(new Result.Success<>(new ArrayList<>())));
        model.retrieveLocationsByDate(100, 101);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(repository, times(1)).retrieveLocations(anyDouble(), anyDouble());
        assertTrue(actionCaptor.getValue() instanceof MapsScreenEffect.ShowToast);
        assertEquals(R.string.no_locations_in_current_period, ((MapsScreenEffect.ShowToast) actionCaptor.getValue()).resId);
        verifyNoMore();
    }

    @Test
    public void retrieveLocationsTest_DefaultTimePeriod() {
        final List<UserLocation> locationList = new ArrayList<>();
        locationList.add(mock(UserLocation.class));
        when(repository.retrieveLocations(anyDouble(), anyDouble()))
                .thenReturn(Single.just(new Result.Success<>(locationList)));
        model.retrieveDefaultLocations();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(repository, times(1)).retrieveLocations(anyDouble(), anyDouble());
        assertTrue(actionCaptor.getValue() instanceof MapsScreenEffect.PlaceMarkers);
        assertEquals(locationList, ((MapsScreenEffect.PlaceMarkers) actionCaptor.getValue()).locationList);
        verifyNoMore();
    }


    @Test
    public void retrieveLocationsTest_Success() {
        final List<UserLocation> locationList = new ArrayList<>();
        locationList.add(mock(UserLocation.class));
        when(repository.retrieveLocations(anyDouble(), anyDouble()))
                .thenReturn(Single.just(new Result.Success<>(locationList)));
        model.retrieveLocationsByDate(100, 101);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(repository, times(1)).retrieveLocations(anyDouble(), anyDouble());
        assertTrue(actionCaptor.getValue() instanceof MapsScreenEffect.PlaceMarkers);
        assertEquals(locationList, ((MapsScreenEffect.PlaceMarkers) actionCaptor.getValue()).locationList);
        verifyNoMore();
    }

    @Test
    public void retrieveLocationsTest_Failure() {
        when(repository.retrieveLocations(anyDouble(), anyDouble()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        checkRetrievingFailed();
    }

    @Test
    public void logoutTest() {
        model.logout();

        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(authNetwork, times(1)).logout();
        assertTrue(actionCaptor.getValue() instanceof MapsScreenEffect.Logout);
        verifyNoMore();
    }
}
