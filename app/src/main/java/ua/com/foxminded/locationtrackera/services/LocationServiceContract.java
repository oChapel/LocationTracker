package ua.com.foxminded.locationtrackera.services;

import android.location.Location;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public interface LocationServiceContract {

    interface Presenter {
        void onStart();
        void saveUserLocation(Location location);
        void onDestroy();
    }

    interface Repository {
        void saveLocation(UserLocation userLocation);
        List<UserLocation> getAll();
    }
}
