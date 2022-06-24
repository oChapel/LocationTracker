package ua.com.foxminded.locationtrackera.models_impl.locations.dao

import android.app.Application
import ua.com.foxminded.locationtrackera.models.locations.UserLocation

class RoomLocationsDao(application: Application) :
    ua.com.foxminded.locationtrackera.models.locations.dao.LocationsDao {

    private val userLocationDao: UserLocationDao

    init {
        val database = LocationDatabase.getInstance(application)
        userLocationDao = database.userLocationDao()
    }

    override fun saveLocation(userLocation: UserLocation) =
        userLocationDao.saveLocation(userLocation)

    override fun getAllLocations(): List<UserLocation> = userLocationDao.getAllLocations()

    override fun deleteAllLocation() = userLocationDao.deleteAllLocation()
}