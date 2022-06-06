package ua.com.foxminded.locationtrackera.model.gps;

import io.reactivex.rxjava3.core.Observable;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public interface GpsSource {

    void onServiceStarted();

    void setUpServices();

    void startLocationUpdates();

    void registerGpsOrGnssStatusChanges();

    Observable<Integer> getGpsStatusObservable();

    Observable<UserLocation> getLocationObservable();

    void onServiceStopped();

    void onDestroy();
}
