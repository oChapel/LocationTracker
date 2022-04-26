package ua.com.foxminded.locationtrackera.services;

import java.util.Calendar;

import android.location.Location;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import ua.com.foxminded.locationtrackera.model.auth.UserLocation;
import ua.com.foxminded.locationtrackera.model.service.GpsSource;
import ua.com.foxminded.locationtrackera.model.service.TrackerCache;

public class LocationServicePresenter implements LocationServiceContract.Presenter {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final GpsSource gpsServices;
    private final LocationServiceContract.Repository repository;
    private final TrackerCache cache;

    public LocationServicePresenter(GpsSource gpsSource, LocationServiceContract.Repository repository,
                                    TrackerCache cache) {
        this.gpsServices = gpsSource;
        this.repository = repository;
        this.cache = cache;
    }

    @Override
    public void onStart() {
        setObservers();
        gpsServices.setUpServices();
        gpsServices.startLocationUpdates();
        gpsServices.registerGpsOrGnssStatusChanges();
        cache.serviceStatusChanged(true);
    }

    @Override
    public void saveUserLocation(Location location) {
        if (location != null) {
            repository.saveLocation(new UserLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    Calendar.getInstance().getTimeInMillis()
            ));
        }
    }

    private void setObservers() {
        compositeDisposable.addAll(
                gpsServices.setGpsStatusObservable().subscribe(cache::setGpsStatus),
                gpsServices.setLocationObservable().subscribe(this::saveUserLocation)
        );
    }

    @Override
    public void onDestroy() {
        gpsServices.onDestroy();
        compositeDisposable.dispose();
        cache.serviceStatusChanged(false);
    }

}
