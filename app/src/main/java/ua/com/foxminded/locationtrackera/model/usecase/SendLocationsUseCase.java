package ua.com.foxminded.locationtrackera.model.usecase;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;

public class SendLocationsUseCase {

    public interface Listener {
        void onLocationsSent();

        void onSendingFailed();
    }

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SendLocationsUseCase.Listener listener;

    @Inject
    LocationRepository repository;

    public SendLocationsUseCase(SendLocationsUseCase.Listener listener) {
        this.listener = listener;
        App.getComponent().inject(this);
    }

    public void execute() {
        compositeDisposable.add(
                repository.sendLocations(repository.getAllLocations())
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.isSuccessful()) {
                                listener.onLocationsSent();
                            } else {
                                listener.onSendingFailed();
                            }
                        }, error -> {
                            error.printStackTrace();
                            listener.onSendingFailed();
                        })
        );
    }

    public void dispose() {
        compositeDisposable.dispose();
    }
}
