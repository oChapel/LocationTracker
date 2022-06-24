package ua.com.foxminded.locationtrackera.ui.tracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.TrampolineSchedulerRule;
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.models.gps.GpsSource;
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.models.locations.UserLocation;
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel;
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.models.util.Result;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

@RunWith(MockitoJUnitRunner.class)
public class TrackerViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public TrampolineSchedulerRule schedulerRule = new TrampolineSchedulerRule();

    @Mock
    AuthNetwork authNetwork;
    @Mock
    TrackerCache cache;
    @Mock
    LocationRepository repository;
    @Mock
    SendLocationsUseCase sendLocationsUseCase;
    @Mock
    SharedPreferencesModel sharedPreferencesModel;
    @Mock
    GpsSource gpsServices;
    @Mock
    Observer<TrackerScreenState> stateObserver;
    @Mock
    Observer<TrackerScreenEffect> effectObserver;
    private TrackerViewModel model;
    private ArgumentCaptor<TrackerScreenState> stateCaptor;
    private ArgumentCaptor<TrackerScreenEffect> actionCaptor;

    @Before
    public void setUp() {
        this.model = new TrackerViewModel(
                authNetwork, cache, repository, sendLocationsUseCase, sharedPreferencesModel, gpsServices);
        model.getStateObservable().observeForever(stateObserver);
        model.getEffectObservable().observeForever(effectObserver);

        stateCaptor = ArgumentCaptor.forClass(TrackerScreenState.class);
        actionCaptor = ArgumentCaptor.forClass(TrackerScreenEffect.class);

        when(gpsServices.getGpsStatusObservable()).thenReturn(Observable.just(0));
        when(cache.setServiceStatusObservable()).thenReturn(Observable.just(true));
        model.onStateChanged(Lifecycle.Event.ON_CREATE);
        model.onStateChanged(Lifecycle.Event.ON_RESUME);
    }

    @After
    public void tearDown() {
        model.getStateObservable().removeObserver(stateObserver);
        model.getEffectObservable().removeObserver(effectObserver);
    }

    private void verifyNoMore() {
        verifyNoMoreInteractions(stateObserver, effectObserver);
        verifyNoMoreInteractions(
                authNetwork, cache, repository, sendLocationsUseCase, sharedPreferencesModel, gpsServices
        );
    }

    private void verifyTwoStatesOneAction() {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
    }

    private void verifyGpsSourceInteractions() {
        verify(gpsServices, times(1)).startLocationUpdates();
        verify(gpsServices, times(1)).registerGpsOrGnssStatusChanges();

        verify(gpsServices, times(1)).getGpsStatusObservable();
        verify(cache, times(1)).setServiceStatusObservable();
    }

    private void assertDialogFragmentArgsEqual(
            int argType, int message, int negativeButton, int positiveButton
    ) {
        assertEquals(argType, ((TrackerScreenEffect.ShowDialogFragment) actionCaptor.getValue()).getArgType());
        assertEquals(message, ((TrackerScreenEffect.ShowDialogFragment) actionCaptor.getValue()).getMessage());
        assertEquals(negativeButton, ((TrackerScreenEffect.ShowDialogFragment) actionCaptor.getValue()).getNegativeButton());
        assertEquals(positiveButton, ((TrackerScreenEffect.ShowDialogFragment) actionCaptor.getValue()).getPositiveButton());
    }

    private void checkResponseFailed() {
        model.setDialogResponse(0);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyTwoStatesOneAction();
        verifyGpsSourceInteractions();
        verify(sendLocationsUseCase, times(1)).execute();
        assertTrue(actionCaptor.getValue() instanceof TrackerScreenEffect.ShowDialogFragment);
        assertDialogFragmentArgsEqual(
                1, R.string.failed_to_send_alert_message, R.string.cancel, R.string.logout_uppercase
        );
        verifyNoMore();
    }

    @Test
    public void test_DoNothing() {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verifyGpsSourceInteractions();

        model.onStateChanged(Lifecycle.Event.ON_DESTROY);
        verify(gpsServices, times(1)).onDestroy();
        verifyNoMore();
    }

    @Test
    public void logoutTest_EmptyDb() {
        doNothing().when(authNetwork).logout();
        model.logout();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyTwoStatesOneAction();
        verifyGpsSourceInteractions();
        verify(repository, times(1)).getAllLocations();
        verify(authNetwork, times(1)).logout();
        assertTrue(actionCaptor.getValue() instanceof TrackerScreenEffect.Logout);
        verifyNoMore();
    }

    @Test
    public void logoutTest_DbNotEmpty() {
        final List<UserLocation> locationList = new ArrayList<>();
        locationList.add(mock(UserLocation.class));
        when(repository.getAllLocations()).thenReturn(locationList);
        model.logout();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyTwoStatesOneAction();
        verifyGpsSourceInteractions();
        verify(repository, times(1)).getAllLocations();
        assertTrue(actionCaptor.getValue() instanceof TrackerScreenEffect.ShowDialogFragment);
        assertDialogFragmentArgsEqual(
                0, R.string.db_not_empty_alert_message, R.string.delete_uppercase, R.string.send_uppercase
        );
        verifyNoMore();
    }

    @Test
    public void responseTest_CodeZero_ThrowError() {
        when(sendLocationsUseCase.execute()).thenThrow(new RuntimeException("some error"));
        checkResponseFailed();
    }

    @Test
    public void responseTest_CodeZero_Success() {
        when(sendLocationsUseCase.execute()).thenReturn(Single.just(new Result.Success<>(null)));
        doNothing().when(repository).deleteLocationsFromDb();
        doNothing().when(authNetwork).logout();
        model.setDialogResponse(0);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyTwoStatesOneAction();
        verifyGpsSourceInteractions();
        verify(sendLocationsUseCase, times(1)).execute();
        verify(repository, times(1)).deleteLocationsFromDb();
        verify(authNetwork, times(1)).logout();
        assertTrue(actionCaptor.getValue() instanceof TrackerScreenEffect.Logout);
        verifyNoMore();
    }

    @Test
    public void responseTest_CodeZero_Failure() {
        when(sendLocationsUseCase.execute())
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        checkResponseFailed();
    }

    @Test
    public void setSharedPreferencesFlagTest() {
        model.setSharedPreferencesServiceFlag(true);

        verifyGpsSourceInteractions();
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verify(sharedPreferencesModel, times(1)).setSharedPreferencesServiceFlag(true);
        verifyNoMore();
    }
}
