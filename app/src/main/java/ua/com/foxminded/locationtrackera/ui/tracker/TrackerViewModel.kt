package ua.com.foxminded.locationtrackera.ui.tracker

import androidx.lifecycle.Lifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache
import ua.com.foxminded.locationtrackera.models.gps.GpsSource
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import ua.com.foxminded.locationtrackera.models.util.Result
import ua.com.foxminded.locationtrackera.mvi.MviViewModel
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect.ShowDialogFragment
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState

class TrackerViewModel(
    private val authNetwork: AuthNetwork,
    private val cache: TrackerCache,
    private val repository: ua.com.foxminded.locationtrackera.models.locations.LocationRepository,
    private val sendLocationsUseCase: SendLocationsUseCase,
    private val sharedPreferencesModel: SharedPreferencesModel,
    private val gpsServices: GpsSource
) : MviViewModel<
        TrackerScreenState,
        TrackerScreenEffect>(),
    TrackerContract.ViewModel {

    private val logoutSupplier = PublishSubject.create<Int>()
    private val responseSupplier = PublishSubject.create<Int>()

    private var gpsStatus = 0
    private var serviceStatus = false

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            gpsServices.startLocationUpdates()
            gpsServices.registerGpsOrGnssStatusChanges()
        }
        if (event == Lifecycle.Event.ON_RESUME) {
            setUpTrackerChain()
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            gpsServices.onDestroy()
        }
    }

    private fun setUpTrackerChain() {
        addTillDestroy(
            gpsServices.gpsStatusObservable
                .doOnNext { status -> gpsStatus = status }
                .subscribe { setState(TrackerScreenState(gpsStatus, serviceStatus)) },
            cache.setServiceStatusObservable()
                .doOnNext { status -> serviceStatus = status }
                .subscribe { setState(TrackerScreenState(gpsStatus, serviceStatus)) },

            logoutSupplier.observeOn(Schedulers.io())
                .flatMapSingle { Single.just(repository.getAllLocations().isEmpty()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { aBoolean: Boolean ->
                    if (aBoolean) {
                        authNetwork.logout()
                        setAction(TrackerScreenEffect.Logout())
                    } else {
                        setAction(
                            ShowDialogFragment(
                                0,
                                R.string.db_not_empty_alert_message,
                                R.string.delete_uppercase,
                                R.string.send_uppercase
                            )
                        )
                    }
                },

            responseSupplier.observeOn(Schedulers.io())
                .flatMapSingle { code: Int ->
                    if (code == 0) {
                        return@flatMapSingle sendLocationsUseCase.execute()
                    } else {
                        return@flatMapSingle Single.just(Result.Success<Void?>(null))
                    }
                }.subscribe({ result: Result<Void?> ->
                    if (result.isSuccessful) {
                        repository.deleteLocationsFromDb()
                        authNetwork.logout()
                        postAction(TrackerScreenEffect.Logout())
                    } else {
                        showFailedToSendDialogFragment()
                    }
                }) { error: Throwable ->
                    error.printStackTrace()
                    showFailedToSendDialogFragment()
                }
        )
    }

    private fun showFailedToSendDialogFragment() {
        postAction(
            ShowDialogFragment(
                1,
                R.string.failed_to_send_alert_message,
                R.string.cancel,
                R.string.logout_uppercase
            )
        )
    }

    override fun setSharedPreferencesServiceFlag(flag: Boolean) =
        sharedPreferencesModel.setSharedPreferencesServiceFlag(flag)

    override fun logout() = logoutSupplier.onNext(0)

    override fun setDialogResponse(code: Int) = responseSupplier.onNext(code)
}