package ua.com.foxminded.locationtrackera.models_impl.locations

import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.models.locations.dao.LocationsDao
import ua.com.foxminded.locationtrackera.models.locations.network.LocationsNetwork
import ua.com.foxminded.locationtrackera.models.util.Result

class LocationRepositoryImpl(
    private val localDataSource: LocationsDao,
    private val remoteDataSource: LocationsNetwork
) : LocationRepository {

    override fun saveLocation(userLocation: UserLocation) = localDataSource.saveLocation(userLocation)

    override fun deleteLocationsFromDb() = localDataSource.deleteAllLocation()

    override fun getAllLocations(): List<UserLocation> = localDataSource.getAllLocations()

    override fun sendLocations(
        locationList: List<UserLocation>
    ): Single<Result<Void?>> = remoteDataSource.sendLocations(locationList)

    override fun retrieveLocations(
        fromTime: Long, toTime: Long
    ): Single<Result<List<UserLocation>>> = remoteDataSource.retrieveLocations(fromTime, toTime)
}
