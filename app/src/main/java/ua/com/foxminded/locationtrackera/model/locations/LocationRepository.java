package ua.com.foxminded.locationtrackera.model.locations;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.util.Result;

public interface LocationRepository {

    void saveLocation(UserLocation userLocation);

    void deleteLocationsFromDb();

    List<UserLocation> getAllLocations();

    Single<Result<Void>> sendLocations(List<UserLocation> locationList);

    Single<Result<List<UserLocation>>> retrieveLocations(double startDate, double endDate);
}
