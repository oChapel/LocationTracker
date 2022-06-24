package ua.com.foxminded.locationtrackera.models.usecase

import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.util.Result

interface SendLocationsUseCase {
    fun execute(): Single<Result<Void?>>
}