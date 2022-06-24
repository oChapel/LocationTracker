package ua.com.foxminded.locationtrackera.models_impl.locations.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ua.com.foxminded.locationtrackera.models.locations.UserLocation

@Dao
interface UserLocationDao {
    @Insert
    fun saveLocation(location: UserLocation)

    @Query("SELECT * FROM UserLocation")
    fun getAllLocations(): List<UserLocation>

    @Query("DELETE FROM UserLocation")
    fun deleteAllLocation()
}