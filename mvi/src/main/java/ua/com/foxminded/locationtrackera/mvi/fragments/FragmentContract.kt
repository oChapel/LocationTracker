package ua.com.foxminded.locationtrackera.mvi.fragments

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData

class FragmentContract {

    interface ViewModel<S, A> : LifecycleObserver {
        fun onStateChanged(event: Lifecycle.Event)
        fun getStateObservable(): LiveData<S>
        fun getEffectObservable(): LiveData<A>
    }

    interface View
    interface Host
}