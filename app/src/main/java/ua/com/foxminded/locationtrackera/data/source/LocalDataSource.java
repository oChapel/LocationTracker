package ua.com.foxminded.locationtrackera.data.source;

import android.app.Application;

import java.util.List;

import ua.com.foxminded.locationtrackera.data.LocationDatabase;
import ua.com.foxminded.locationtrackera.data.UserLocationDao;
import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public class LocalDataSource implements LocationDataSource {

    private final UserLocationDao userLocationDao;

    public LocalDataSource(Application application) {
        final LocationDatabase database = LocationDatabase.getInstance(application);
        userLocationDao = database.userLocationDao();
    }

    @Override
    public void saveLocation(UserLocation userLocation) {
        userLocationDao.saveLocation(userLocation);
    }

    @Override
    public List<UserLocation> getAll() {
        return null;
    }
}
