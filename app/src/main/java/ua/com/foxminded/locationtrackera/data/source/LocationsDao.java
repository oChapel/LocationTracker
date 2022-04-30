package ua.com.foxminded.locationtrackera.data.source;

import java.util.List;

import ua.com.foxminded.locationtrackera.data.UserLocation;

public interface LocationsDao {

    void saveLocation(UserLocation userLocation);

    List<UserLocation> getAllLocations();

    void deleteAllLocation();
}
