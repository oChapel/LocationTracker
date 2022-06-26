package ua.com.foxminded.locationtrackera.models_impl.bus

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache

class DefaultTrackerCache : TrackerCache {

    private val serviceStatusSupplier = BehaviorSubject.create<Boolean>()

    override fun serviceStatusChanged(isRunning: Boolean) {
        serviceStatusSupplier.onNext(isRunning)
    }

    override fun setServiceStatusObservable(): Observable<Boolean> = serviceStatusSupplier
}
