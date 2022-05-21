package ua.com.foxminded.locationtrackera.model.usecase;

import io.reactivex.rxjava3.core.Single;

import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.util.Result;

public class SendLocationsUseCaseImpl implements SendLocationsUseCase {

    private final LocationRepository repository;

    public SendLocationsUseCaseImpl(LocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Single<Result<Void>> execute() {
        return repository.sendLocations(repository.getAllLocations())
                .onErrorResumeNext(e -> Single.just(new Result.Error(e)));
    }
}
