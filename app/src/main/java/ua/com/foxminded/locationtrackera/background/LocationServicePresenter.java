package ua.com.foxminded.locationtrackera.background;

import android.location.Location;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.background.jobs.LocationsUploader;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.gps.GpsSource;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public class LocationServicePresenter implements LocationServiceContract.Presenter {

    public static final int WORK_REQUEST_REPEAT_INTERVAL_MIN = 40;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final WorkManager workManager = WorkManager.getInstance(App.getInstance());
    private final GpsSource gpsServices;
    private final LocationServiceContract.Repository repository;
    private final TrackerCache cache;

    public LocationServicePresenter(
            GpsSource gpsSource,
            LocationServiceContract.Repository repository,
            TrackerCache cache
    ) {
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
        setLocationsUploader();
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
                gpsServices.setLocationObservable()
                        .observeOn(Schedulers.io())
                        .subscribe(this::saveUserLocation, Throwable::printStackTrace)
        );
    }

    private void setLocationsUploader() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        final WorkRequest request = new PeriodicWorkRequest
                .Builder(LocationsUploader.class, WORK_REQUEST_REPEAT_INTERVAL_MIN, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        workManager.enqueue(request);
    }

    @Override
    public void onDestroy() {
        gpsServices.onDestroy();
        compositeDisposable.dispose();
        workManager.cancelAllWork();
        cache.serviceStatusChanged(false);
    }
}
