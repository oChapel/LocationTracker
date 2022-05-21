package ua.com.foxminded.locationtrackera.model.bus;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class DefaultTrackerCache implements TrackerCache {

    private final BehaviorSubject<Integer> gpsStatusSupplier = BehaviorSubject.create();
    private final BehaviorSubject<Boolean> serviceStatusSupplier = BehaviorSubject.create();

    @Override
    public void setGpsStatus(int status) {
        gpsStatusSupplier.onNext(status);
    }

    @Override
    public void serviceStatusChanged(boolean isRunning) {
        serviceStatusSupplier.onNext(isRunning);
    }

    @Override
    public Observable<Integer> setGpsStatusObservable() {
        return gpsStatusSupplier;
    }

    @Override
    public Observable<Boolean> setServiceStatusObservable() {
        return serviceStatusSupplier;
    }

}
