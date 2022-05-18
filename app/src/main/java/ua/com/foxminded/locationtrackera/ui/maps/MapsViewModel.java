package ua.com.foxminded.locationtrackera.ui.maps;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.model.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

public class MapsViewModel extends MviViewModel<MapsScreenState, MapsScreenEffect>
        implements MapsContract.ViewModel {

    private static final double DEFAULT_LOCATIONS_RETRIEVING_TIME_HOURS = 12;

    private final AuthNetwork authNetwork;
    private final LocationRepository repository;
    private final PublishSubject<Pair<Double, Double>> locationsSupplier = PublishSubject.create();

    public MapsViewModel(AuthNetwork authNetwork, LocationRepository repository) {
        this.authNetwork = authNetwork;
        this.repository = repository;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        if (event == Lifecycle.Event.ON_CREATE) {
            setUpMapsChain();
        }
    }

    private void setUpMapsChain() {
        addTillDestroy(
                locationsSupplier.observeOn(Schedulers.io())
                        .flatMapSingle(pair -> {
                            if (pair.first < pair.second) {
                                return repository.retrieveLocations(pair.first, pair.second);
                            } else {
                                return Single.just(new Result.Error(new IllegalArgumentException("Start date must be anterior to end date")));
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                                    if (result.isSuccessful()) {
                                        final List<UserLocation> list = ((Result.Success<List<UserLocation>>) result).getData();
                                        if (list.isEmpty()) {
                                            setAction(new MapsScreenEffect.ShowToast(R.string.no_locations_in_current_period));
                                        } else {
                                            setAction(new MapsScreenEffect.PlaceMarkers(list));
                                        }
                                    } else {
                                        if (((Result.Error) result).getError() instanceof IllegalArgumentException) {
                                            setAction(new MapsScreenEffect.ShowToast(R.string.invalid_time_period));
                                        } else {
                                            setAction(new MapsScreenEffect.ShowToast(R.string.retrieve_failed));
                                        }
                                    }
                                }
                        )
        );
    }

    @Override
    public void retrieveLocationsByDate(double startDate, double endDate) {
        locationsSupplier.onNext(new Pair<>(startDate, endDate));
    }

    @Override
    public void retrieveDefaultLocations() {
        retrieveLocationsByDate(
                System.currentTimeMillis() - DEFAULT_LOCATIONS_RETRIEVING_TIME_HOURS * 3600,
                System.currentTimeMillis()
        );
    }

    @Override
    public void logout() {
        authNetwork.logout();
        setAction(new MapsScreenEffect.Logout());
    }
}
