package ua.com.foxminded.locationtrackera.model.locations.network;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.util.Result;

public interface LocationsNetwork {

    Single<Result<Void>> saveLocations(List<UserLocation> locationList);
}
