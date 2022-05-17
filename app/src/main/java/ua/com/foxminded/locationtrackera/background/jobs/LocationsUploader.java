package ua.com.foxminded.locationtrackera.background.jobs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;

public class LocationsUploader extends Worker {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    LocationRepository repository;

    @Inject
    SendLocationsUseCase sendLocationsUseCase;

    public LocationsUploader(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        App.getComponent().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        final AtomicReference<Result> workResult = new AtomicReference<>();
        compositeDisposable.add(
                sendLocationsUseCase.execute()
                        .subscribe(result -> {
                            if (result.isSuccessful()) {
                                repository.deleteLocationsFromDb();
                                workResult.set(Result.success());
                            } else {
                                workResult.set(Result.failure());
                            }
                        })
        );
        return workResult.get();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        compositeDisposable.dispose();
    }
}
