package ua.com.foxminded.locationtrackera.mvi;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;

public class FragmentContract {

    public interface ViewModel<S, A> extends LifecycleObserver {
        MutableLiveData<S> getStateObservable();
        MutableLiveData<A> getEffectObservable();
    }

    public interface View {
    }

    public interface Host {
    }
}
