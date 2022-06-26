package ua.com.foxminded.locationtrackera.models.gps

import io.reactivex.rxjava3.core.Observable
import ua.com.foxminded.locationtrackera.models.locations.UserLocation

interface GpsSource {

    val gpsStatusObservable: Observable<Int>

    val locationObservable: Observable<UserLocation>

    fun onServiceStarted()

    fun startLocationUpdates()

    fun registerGpsOrGnssStatusChanges()

    fun onServiceStopped()

    fun onDestroy()
}
