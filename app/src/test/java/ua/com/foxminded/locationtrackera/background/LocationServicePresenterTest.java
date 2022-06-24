package ua.com.foxminded.locationtrackera.background;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.TrampolineSchedulerRule;
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel;
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.models.gps.GpsSource;
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.models.locations.UserLocation;
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.models.util.Result;
import ua.com.foxminded.locationtrackera.models_impl.gps.GpsStatusConstants;

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
    }

    @Test
    public void serviceDestroyedTest() {
        presenter.onDestroy();

        verify(gpsServices, times(1)).onServiceStopped();
        verify(cache, times(1)).serviceStatusChanged(false);
        verifyNoMore();
    }

    @Test
    public void saveLocationTest() {
        doNothing().when(repository).saveLocation(any(UserLocation.class));
        presenter.saveUserLocation(mock(UserLocation.class))
                .test()
                .assertComplete()
                .assertNoErrors();


        verify(repository, times(1)).saveLocation(any(UserLocation.class));
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_SendingSuccess() {
        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(UserLocation.class)));
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
        verify(repository, times(1)).deleteLocationsFromDb();
        verify(sendLocationsUseCase, times(1)).execute();
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_SendingFailure() {
        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(UserLocation.class)));
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
        verify(sendLocationsUseCase, times(1)).execute();
        verify(workModel, times(1)).enqueueRequest();
        verifyNoMore();
    }

    @Test
    public void locationReceivedTest_ThrowError() {
        when(gpsServices.getLocationObservable()).thenReturn(Observable.just(mock(UserLocation.class)));
        when(sendLocationsUseCase.execute()).thenThrow(new RuntimeException("some error"));

        presenter.onStart();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyServiceSetUp();
        verify(repository, times(1)).saveLocation(any(UserLocation.class));
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
