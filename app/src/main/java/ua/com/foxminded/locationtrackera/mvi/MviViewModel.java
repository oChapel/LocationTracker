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

public abstract class MviViewModel<S, A> extends ViewModel implements FragmentContract.ViewModel<S, A>, LifecycleEventObserver {

    private final CompositeDisposable onStopDisposable = new CompositeDisposable();
    private final CompositeDisposable onDestroyDisposable = new CompositeDisposable();
    private final MutableLiveData<S> stateHolder = new MutableLiveData<>();
    private final MutableLiveData<A> actionHolder = new MutableLiveData<>();

    @Override
    public MutableLiveData<S> getStateObservable() {
        return stateHolder;
    }

    @Override
    public MutableLiveData<A> getEffectObservable() {
        return actionHolder;
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

    protected void setState(S state) {
        stateHolder.setValue(state);
    }

    protected void postState(S state) {
        stateHolder.postValue(state);
    }

    protected void setAction(A action) {
        actionHolder.setValue(action);
    }

    protected void postAction(A action) {
        actionHolder.postValue(action);
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
