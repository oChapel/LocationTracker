package ua.com.foxminded.locationtrackera.model.bus;

import io.reactivex.rxjava3.core.Observable;

public interface TrackerCache {

    void serviceStatusChanged(boolean isRunning);

    Observable<Boolean> setServiceStatusObservable();
}
