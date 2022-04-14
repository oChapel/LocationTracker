package ua.com.foxminded.locationtrackera.mvi;

public abstract class ScreenState<T extends FragmentContract.View> {

    //TODO: why visit?
    public abstract void visit(T screen);
}
