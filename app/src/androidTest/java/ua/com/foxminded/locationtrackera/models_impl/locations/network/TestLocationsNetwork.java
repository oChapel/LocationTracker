package ua.com.foxminded.locationtrackera.models_impl.locations.network;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.models.locations.network.LocationsNetwork;
import ua.com.foxminded.locationtrackera.models.locations.UserLocation;
import ua.com.foxminded.locationtrackera.models.util.Result;

public class TestLocationsNetwork implements LocationsNetwork {

    private final List<UserLocation> locationList = new ArrayList<>();

    @NotNull
    @Override
    public Single<Result<Void>> sendLocations(@NotNull List<UserLocation> locationList) {
        return Single.just(new Result.Error<>(null));
    }

    @NotNull
    @Override
    public Single<Result<List<UserLocation>>> retrieveLocations(long fromTime, long toTime) {
        addFakeLocations();

        final List<UserLocation> retrievedLocations = new ArrayList<>();
        for (UserLocation location : locationList) {
            if (location.getDate() >= fromTime && location.getDate() <= toTime) {
                retrievedLocations.add(location);
            }
        }
        return Single.just(new Result.Success<>(retrievedLocations));
    }

    private void addFakeLocations() {

        final Calendar calendar = Calendar.getInstance();
        calendar.set(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                1, 0, 0
        );
        final long startOfMonth = calendar.getTimeInMillis();

        if (locationList.isEmpty()) {
            // 1st currentMonth 00:00 - 2nd currentMonth 00:00 -> retrieved three locations
            locationList.add(new UserLocation(50.45001, 30.52333, startOfMonth)); //Kyiv
            locationList.add(new UserLocation(50.58321, 30.48566, startOfMonth + 40000000L)); //Vyshgorod
            locationList.add(new UserLocation(49.80939, 30.11209, startOfMonth + 80000000L)); //Bila Tserkva
        }
    }
}
