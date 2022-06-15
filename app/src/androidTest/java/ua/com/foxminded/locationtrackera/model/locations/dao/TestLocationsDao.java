package ua.com.foxminded.locationtrackera.model.locations.dao;

import java.util.ArrayList;
import java.util.List;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public class TestLocationsDao implements LocationsDao {

    private final List<UserLocation> locationList = new ArrayList<>();

    @Override
    public void saveLocation(UserLocation userLocation) {
        locationList.add(userLocation);
    }

    @Override
    public List<UserLocation> getAllLocations() {
        return locationList;
    }

    @Override
    public void deleteAllLocation() {
        locationList.clear();
    }
}
