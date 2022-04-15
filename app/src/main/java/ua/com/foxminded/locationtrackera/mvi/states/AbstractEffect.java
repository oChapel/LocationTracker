package ua.com.foxminded.locationtrackera.mvi.states;

public abstract class AbstractEffect<T> extends ScreenEffect<T> {

    private boolean isHandled = false;

    @Override
    public void visit(T screen) {
        if (!isHandled) {
            handle(screen);
            isHandled = true;
        }
    }

    public abstract void handle(T screen);
}
