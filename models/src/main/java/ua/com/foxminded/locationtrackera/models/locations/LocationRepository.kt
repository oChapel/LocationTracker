package ua.com.foxminded.locationtrackera.models.locations

import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.util.Result

interface LocationRepository {

    fun saveLocation(userLocation: UserLocation)

    fun deleteLocationsFromDb()

    fun getAllLocations(): List<UserLocation>

    fun sendLocations(locationList: List<UserLocation>): Single<Result<Void?>>

    fun retrieveLocations(fromTime: Long, toTime: Long): Single<Result<List<UserLocation>>>
}
