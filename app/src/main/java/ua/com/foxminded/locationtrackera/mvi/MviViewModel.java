package ua.com.foxminded.locationtrackera.mvi;

import androidx.annotation.CallSuper;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class MviViewModel<T> extends ViewModel implements FragmentContract.ViewModel<T> {

    //private final CompositeDisposable onStopDisposable = new CompositeDisposable();
    //private final CompositeDisposable onDestroyDisposable = new CompositeDisposable();
    private final MutableLiveData<T> stateHolder = new MutableLiveData<>();

    @Override
    public MutableLiveData<T> getStateObservable() {
        return stateHolder;
    }

    //impl DefaultLifecycleObserver?
/*    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    protected void onAny(LifecycleOwner owner, Lifecycle.Event event) {
        switch (event) {
            //case ON_STOP: onStopDisposable.clear();
            //case ON_DESTROY: onDestroyDisposable.clear();
        }
    }*/

    protected void setState(T state) {
        stateHolder.setValue(state);
    }
}
