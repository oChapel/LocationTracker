package ua.com.foxminded.locationtrackera.model.locations;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.model.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.network.LocationsNetwork;
import ua.com.foxminded.locationtrackera.util.Result;

public class LocationRepositoryImpl implements LocationRepository {

    private final LocationsDao localDataSource;
    private final LocationsNetwork remoteDataSource;

    public LocationRepositoryImpl(LocationsDao localDataSource, LocationsNetwork remoteDataSource) {
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
    public Single<Result<Void>> sendLocations(List<UserLocation> locationList) {
        return remoteDataSource.sendLocations(locationList);
    }

    @Override
    public Single<Result<List<UserLocation>>> retrieveLocations(double startDate, double endDate) {
        return remoteDataSource.retrieveLocations(startDate, endDate);
    }
}
