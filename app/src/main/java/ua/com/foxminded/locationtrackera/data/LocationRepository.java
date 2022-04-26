package ua.com.foxminded.locationtrackera.data;

import java.util.List;

import ua.com.foxminded.locationtrackera.data.source.LocationDataSource;
import ua.com.foxminded.locationtrackera.model.auth.UserLocation;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;

public class LocationRepository implements LocationServiceContract.Repository {

    private final LocationDataSource localDataSource;
    private final LocationDataSource remoteDataSource;

    private List<UserLocation> locationList;

    public LocationRepository(LocationDataSource localDataSource, LocationDataSource remoteDataSource) {
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
