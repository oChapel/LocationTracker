package ua.com.foxminded.locationtrackera.background

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.background.LocationServiceContract.Presenter
import ua.com.foxminded.locationtrackera.background.jobs.UploadWorkModel
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache
import ua.com.foxminded.locationtrackera.models.gps.GpsSource
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import ua.com.foxminded.locationtrackera.models.util.Result
import ua.com.foxminded.locationtrackera.models_impl.gps.GpsStatusConstants

class LocationServicePresenter(
    private val gpsServices: GpsSource,
    private val repository: LocationRepository,
    private val cache: TrackerCache,
    private val sendLocationsUseCase: SendLocationsUseCase,
    private val workModel: UploadWorkModel
) : Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onStart() {
        setObservers()
        gpsServices.onServiceStarted()
        cache.serviceStatusChanged(true)
    }

    override fun saveUserLocation(location: UserLocation): Completable =
        Completable.fromRunnable { repository.saveLocation(location) }

    private fun setObservers() {
        compositeDisposable.addAll(
            gpsServices.locationObservable
                .observeOn(Schedulers.io())
                .flatMapCompletable { location: UserLocation -> saveUserLocation(location) }
                .andThen(Single.defer(sendLocationsUseCase::execute))
                .subscribe({ result: Result<Void?> ->
                    if (result.isSuccessful) {
                        repository.deleteLocationsFromDb()
                    } else {
                        workModel.enqueueRequest()
                    }
                }) { error: Throwable -> error.printStackTrace() }
        )
    }

    override val gpsStatusObservable: Observable<Int>
        get() = gpsServices.gpsStatusObservable.map { status: Int ->
            when (status) {
                GpsStatusConstants.ACTIVE -> return@map R.string.connecting
                GpsStatusConstants.FIX_ACQUIRED -> return@map R.string.enabled
                GpsStatusConstants.FIX_NOT_ACQUIRED -> return@map R.string.disabled
                else -> return@map 0
            }
        }

    override fun onDestroy() {
        gpsServices.onServiceStopped()
        compositeDisposable.dispose()
        cache.serviceStatusChanged(false)
    }
}