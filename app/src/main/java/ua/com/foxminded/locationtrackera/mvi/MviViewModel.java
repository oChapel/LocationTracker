package ua.com.foxminded.locationtrackera.mvi;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class MviViewModel<T> extends ViewModel implements FragmentContract.ViewModel<T>, LifecycleEventObserver {

    private final CompositeDisposable onStopDisposable = new CompositeDisposable();
    private final CompositeDisposable onDestroyDisposable = new CompositeDisposable();
    private final MutableLiveData<T> stateHolder = new MutableLiveData<>();

    @Override
    public MutableLiveData<T> getStateObservable() {
        return stateHolder;
    }

    @CallSuper
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_STOP:
                onStopDisposable.clear();
            case ON_DESTROY:
                onDestroyDisposable.clear();
        }
    }

    protected void setState(T state) {
        stateHolder.setValue(state);
    }

    protected void addTillDestroy(Disposable... disposables) {
        onDestroyDisposable.addAll(disposables);
    }

    @CallSuper
    @Override
    protected void onCleared() {
        onDestroyDisposable.dispose();
        onStopDisposable.dispose();
    }
}
