package ua.com.foxminded.locationtrackera.data;

import java.util.List;

import ua.com.foxminded.locationtrackera.data.source.LocationsDao;
import ua.com.foxminded.locationtrackera.data.source.LocationsNetwork;
import ua.com.foxminded.locationtrackera.model.auth.UserLocation;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;

public class LocationRepository implements LocationServiceContract.Repository {

    private final LocationsDao localDataSource;
    private final LocationsNetwork remoteDataSource;

    private List<UserLocation> locationList;

    public LocationRepository(LocationsDao localDataSource, LocationsNetwork remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    @Override
    public void saveLocation(UserLocation userLocation) {
        //dataSource.saveLocation(userLocation);
    }

    @Override
    public List<UserLocation> getAll() {
        return locationList;
    }
}
