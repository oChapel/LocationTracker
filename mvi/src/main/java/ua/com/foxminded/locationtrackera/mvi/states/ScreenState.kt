package ua.com.foxminded.locationtrackera.mvi.states

import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract

abstract class ScreenState<T : FragmentContract.View?> {

    abstract fun visit(screen: T)
}