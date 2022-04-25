package ua.com.foxminded.locationtrackera.services;

import java.util.Calendar;

import javax.inject.Inject;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public class LocationServicePresenter implements LocationServiceContract.Presenter, StatusListener, LocationListener {

    private final LocationServiceContract.ServiceInteractor serviceInteractions;

    @Inject
    LocationServiceContract.GpsServices gpsServices;

    @Inject
    LocationServiceContract.Repository repository;

    @Inject
    LocationServiceContract.Cache cache;

    public LocationServicePresenter(LocationServiceContract.ServiceInteractor serviceInteractions) {
        this.serviceInteractions = serviceInteractions;
        App.getComponent().inject(this);
    }

    @Override
    public void init() {
        gpsServices.setStatusListener(this);
        gpsServices.setLocationListener(this);
        gpsServices.setUpServices();
        gpsServices.startLocationUpdates();
        gpsServices.registerGpsOrGnssStatusChanges();
        onServiceStatusChanged(true);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        saveUserLocation(location);
    }

    @Override
    public void onGpsStatusChanged(int gpsStatus) {
        cache.setGpsStatus(gpsStatus);
    }

    @Override
    public void onServiceStatusChanged(boolean isRunning) {
        cache.serviceStatusChanged(isRunning);
    }

    @Override
    public void onDestroy() {
        gpsServices.onDestroy();
        onServiceStatusChanged(false);
    }

}
