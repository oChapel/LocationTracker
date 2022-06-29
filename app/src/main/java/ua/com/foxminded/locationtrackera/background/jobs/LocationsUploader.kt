package ua.com.foxminded.locationtrackera.background.jobs

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class LocationsUploader(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var repository: LocationRepository

    @Inject
    lateinit var sendLocationsUseCase: SendLocationsUseCase

    init {
        App.component.inject(this)
    }

    override fun doWork(): Result {
        val workResult = AtomicReference<Result>()
        compositeDisposable.add(
            sendLocationsUseCase.execute()
                .subscribe { result ->
                    if (result.isSuccessful) {
                        repository.deleteLocationsFromDb()
                        workResult.set(Result.success())
                    } else {
                        workResult.set(Result.failure())
                    }
                }
        )
        return workResult.get()
    }

    override fun onStopped() {
        super.onStopped()
        compositeDisposable.dispose()
    }
}
