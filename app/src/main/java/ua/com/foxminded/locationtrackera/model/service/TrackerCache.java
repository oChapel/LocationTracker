package ua.com.foxminded.locationtrackera.model.service;

import io.reactivex.rxjava3.core.Observable;

public interface TrackerCache {

    void serviceStatusChanged(boolean isRunning);

    void setGpsStatus(int status);

    Observable<Integer> setGpsStatusObservable();

    Observable<Boolean> setServiceStatusObservable();
}
