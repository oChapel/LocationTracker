package ua.com.foxminded.locationtrackera.mvi.states;

import ua.com.foxminded.locationtrackera.mvi.FragmentContract;

public abstract class ScreenState<T extends FragmentContract.View, S> {

    public abstract void visit(T screen);

    public abstract void merge(S prevState);
}
