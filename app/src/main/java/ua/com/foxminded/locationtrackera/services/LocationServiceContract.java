package ua.com.foxminded.locationtrackera.services;

import android.location.Location;
import android.location.LocationListener;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public interface LocationServiceContract {

    interface ServiceInteractor {
        void showAlertToast(int stringId);
        void stopService();
    }

    interface Presenter {
        void onStart(LocationServiceContract.ServiceInteractor serviceInteractor);
        void saveUserLocation(Location location);
        void onDestroy();
    }

    interface Repository {
        void saveLocation(UserLocation userLocation);
        List<UserLocation> getAll();
    }
}
