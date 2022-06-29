package ua.com.foxminded.locationtrackera.ui.maps.state

import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect
import ua.com.foxminded.locationtrackera.ui.maps.MapsContract

sealed class MapsScreenEffect : AbstractEffect<MapsContract.View>() {

    class Logout : MapsScreenEffect() {
        override fun handle(screen: MapsContract.View) {
            screen.proceedToSplashScreen()
        }
    }

    class PlaceMarkers(val locationList: List<UserLocation>) : MapsScreenEffect() {
        override fun handle(screen: MapsContract.View) {
            screen.placeLocationMarkers(locationList)
        }
    }

    class ShowToast(val resId: Int) : MapsScreenEffect() {
        override fun handle(screen: MapsContract.View) {
            screen.showToastMessage(resId)
        }
    }
}
