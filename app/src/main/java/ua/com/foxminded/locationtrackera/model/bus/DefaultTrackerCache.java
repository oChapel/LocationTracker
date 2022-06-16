package ua.com.foxminded.locationtrackera.model.bus;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class DefaultTrackerCache implements TrackerCache {

    private final BehaviorSubject<Boolean> serviceStatusSupplier = BehaviorSubject.create();

    @Override
    public void serviceStatusChanged(boolean isRunning) {
        serviceStatusSupplier.onNext(isRunning);
    }

    @Override
    public Observable<Boolean> setServiceStatusObservable() {
        return serviceStatusSupplier;
    }

}
