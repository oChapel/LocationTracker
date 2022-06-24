package ua.com.foxminded.locationtrackera.ui.tracker.state

import ua.com.foxminded.locationtrackera.ui.tracker.TrackerContract
import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect

abstract class TrackerScreenEffect : AbstractEffect<TrackerContract.View>() {

    class Logout : TrackerScreenEffect() {
        override fun handle(screen: TrackerContract.View) {
            screen.proceedToSplashScreen()
        }
    }

    class ShowDialogFragment(
        val argType: Int,
        val message: Int,
        val negativeButton: Int,
        val positiveButton: Int
    ) : TrackerScreenEffect() {

        override fun handle(screen: TrackerContract.View) {
            screen.showDialogFragment(argType, message, negativeButton, positiveButton)
        }
    }
}