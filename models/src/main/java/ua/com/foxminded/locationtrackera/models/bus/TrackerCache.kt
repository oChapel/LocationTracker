package ua.com.foxminded.locationtrackera.models.bus

import io.reactivex.rxjava3.core.Observable

interface TrackerCache {
    fun serviceStatusChanged(isRunning: Boolean)
    fun setServiceStatusObservable(): Observable<Boolean>
}