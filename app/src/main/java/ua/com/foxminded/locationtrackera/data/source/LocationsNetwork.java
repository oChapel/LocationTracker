package ua.com.foxminded.locationtrackera.data.source;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.data.UserLocation;
import ua.com.foxminded.locationtrackera.util.Result;

public interface LocationsNetwork {

    Single<Result<Void>> saveLocations(List<UserLocation> locationList);
}
