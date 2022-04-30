package ua.com.foxminded.locationtrackera.ui.tracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.background.LocationServiceContract;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

public class TrackerViewModel extends MviViewModel<TrackerScreenState, TrackerScreenEffect>
        implements TrackerContract.ViewModel {

    private final PublishSubject<Integer> logoutSupplier = PublishSubject.create();
    private final PublishSubject<Integer> responseSupplier = PublishSubject.create();
    private final AuthNetwork authNetwork;
    private final TrackerCache cache;
    private final LocationServiceContract.Repository repository;

    private int gpsStatus;
    private boolean serviceStatus;

    public TrackerViewModel(
            AuthNetwork authNetwork,
            TrackerCache cache,
            LocationServiceContract.Repository repository) {
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
                        }),

                responseSupplier.observeOn(Schedulers.io())
                        .flatMapSingle(code -> {
                            if (code == 0) {
                                return repository.saveLocationsToNetwork(repository.getAllLocations());
                            } else {
                                return Single.just(new Result.Success<>(null));
                            }
                        }).subscribe(result -> {
                            if (result.isSuccessful()) {
                                repository.deleteLocationsFromDb();
                                authNetwork.logout();
                                postAction(new TrackerScreenEffect.Logout());
                            } else {
                                postAction(new TrackerScreenEffect.ShowDialogFragment(
                                        1,
                                        R.string.failed_to_send_alert_message,
                                        android.R.string.cancel,
                                        R.string.logout_uppercase
                                ));
                            }
                        }, error -> {
                            error.printStackTrace();
                            postAction(new TrackerScreenEffect.ShowDialogFragment(
                                    1,
                                    R.string.failed_to_send_alert_message,
                                    android.R.string.cancel,
                                    R.string.logout_uppercase
                            ));
                        })
        );
    }

    @Override
    public void logout() {
        logoutSupplier.onNext(0);
    }

    @Override
    public void setDialogResponse(int code) {
        responseSupplier.onNext(code);
    }
}
