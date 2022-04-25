package ua.com.foxminded.locationtrackera.ui.tracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;
import ua.com.foxminded.locationtrackera.services.StatusListener;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

public class TrackerViewModel extends MviViewModel<TrackerScreenState, TrackerScreenEffect>
        implements TrackerContract.ViewModel, StatusListener {

    //private final PublishSubject<Void> logoutSupplier = PublishSubject.create();
    private final PublishSubject<Integer> statusSupplier = PublishSubject.create();
    private final AuthNetwork authNetwork;
    private int gpsStatus;
    private boolean serviceStatus;

    @Inject
    LocationServiceContract.Cache cache;

    public TrackerViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        if (event == Lifecycle.Event.ON_CREATE) {
            App.getComponent().inject(this);
            setUpTrackerChain();
            setStatusListener();
        }
    }

    private void setUpTrackerChain() {
        addTillDestroy(
                statusSupplier.doOnNext(integer -> setState(new TrackerScreenState(gpsStatus, serviceStatus)))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe()
        );
    }


    public void setStatusListener() {
        cache.setStatusListener(this);
    }

    @Override
    public void logout() {
        //demo
        authNetwork.logout();
        setAction(new TrackerScreenEffect.Logout());
    }

    @Override
    public void onGpsStatusChanged(int gpsStatus) {
        this.gpsStatus = gpsStatus;
        statusSupplier.onNext(0);
    }

    @Override
    public void onServiceStatusChanged(boolean isRunning) {
        this.serviceStatus = isRunning;
        statusSupplier.onNext(0);
    }
}
