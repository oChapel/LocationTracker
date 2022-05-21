package ua.com.foxminded.locationtrackera.model.locations.dao;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public interface LocationsDao {

    void saveLocation(UserLocation userLocation);

    List<UserLocation> getAllLocations();

    void deleteAllLocation();
}
