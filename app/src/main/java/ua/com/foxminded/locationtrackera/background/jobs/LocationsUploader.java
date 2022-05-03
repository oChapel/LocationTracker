package ua.com.foxminded.locationtrackera.background.jobs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCase;

public class LocationsUploader extends Worker implements SendLocationsUseCase.Listener {

    private final SendLocationsUseCase sendLocationsUseCase = new SendLocationsUseCase(this);
    private final AtomicReference<Result> workResult = new AtomicReference<>();

    @Inject
    LocationRepository repository;

    public LocationsUploader(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        App.getComponent().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendLocationsUseCase.execute();
        return workResult.get();
    }

    @Override
    public void onLocationsSent() {
        workResult.set(Result.success());
        repository.deleteLocationsFromDb();
    }

    @Override
    public void onSendingFailed() {
        workResult.set(Result.failure());
    }

    @Override
    public void onStopped() {
        super.onStopped();
        sendLocationsUseCase.dispose();
    }
}
