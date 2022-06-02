package ua.com.foxminded.locationtrackera.background;

import android.location.Location;

import java.util.Calendar;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel;
import ua.com.foxminded.locationtrackera.model.bus.TrackerCache;
import ua.com.foxminded.locationtrackera.model.gps.GpsSource;
import ua.com.foxminded.locationtrackera.model.gps.GpsStatusConstants;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;

public class LocationServicePresenter implements LocationServiceContract.Presenter {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SendLocationsUseCase sendLocationsUseCase;
    private final GpsSource gpsServices;
    private final LocationRepository repository;
    private final TrackerCache cache;
    private final UploadWorkModel workModel;

    public LocationServicePresenter(
            GpsSource gpsSource,
            LocationRepository repository,
            TrackerCache cache,
            SendLocationsUseCase sendLocationsUseCase,
            UploadWorkModel workModel) {
        this.gpsServices = gpsSource;
        this.repository = repository;
        this.cache = cache;
        this.sendLocationsUseCase = sendLocationsUseCase;
        this.workModel = workModel;
    }

    @Override
    public void onStart() {
        setObservers();
        gpsServices.setUpServices();
        gpsServices.startLocationUpdates();
        gpsServices.registerGpsOrGnssStatusChanges();
        cache.serviceStatusChanged(true);
        workModel.setLocationsUploader();
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
                gpsServices.getGpsStatusObservable().subscribe(cache::setGpsStatus),

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
                                workModel.enqueueRequest();
                            }
                        }, Throwable::printStackTrace)
        );
    }

    @Override
    public Observable<Integer> getGpsStatusObservable() {
        return gpsServices.getGpsStatusObservable().map(status -> {
            if (status == GpsStatusConstants.ACTIVE) {
                return R.string.connecting;
            } else if (status == GpsStatusConstants.FIX_ACQUIRED) {
                return R.string.enabled;
            } else if (status == GpsStatusConstants.FIX_NOT_ACQUIRED) {
                return R.string.disabled;
            } else {
                return 0;
            }
        });
    }

    @Override
    public void onDestroy() {
        gpsServices.onDestroy();
        compositeDisposable.dispose();
        cache.serviceStatusChanged(false);
    }
}
