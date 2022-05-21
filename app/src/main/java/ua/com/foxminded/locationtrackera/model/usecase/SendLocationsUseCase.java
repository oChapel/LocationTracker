package ua.com.foxminded.locationtrackera.model.usecase;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.util.Result;

public interface SendLocationsUseCase {

    Single<Result<Void>> execute();
}
