package ua.com.foxminded.locationtrackera.mvi

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract

abstract class MviViewModel<S, A> : ViewModel(), FragmentContract.ViewModel<S, A> {

    private val onStopDisposable = CompositeDisposable()
    private val onDestroyDisposable = CompositeDisposable()
    private val stateHolder = MutableLiveData<S>()
    private val actionHolder = MutableLiveData<A>()

    override fun getStateObservable(): MutableLiveData<S> = stateHolder

    override fun getEffectObservable(): MutableLiveData<A> = actionHolder

    @CallSuper
    override fun onStateChanged(event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                onStopDisposable.clear()
                onDestroyDisposable.clear()
            }
            Lifecycle.Event.ON_DESTROY -> onDestroyDisposable.clear()
            else -> return
        }
    }

    protected fun setState(state: S) {
        stateHolder.value = state
    }

    protected fun setAction(action: A) {
        actionHolder.value = action
    }

    protected fun addTillDestroy(vararg disposables: Disposable) {
        onDestroyDisposable.addAll(*disposables)
    }

    @CallSuper
    override fun onCleared() {
        onDestroyDisposable.dispose()
        onStopDisposable.dispose()
    }
}
