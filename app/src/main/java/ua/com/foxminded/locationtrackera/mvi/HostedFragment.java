package ua.com.foxminded.locationtrackera.mvi;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.lang.reflect.ParameterizedType;

import ua.com.foxminded.locationtrackera.mvi.states.ScreenEffect;
import ua.com.foxminded.locationtrackera.mvi.states.ScreenState;

public abstract class HostedFragment<
        VIEW extends FragmentContract.View,
        STATE extends ScreenState<VIEW>,
        EFFECT extends ScreenEffect<VIEW>,
        VIEW_MODEL extends FragmentContract.ViewModel<STATE, EFFECT>,
        HOST extends FragmentContract.Host>
        extends Fragment
        implements FragmentContract.View, Observer<STATE> {

    private VIEW_MODEL model;
    private HOST fragmentHost;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentHost = (HOST) context;
        } catch (Throwable e) {
            final String hostClassName = ((Class) ((ParameterizedType) getClass().
                    getGenericSuperclass())
                    .getActualTypeArguments()[1]).getCanonicalName();
            throw new RuntimeException("Activity must implement " + hostClassName
                    + " to attach " + this.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel(createModel());
        if (getModel() != null) {
            getLifecycle().addObserver(getModel());
            getModel().getStateObservable().observe(this, this);
            getModel().getEffectObservable().observe(this, effect -> effect.visit((VIEW) this));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentHost = null;
    }

    @Override
    public void onDestroy() {
        if (getModel() != null) {
            getModel().getEffectObservable().removeObservers(this);
            getModel().getStateObservable().removeObservers(this);
            getLifecycle().removeObserver(getModel());
        }
        super.onDestroy();
    }

    @Override
    public void onChanged(STATE screenState) {
        screenState.visit((VIEW) this);
    }

    protected abstract VIEW_MODEL createModel();

    protected VIEW_MODEL getModel() {
        return model;
    }

    protected void setModel(VIEW_MODEL model) {
        this.model = model;
    }

    protected boolean hasHost() {
        return fragmentHost != null;
    }

    protected HOST getFragmentHost() {
        return fragmentHost;
    }
}
