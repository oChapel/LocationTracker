package ua.com.foxminded.locationtrackera.model.locations;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.background.LocationServiceContract;
import ua.com.foxminded.locationtrackera.model.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.network.LocationsNetwork;
import ua.com.foxminded.locationtrackera.util.Result;

public class LocationRepository implements LocationServiceContract.Repository {

    private final LocationsDao localDataSource;
    private final LocationsNetwork remoteDataSource;

    public LocationRepository(LocationsDao localDataSource, LocationsNetwork remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    @Override
    public void saveLocation(UserLocation userLocation) {
        localDataSource.saveLocation(userLocation);
    }

    @Override
    public List<UserLocation> getAllLocations() {
        return localDataSource.getAllLocations();
    }

    @Override
    public void deleteLocationsFromDb() {
        localDataSource.deleteAllLocation();
    }

    @Override
    public Single<Result<Void>> saveLocationsToNetwork(List<UserLocation> locationList) {
        return remoteDataSource.saveLocations(locationList);
    }
}
