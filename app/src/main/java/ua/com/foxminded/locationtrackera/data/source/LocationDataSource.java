package ua.com.foxminded.locationtrackera.data.source;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public interface LocationDataSource {

    void saveLocation(UserLocation userLocation);

    List<UserLocation> getAll();
}
