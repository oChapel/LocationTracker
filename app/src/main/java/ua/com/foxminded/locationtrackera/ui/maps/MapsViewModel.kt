package ua.com.foxminded.locationtrackera.ui.maps

import androidx.core.util.Pair
import androidx.lifecycle.Lifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.models.util.Result
import ua.com.foxminded.locationtrackera.mvi.MviViewModel
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState

class MapsViewModel(
    private val authNetwork: AuthNetwork,
    private val repository: LocationRepository
) : MviViewModel<MapsScreenState, MapsScreenEffect>(), MapsContract.ViewModel {

    private val locationsSupplier: PublishSubject<Pair<Long, Long>> = PublishSubject.create()

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_RESUME) {
            setUpMapsChain()
        }
    }

    private fun setUpMapsChain() {
        addTillDestroy(
            locationsSupplier.observeOn(Schedulers.io())
                .flatMapSingle { pair: Pair<Long, Long> ->
                    if (pair.first < pair.second) {
                        return@flatMapSingle repository.retrieveLocations(pair.first, pair.second)
                    } else {
                        return@flatMapSingle Single.just(
                            Result.Error<List<UserLocation>>(
                                IllegalArgumentException("Start date must be anterior to end date")
                            )
                        )
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: Result<List<UserLocation>> ->
                    if (result.isSuccessful) {
                        val list: List<UserLocation> =
                            (result as Result.Success<List<UserLocation>>).data
                        if (list.isEmpty()) {
                            setAction(MapsScreenEffect.ShowToast(R.string.no_locations_in_current_period))
                        } else {
                            setAction(MapsScreenEffect.PlaceMarkers(list))
                        }
                    } else {
                        if ((result as Result.Error<List<UserLocation>>).error is IllegalArgumentException) {
                            setAction(MapsScreenEffect.ShowToast(R.string.invalid_time_period))
                        } else {
                            setAction(MapsScreenEffect.ShowToast(R.string.retrieve_failed))
                        }
                    }
                }) { error: Throwable ->
                    error.printStackTrace()
                    setAction(MapsScreenEffect.ShowToast(R.string.retrieve_failed))
                }
        )
    }

    override fun retrieveLocationsByDate(startDate: Long, endDate: Long) {
        locationsSupplier.onNext(Pair(startDate, endDate))
    }

    override fun retrieveDefaultLocations() {
        retrieveLocationsByDate(
            System.currentTimeMillis() - DEFAULT_LOCATIONS_RETRIEVING_TIME_HOURS * 3600000,
            System.currentTimeMillis()
        )
    }

    override fun logout() {
        authNetwork.logout()
        setAction(MapsScreenEffect.Logout())
    }

    companion object {
        private const val DEFAULT_LOCATIONS_RETRIEVING_TIME_HOURS: Long = 12
    }

}
