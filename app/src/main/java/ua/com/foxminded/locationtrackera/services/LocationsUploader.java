package ua.com.foxminded.locationtrackera.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.com.foxminded.locationtrackera.App;

public class LocationsUploader extends Worker {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    LocationServiceContract.Repository repository;

    public LocationsUploader(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        App.getComponent().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        final AtomicReference<Result> workResult = new AtomicReference<>();
        compositeDisposable.add(
                repository.saveLocationsToNetwork(repository.getAllLocations())
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                                    if (result.isSuccessful()) {
                                        workResult.set(Result.success());
                                        repository.deleteLocationsFromDb();
                                    } else {
                                        workResult.set(Result.failure());
                                    }
                                }, error -> {
                                    error.printStackTrace();
                                    workResult.set(Result.failure());
                                }
                        )
        );
        return workResult.get();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        compositeDisposable.dispose();
    }
}
