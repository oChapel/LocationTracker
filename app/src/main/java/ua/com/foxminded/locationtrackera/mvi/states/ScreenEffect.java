package ua.com.foxminded.locationtrackera.mvi.states;

public abstract class ScreenEffect<T> {

    public abstract void visit(T screen);
}
