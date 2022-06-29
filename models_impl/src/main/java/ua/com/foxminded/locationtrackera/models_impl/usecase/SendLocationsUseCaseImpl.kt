package ua.com.foxminded.locationtrackera.models_impl.usecase

import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import ua.com.foxminded.locationtrackera.models.util.Result

class SendLocationsUseCaseImpl(private val repository: ua.com.foxminded.locationtrackera.models.locations.LocationRepository) :
    SendLocationsUseCase {

    override fun execute(): Single<Result<Void?>> =
        Single.fromCallable { repository.getAllLocations() }
            .flatMap { locationList: List<UserLocation> -> repository.sendLocations(locationList) }
            .onErrorResumeNext { e: Throwable? -> Single.just(Result.Error(e)) }
}
