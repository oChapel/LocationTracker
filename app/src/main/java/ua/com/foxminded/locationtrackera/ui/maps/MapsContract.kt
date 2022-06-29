package ua.com.foxminded.locationtrackera.ui.maps

import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState

class MapsContract {
    interface ViewModel : FragmentContract.ViewModel<MapsScreenState, MapsScreenEffect> {
        fun logout()
        fun retrieveLocationsByDate(startDate: Long, endDate: Long)
        fun retrieveDefaultLocations()
    }

    interface View : FragmentContract.View {
        fun proceedToSplashScreen()
        fun placeLocationMarkers(locationList: List<UserLocation>)
        fun showToastMessage(resId: Int)
    }

    interface Host : FragmentContract.Host
}
