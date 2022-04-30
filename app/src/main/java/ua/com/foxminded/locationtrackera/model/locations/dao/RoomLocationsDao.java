package ua.com.foxminded.locationtrackera.model.locations.dao;

import android.app.Application;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public class RoomLocationsDao implements LocationsDao {

    private final UserLocationDao userLocationDao;

    public RoomLocationsDao(Application application) {
        final LocationDatabase database = LocationDatabase.getInstance(application);
        userLocationDao = database.userLocationDao();
    }

    @Override
    public void saveLocation(UserLocation userLocation) {
        userLocationDao.saveLocation(userLocation);
    }

    @Override
    public List<UserLocation> getAllLocations() {
        return userLocationDao.getAllLocations();
    }

    @Override
    public void deleteAllLocation() {
        userLocationDao.deleteAllLocation();
    }
}
