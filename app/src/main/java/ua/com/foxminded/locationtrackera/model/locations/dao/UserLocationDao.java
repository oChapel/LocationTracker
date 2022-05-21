package ua.com.foxminded.locationtrackera.model.locations.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

@Dao
public interface UserLocationDao {

    @Insert
    void saveLocation(UserLocation location);

    @Query("SELECT * FROM userlocation")
    List<UserLocation> getAllLocations();

    @Query("DELETE FROM userlocation")
    void deleteAllLocation();
}
