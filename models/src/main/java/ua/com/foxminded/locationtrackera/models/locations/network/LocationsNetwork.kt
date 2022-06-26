package ua.com.foxminded.locationtrackera.models.locations.network

import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.models.util.Result

interface LocationsNetwork {

    fun sendLocations(locationList: List<UserLocation>): Single<Result<Void?>>

    fun retrieveLocations(fromTime: Long, toTime: Long): Single<Result<List<UserLocation>>>
}
