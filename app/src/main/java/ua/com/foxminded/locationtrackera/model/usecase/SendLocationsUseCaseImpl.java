package ua.com.foxminded.locationtrackera.model.usecase;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.util.Result;

public class SendLocationsUseCaseImpl implements SendLocationsUseCase {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LocationRepository repository;

    public SendLocationsUseCaseImpl(LocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Single<Result<Void>> execute() {
        final AtomicReference<Single<Result<Void>>> resultSingle = new AtomicReference<>();
        compositeDisposable.add(
                repository.sendLocations(repository.getAllLocations())
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> resultSingle.set(Single.just(result)), error -> {
                            error.printStackTrace();
                            resultSingle.set(Single.just(new Result.Error(error)));
                        })
        );
        return resultSingle.get();
    }

    @Override
    public void dispose() {
        compositeDisposable.dispose();
    }
}
