package ua.com.foxminded.locationtrackera.background

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import ua.com.foxminded.locationtrackera.models.locations.UserLocation

interface LocationServiceContract {

    interface Presenter {

        fun onStart()

        fun saveUserLocation(location: UserLocation): Completable

        fun onDestroy()

        val gpsStatusObservable: Observable<Int>
    }
}
