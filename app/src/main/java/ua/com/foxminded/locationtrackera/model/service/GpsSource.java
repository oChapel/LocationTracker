package ua.com.foxminded.locationtrackera.model.service;

import android.location.Location;

import io.reactivex.rxjava3.core.Observable;

public interface GpsSource {

    void setUpServices();

    void startLocationUpdates();

    void registerGpsOrGnssStatusChanges();

    Observable<Integer> setGpsStatusObservable();

    Observable<Location> setLocationObservable();

    void onDestroy();
}
