package ua.com.foxminded.locationtrackera.models_impl.locations.dao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ua.com.foxminded.locationtrackera.models.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.models.locations.UserLocation;

public class TestLocationsDao implements LocationsDao {

    private final List<UserLocation> locationList = new ArrayList<>();

    @Override
    public void saveLocation(@NotNull UserLocation userLocation) {
        locationList.add(userLocation);
    }

    @NotNull
    @Override
    public List<UserLocation> getAllLocations() {
        return locationList;
    }

    @Override
    public void deleteAllLocation() {
        locationList.clear();
    }
}
