package ua.com.foxminded.locationtrackera.background;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
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
        gpsServices.onServiceStarted();
        cache.serviceStatusChanged(true);
        workModel.setLocationsUploader();
    }

    @Override
    public @NonNull Completable saveUserLocation(UserLocation location) {
        return Completable.fromRunnable(() -> repository.saveLocation(location));
    }

    private void setObservers() {
        compositeDisposable.addAll(
                gpsServices.getLocationObservable()
                        .observeOn(Schedulers.io())
                        .flatMapCompletable(this::saveUserLocation)
                        .andThen(Single.defer(sendLocationsUseCase::execute))
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
        gpsServices.onServiceStopped();
        compositeDisposable.dispose();
        cache.serviceStatusChanged(false);
    }
}
