package ua.com.foxminded.locationtrackera.models.locations.dao

import ua.com.foxminded.locationtrackera.models.locations.UserLocation

interface LocationsDao {

    fun saveLocation(userLocation: UserLocation)

    fun getAllLocations(): List<UserLocation>

    fun deleteAllLocation()
}