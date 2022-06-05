package ua.com.foxminded.locationtrackera.background;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.TrampolineSchedulerRule;
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.gps.GpsSource;
import ua.com.foxminded.locationtrackera.model.gps.GpsStatusConstants;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.util.Result;

@RunWith(MockitoJUnitRunner.class)
public class LocationServicePresenterTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public TrampolineSchedulerRule schedulerRule = new TrampolineSchedulerRule();

    @Mock
    GpsSource gpsServices;
    @Mock
    LocationRepository repository;
    @Mock
    TrackerCache cache;
    @Mock
    SendLocationsUseCase sendLocationsUseCase;
    @Mock
    UploadWorkModel workModel;
    private LocationServicePresenter presenter;

    @Before
    public void setUp() {
        this.presenter = new LocationServicePresenter(
                gpsServices, repository, cache, sendLocationsUseCase, workModel
        );
        when(gpsServices.getGpsStatusObservable()).thenReturn(Observable.just(0));
    }

    private void verifyNoMore() {
        verifyNoMoreInteractions(
                gpsServices, repository, cache, sendLocationsUseCase, workModel
        );
    }

    private void verifyServiceSetUp() {
        verify(gpsServices, times(1)).onServiceStarted();
        verify(gpsServices, times(1)).getLocationObservable();
        verify(cache, times(1)).serviceStatusChanged(true);
        verify(workModel, times(1)).setLocationsUploader();
    }

    @Test
    public void serviceStartedTest() {
        when(gpsServices.getLocationObservable()).thenReturn(Observable.empty());
        presenter.onStart();

        verifyServiceSetUp();
        verifyNoMore();
    }

    @Test
    public void serviceDestroyedTest() {
        when(gpsServices.getLocationObservable()).thenReturn(Observable.empty());
        presenter.onStart();
        verifyServiceSetUp();

        presenter.onDestroy();
        verify(gpsServices, times(1)).onServiceStopped();
        verify(cache, times(1)).serviceStatusChanged(false);
        verifyNoMore();
    }

    @Test
    public void saveLocationTest_LessThenFiveLocationsInDb() {
        doNothing().when(repository).saveLocation(any(UserLocation.class));
        when(repository.getAllLocations()).thenReturn(new ArrayList<>());

        assertFalse(presenter.saveUserLocation(mock(Location.class)));

        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verify(repository, times(1)).getAllLocations();
        verifyNoMore();
    }

    @Test
    public void saveLocationTest_FiveOrMoreLocationsInDb() {
        final List<UserLocation> locationList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            locationList.add(mock(UserLocation.class));
        }

        doNothing().when(repository).saveLocation(any(UserLocation.class));
        when(repository.getAllLocations()).thenReturn(locationList);

        assertTrue(presenter.saveUserLocation(mock(Location.class)));

        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verify(repository, times(1)).getAllLocations();
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_LessThenFiveLocationsInDb() {
        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(Location.class)));
        when(repository.getAllLocations()).thenReturn(new ArrayList<>());

        presenter.onStart();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyServiceSetUp();
        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verify(repository, times(1)).getAllLocations();
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_FiveOrMoreLocationInDb_SendingSuccess() {
        final List<UserLocation> locationList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            locationList.add(mock(UserLocation.class));
        }

        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(Location.class)));
        when(repository.getAllLocations()).thenReturn(locationList);
        when(sendLocationsUseCase.execute()).thenReturn(Single.just(new Result.Success<>(null)));
        doNothing().when(repository).deleteLocationsFromDb();

        presenter.onStart();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyServiceSetUp();
        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verify(repository, times(1)).getAllLocations();
        verify(repository, times(1)).deleteLocationsFromDb();
        verify(sendLocationsUseCase, times(1)).execute();
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_FiveOrMoreLocationInDb_SendingFailure() {
        final List<UserLocation> locationList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            locationList.add(mock(UserLocation.class));
        }

        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(Location.class)));
        when(repository.getAllLocations()).thenReturn(locationList);
        when(sendLocationsUseCase.execute()).thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        doNothing().when(workModel).enqueueRequest();

        presenter.onStart();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyServiceSetUp();
        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verify(repository, times(1)).getAllLocations();
        verify(sendLocationsUseCase, times(1)).execute();
        verify(workModel, times(1)).enqueueRequest();
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_FiveOrMoreLocationInDb_ThrowError() {
        final List<UserLocation> locationList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            locationList.add(mock(UserLocation.class));
        }

        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(Location.class)));
        when(repository.getAllLocations()).thenReturn(locationList);
        when(sendLocationsUseCase.execute()).thenThrow(new RuntimeException("some error"));

        presenter.onStart();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyServiceSetUp();
        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verify(repository, times(1)).getAllLocations();
        verify(sendLocationsUseCase, times(1)).execute();
        verifyNoMore();
    }

    private void checkGpsStatusObservableValue(int inputConstant, int expected) {
        when(gpsServices.getGpsStatusObservable()).thenReturn(Observable.just(inputConstant));

        presenter.getGpsStatusObservable()
                .test()
                .assertValue(expected);

        verify(gpsServices, times(1)).getGpsStatusObservable();
        verifyNoMore();
    }

    @Test
    public void gpsStatusObservableTest_StatusConnecting() {
        checkGpsStatusObservableValue(GpsStatusConstants.ACTIVE, R.string.connecting);
    }

    @Test
    public void gpsStatusObservableTest_StatusFixAcquired() {
        checkGpsStatusObservableValue(GpsStatusConstants.FIX_ACQUIRED, R.string.enabled);
    }

    @Test
    public void gpsStatusObservableTest_StatusFixNotAcquired() {
        checkGpsStatusObservableValue(GpsStatusConstants.FIX_NOT_ACQUIRED, R.string.disabled);
    }
}
