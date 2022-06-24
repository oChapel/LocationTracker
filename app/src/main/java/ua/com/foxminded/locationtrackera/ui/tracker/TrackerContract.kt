package ua.com.foxminded.locationtrackera.ui.tracker

import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState
import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract

class TrackerContract {
    interface ViewModel : FragmentContract.ViewModel<
            TrackerScreenState,
            TrackerScreenEffect> {
        fun logout()
        fun setDialogResponse(code: Int)
        fun setSharedPreferencesServiceFlag(flag: Boolean)
    }

    interface View : FragmentContract.View {
        fun proceedToSplashScreen()
        fun changeGpsStatus(gpsStatus: Int)
        fun changeServiceStatus(isEnabled: Boolean)
        fun showDialogFragment(argType: Int, message: Int, negativeButton: Int, positiveButton: Int)
    }

    interface Host : FragmentContract.Host
}