package ua.com.foxminded.locationtrackera.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

@Dao
public interface UserLocationDao {

    @Insert
    void saveLocation(UserLocation location);

    @Query("SELECT * FROM userlocation")
    List<UserLocation> getAll();
}
