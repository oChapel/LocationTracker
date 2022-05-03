package ua.com.foxminded.locationtrackera.ui.tracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

public class TrackerViewModel extends MviViewModel<TrackerScreenState, TrackerScreenEffect>
        implements TrackerContract.ViewModel, SendLocationsUseCase.Listener {

    private final PublishSubject<Integer> logoutSupplier = PublishSubject.create();
    private final SendLocationsUseCase sendLocationsUseCase = new SendLocationsUseCase(this);
    private final AuthNetwork authNetwork;
    private final TrackerCache cache;
    private final LocationRepository repository;

    private int gpsStatus;
    private boolean serviceStatus;

    public TrackerViewModel(
            AuthNetwork authNetwork,
            TrackerCache cache,
            LocationRepository repository) {
        this.authNetwork = authNetwork;
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        if (event == Lifecycle.Event.ON_CREATE) {
            setUpTrackerChain();
        }
    }

    private void setUpTrackerChain() {
        addTillDestroy(
                cache.setGpsStatusObservable()
                        .doOnNext(status -> gpsStatus = status)
                        .subscribe(status -> setState(new TrackerScreenState(gpsStatus, serviceStatus))),
                cache.setServiceStatusObservable()
                        .doOnNext(status -> serviceStatus = status)
                        .subscribe(status -> setState(new TrackerScreenState(gpsStatus, serviceStatus))),

                logoutSupplier.observeOn(Schedulers.io())
                        .flatMapSingle(locationList -> Single.just(repository.getAllLocations().isEmpty()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                authNetwork.logout();
                                setAction(new TrackerScreenEffect.Logout());
                            } else {
                                setAction(new TrackerScreenEffect.ShowDialogFragment(
                                        0,
                                        R.string.db_not_empty_alert_message,
                                        R.string.delete_uppercase,
                                        R.string.send_uppercase
                                ));
                            }
                        })
        );
    }

    @Override
    public void logout() {
        logoutSupplier.onNext(0);
    }

    @Override
    public void setDialogResponse(int code) {
        if (code == 0) {
            sendLocationsUseCase.execute();
        } else {
            onLocationsSent();
        }
    }

    @Override
    public void onLocationsSent() {
        repository.deleteLocationsFromDb();
        authNetwork.logout();
        postAction(new TrackerScreenEffect.Logout());
    }

    @Override
    public void onSendingFailed() {
        postAction(new TrackerScreenEffect.ShowDialogFragment(
                1,
                R.string.failed_to_send_alert_message,
                android.R.string.cancel,
                R.string.logout_uppercase
        ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        sendLocationsUseCase.dispose();
    }
}
