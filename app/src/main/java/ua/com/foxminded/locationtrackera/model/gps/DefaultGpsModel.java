package ua.com.foxminded.locationtrackera.model.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import ua.com.foxminded.locationtrackera.BuildConfig;

public class DefaultGpsModel implements GpsSource {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 10;
    private static final int SMALLEST_DISPLACEMENT = 60;

    private final Context context;
    private final BehaviorSubject<Integer> gpsStatusSupplier = BehaviorSubject.create();
    private final BehaviorSubject<Location> locationSupplier = BehaviorSubject.create();
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationManager.isProviderEnabled(BuildConfig.LOCATION_PROVIDER)) {
                locationSupplier.onNext(locationResult.getLastLocation());
            }
        }
    };

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private GnssStatus.Callback gnssStatusCallback;
    private GpsStatus.Listener gpsStatusListener;

    public DefaultGpsModel(Context appContext) {
        this.context = appContext;
    }

    @Override
    public void setUpServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        checkGpsEnabled();

        locationRequest = LocationRequest.create()
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void checkGpsEnabled() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED);
        } else {
            gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED);
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
                    gpsStatusSupplier.onNext(GpsStatusConstants.CONNECTING);
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED);
                }

                @Override
                public void onStopped() {
                    gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED);
                }
            };
            locationManager.registerGnssStatusCallback(gnssStatusCallback);
        } else {
            gpsStatusListener = event -> {
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED: gpsStatusSupplier.onNext(GpsStatusConstants.CONNECTING);
                    case GpsStatus.GPS_EVENT_FIRST_FIX: gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED);
                    case GpsStatus.GPS_EVENT_STOPPED: gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED);
                }
            };
            locationManager.addGpsStatusListener(gpsStatusListener);
        }
    }

    @Override
    public Observable<Integer> getGpsStatusObservable() {
        return gpsStatusSupplier;
    }

    @Override
    public Observable<Location> getLocationObservable() {
        return locationSupplier;
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
