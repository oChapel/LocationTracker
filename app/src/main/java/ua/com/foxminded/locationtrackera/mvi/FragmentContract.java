package ua.com.foxminded.locationtrackera.mvi;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;

public class FragmentContract {

    public interface ViewModel<S, A> extends LifecycleObserver {
        LiveData<S> getStateObservable();
        LiveData<A> getEffectObservable();
    }

    public interface View {
    }

    public interface Host {
    }
}
