package ua.com.foxminded.locationtrackera.model.gps;

import android.location.Location;

import io.reactivex.rxjava3.core.Observable;

public interface GpsSource {

    void setUpServices();

    void startLocationUpdates();

    void registerGpsOrGnssStatusChanges();

    Observable<Integer> getGpsStatusObservable();

    Observable<Location> getLocationObservable();

    void onDestroy();
}