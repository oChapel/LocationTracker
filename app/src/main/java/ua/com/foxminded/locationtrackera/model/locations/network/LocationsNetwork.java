package ua.com.foxminded.locationtrackera.model.locations.network;

import androidx.core.util.Pair;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.util.Result;

public interface LocationsNetwork {

    Single<Result<Void>> sendLocations(List<UserLocation> locationList);

    Single<Result<?>> retrieveLocations(double startDate, double endDate);
}
