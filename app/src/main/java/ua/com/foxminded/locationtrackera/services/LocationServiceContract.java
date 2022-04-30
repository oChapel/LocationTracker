package ua.com.foxminded.locationtrackera.services;

import android.location.Location;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.data.UserLocation;
import ua.com.foxminded.locationtrackera.util.Result;

public interface LocationServiceContract {

    interface Presenter {
        void onStart();
        void saveUserLocation(Location location);
        void onDestroy();
    }

    interface Repository {
        void saveLocation(UserLocation userLocation);
        void deleteLocationsFromDb();
        List<UserLocation> getAllLocations();
        Single<Result<Void>> saveLocationsToNetwork(List<UserLocation> locationList);
    }
}
