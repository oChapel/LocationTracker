package ua.com.foxminded.locationtrackera.models_impl.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import ua.com.foxminded.locationtrackera.models.gps.GpsSource;
import ua.com.foxminded.locationtrackera.models.locations.UserLocation;

public class TestGpsModel implements GpsSource {

    private final Context context;
    private final BehaviorSubject<Integer> gpsStatusSupplier = BehaviorSubject.create();
    private final BehaviorSubject<UserLocation> locationSupplier = BehaviorSubject.create();

    public TestGpsModel(Context context) {
        this.context = context;
    }

    @Override
    public void onServiceStarted() {
        startLocationUpdates();
        registerGpsOrGnssStatusChanges();
    }

    @Override
    public void startLocationUpdates() {
        locationSupplier.onNext(new UserLocation(
                100.00, 100.00,
                Calendar.getInstance().getTimeInMillis()
        ));
    }

    @Override
    public void registerGpsOrGnssStatusChanges() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            gpsStatusSupplier.onNext(GpsStatusConstants.ACTIVE);

            final Handler handler = new Handler();
            handler.postDelayed(() -> gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED), 1000);
        } else {
            gpsStatusSupplier.onNext(GpsStatusConstants.NO_PERMISSION);
        }
    }

    @NotNull
    @Override
    public Observable<Integer> getGpsStatusObservable() {
        return gpsStatusSupplier;
    }

    @NotNull
    @Override
    public Observable<UserLocation> getLocationObservable() {
        return locationSupplier;
    }

    @Override
    public void onServiceStopped() {
    }

    @Override
    public void onDestroy() {
    }
}
