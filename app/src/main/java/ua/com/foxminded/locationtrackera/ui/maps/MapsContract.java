package ua.com.foxminded.locationtrackera.ui.maps;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.mvi.FragmentContract;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState;

public class MapsContract {

    public interface ViewModel extends FragmentContract.ViewModel<MapsScreenState, MapsScreenEffect> {
        void logout();

        void retrieveLocationsByDate(double startDate, double endDate);

        void retrieveDefaultLocations();
    }

    public interface View extends FragmentContract.View {
        void proceedToSplashScreen();

        void placeLocationMarkers(List<UserLocation> locationList);

        void showToastMessage(int resId);
    }

    public interface Host extends FragmentContract.Host {
    }
}
