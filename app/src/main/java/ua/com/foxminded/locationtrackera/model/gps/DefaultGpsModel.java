package ua.com.foxminded.locationtrackera.model.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import ua.com.foxminded.locationtrackera.BuildConfig;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public class DefaultGpsModel implements GpsSource {

    private final Context context;
    private final BehaviorSubject<Integer> gpsStatusSupplier = BehaviorSubject.create();
    private final BehaviorSubject<UserLocation> locationSupplier = BehaviorSubject.create();
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (!serviceRunningFlag
                    || locationManager.isProviderEnabled(BuildConfig.LOCATION_PROVIDER)) {
                return;
            }
            final Location loc = locationResult.getLastLocation();
            locationSupplier.onNext(new UserLocation(
                    loc.getLatitude(), loc.getLongitude(),
                    Calendar.getInstance().getTimeInMillis()
            ));
        }
    };

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private GnssStatus.Callback gnssStatusCallback;
    private GpsStatus.Listener gpsStatusListener;
    private boolean serviceRunningFlag = false;
    private boolean locationUpdatesFlag = false;

    public DefaultGpsModel(Context appContext) {
        this.context = appContext;
    }

    @Override
    public void onServiceStarted() {
        this.serviceRunningFlag = true;
        if (!locationUpdatesFlag) {
            startLocationUpdates();
            registerGpsOrGnssStatusChanges();
        }
    }

    @Override
    public void setUpServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationRequest = LocationRequest.create()
                .setInterval(1000 * BuildConfig.UPDATE_INTERVAL)
                .setSmallestDisplacement(BuildConfig.TRACKING_SENSIVITY)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            this.locationUpdatesFlag = true;
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
            );
        }
    }

    @Override
    public void registerGpsOrGnssStatusChanges() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                gnssStatusCallback = new GnssStatus.Callback() {
                    @Override
                    public void onStarted() {
                        gpsStatusSupplier.onNext(GpsStatusConstants.ACTIVE);
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
                        case GpsStatus.GPS_EVENT_STARTED:
                            gpsStatusSupplier.onNext(GpsStatusConstants.ACTIVE);
                            break;
                        case GpsStatus.GPS_EVENT_FIRST_FIX:
                            gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED);
                            break;
                        case GpsStatus.GPS_EVENT_STOPPED:
                            gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED);
                            break;
                    }
                };
                locationManager.addGpsStatusListener(gpsStatusListener);
            }
        } else {
            gpsStatusSupplier.onNext(GpsStatusConstants.NO_PERMISSION);
        }
    }

    @Override
    public Observable<Integer> getGpsStatusObservable() {
        return gpsStatusSupplier;
    }

    @Override
    public Observable<UserLocation> getLocationObservable() {
        return locationSupplier;
    }

    @Override
    public void onServiceStopped() {
        this.serviceRunningFlag = false;
    }

    @Override
    public void onDestroy() {
        this.locationUpdatesFlag = false;
        fusedLocationClient.removeLocationUpdates(locationCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
        } else {
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
    }
}
