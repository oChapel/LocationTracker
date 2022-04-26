package ua.com.foxminded.locationtrackera.ui.tracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.subjects.PublishSubject;

import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.service.TrackerCache;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

public class TrackerViewModel extends MviViewModel<TrackerScreenState, TrackerScreenEffect>
        implements TrackerContract.ViewModel {

    //private final PublishSubject<Void> logoutSupplier = PublishSubject.create();
    private final AuthNetwork authNetwork;
    private final TrackerCache cache;

    private int gpsStatus;
    private boolean serviceStatus;

    public TrackerViewModel(AuthNetwork authNetwork, TrackerCache cache) {
        this.authNetwork = authNetwork;
        this.cache = cache;
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
                        .subscribe(status -> setState(new TrackerScreenState(gpsStatus, serviceStatus)))
        );
    }

    @Override
    public void logout() {
        //demo
        authNetwork.logout();
        setAction(new TrackerScreenEffect.Logout());
    }
}
