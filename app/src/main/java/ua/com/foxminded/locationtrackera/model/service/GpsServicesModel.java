package ua.com.foxminded.locationtrackera.model.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import ua.com.foxminded.locationtrackera.services.LocationServiceContract;
import ua.com.foxminded.locationtrackera.services.StatusListener;

public class GpsServicesModel implements LocationServiceContract.GpsServices {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 10;
    private static final int SMALLEST_DISPLACEMENT = 60;

    private final Application application;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationManager locationManager;
    private Location currentLocation;
    private GnssStatus.Callback gnssStatusCallback;
    private GpsStatus.Listener gpsStatusListener;
    private LocationListener locationListener;
    private StatusListener statusListener;

    public GpsServicesModel(Application application) {
        this.application = application;
    }

    @Override
    public void setUpServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        checkGpsEnabled();

        locationRequest = LocationRequest.create()
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                locationListener.onLocationChanged(currentLocation);
            }
        };
    }

    private void checkGpsEnabled() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            statusListener.onGpsStatusChanged(GpsStatusConstants.FIX_ACQUIRED);
        } else {
            statusListener.onGpsStatusChanged(GpsStatusConstants.FIX_NOT_ACQUIRED);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startLocationUpdates() {
        fusedLocationClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void registerGpsOrGnssStatusChanges() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gnssStatusCallback = new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    statusListener.onGpsStatusChanged(GpsStatusConstants.CONNECTING);
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    statusListener.onGpsStatusChanged(GpsStatusConstants.FIX_ACQUIRED);
                }

                @Override
                public void onStopped() {
                    statusListener.onGpsStatusChanged(GpsStatusConstants.FIX_NOT_ACQUIRED);
                }

            };
            locationManager.registerGnssStatusCallback(gnssStatusCallback);
        } else {
            gpsStatusListener = event -> {
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED: statusListener.onGpsStatusChanged(GpsStatusConstants.CONNECTING);
                    case GpsStatus.GPS_EVENT_FIRST_FIX: statusListener.onGpsStatusChanged(GpsStatusConstants.FIX_ACQUIRED);
                    case GpsStatus.GPS_EVENT_STOPPED: statusListener.onGpsStatusChanged(GpsStatusConstants.FIX_NOT_ACQUIRED);
                }
            };
            locationManager.addGpsStatusListener(gpsStatusListener);
        }
    }

    @Override
    public void setStatusListener(StatusListener listener) {
        this.statusListener = listener;
    }

    @Override
    public void setLocationListener(LocationListener listener) {
        this.locationListener = listener;
    }

    @Override
    public void onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
        } else {
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
    }
}
