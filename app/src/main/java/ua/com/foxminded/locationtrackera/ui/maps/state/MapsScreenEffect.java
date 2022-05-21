package ua.com.foxminded.locationtrackera.ui.maps.state;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect;
import ua.com.foxminded.locationtrackera.ui.maps.MapsContract;

public abstract class MapsScreenEffect extends AbstractEffect<MapsContract.View> {

    public static class Logout extends MapsScreenEffect {
        @Override
        public void handle(MapsContract.View screen) {
            screen.proceedToSplashScreen();
        }
    }

    public static class PlaceMarkers extends MapsScreenEffect {
        private final List<UserLocation> locationList;

        public PlaceMarkers(List<UserLocation> locationList) {
            this.locationList = locationList;
        }

        @Override
        public void handle(MapsContract.View screen) {
            screen.placeLocationMarkers(locationList);
        }
    }

    public static class ShowToast extends MapsScreenEffect {
        private final int resId;

        public ShowToast(int resId) {
            this.resId = resId;
        }

        @Override
        public void handle(MapsContract.View screen) {
            screen.showToastMessage(resId);
        }
    }
}
