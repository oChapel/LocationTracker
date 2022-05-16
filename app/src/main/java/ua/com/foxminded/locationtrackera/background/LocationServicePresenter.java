package ua.com.foxminded.locationtrackera.background;

import android.location.Location;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.background.jobs.LocationsUploader;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.gps.GpsSource;
import ua.com.foxminded.locationtrackera.model.gps.GpsStatusConstants;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;

public class LocationServicePresenter implements LocationServiceContract.Presenter {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final BehaviorSubject<Integer> gpsStatusSupplier = BehaviorSubject.create();
    private final SendLocationsUseCase sendLocationsUseCase;
    private final GpsSource gpsServices;
    private final LocationRepository repository;
    private final TrackerCache cache;
    private WorkRequest request;

    public LocationServicePresenter(
            GpsSource gpsSource,
            LocationRepository repository,
            TrackerCache cache,
            SendLocationsUseCase sendLocationsUseCase
    ) {
        this.gpsServices = gpsSource;
        this.repository = repository;
        this.cache = cache;
        this.sendLocationsUseCase = sendLocationsUseCase;
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
    public boolean saveUserLocation(Location location) {
        if (location != null) {
            repository.saveLocation(new UserLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    Calendar.getInstance().getTimeInMillis()
            ));
        }
        return repository.getAllLocations().size() >= 5;
    }

    private void setObservers() {
        compositeDisposable.addAll(
                gpsServices.getGpsStatusObservable().subscribe(status -> {
                    cache.setGpsStatus(status);
                    int resourceStatusString = 0;
                    if (status == GpsStatusConstants.CONNECTING) {
                        resourceStatusString = R.string.connecting;
                    } else if (status == GpsStatusConstants.FIX_ACQUIRED) {
                        resourceStatusString = R.string.enabled;
                    } else if (status == GpsStatusConstants.FIX_NOT_ACQUIRED) {
                        resourceStatusString = R.string.disabled;
                    }
                    gpsStatusSupplier.onNext(resourceStatusString);
                }),

                gpsServices.getLocationObservable()
                        .observeOn(Schedulers.io())
                        .flatMapSingle(location -> Single.just(saveUserLocation(location)))
                        .flatMapSingle(aBoolean -> {
                            if (aBoolean) {
                                return sendLocationsUseCase.execute();
                            }
                            return null;
                        })
                        .subscribe(result -> {
                            if (result.isSuccessful()) {
                                repository.deleteLocationsFromDb();
                            } else {
                                WorkManager.getInstance(App.getInstance()).enqueue(request);
                            }
                        }, Throwable::printStackTrace)
        );
    }

    private void setLocationsUploader() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        request = new OneTimeWorkRequest
                .Builder(LocationsUploader.class)
                .setConstraints(constraints)
                .build();
    }

    @Override
    public Observable<Integer> getGpsStatusObservable() {
        return gpsStatusSupplier;
    }

    @Override
    public void onDestroy() {
        gpsServices.onDestroy();
        compositeDisposable.dispose();
        cache.serviceStatusChanged(false);
    }
}
