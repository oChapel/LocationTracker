package ua.com.foxminded.locationtrackera.mvi.states

abstract class ScreenEffect<T> {

    abstract fun visit(screen: T)
}
