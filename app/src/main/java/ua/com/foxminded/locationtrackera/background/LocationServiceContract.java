package ua.com.foxminded.locationtrackera.background;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

public interface LocationServiceContract {

    interface Presenter {

        void onStart();

        @NonNull Completable saveUserLocation(UserLocation location);

        void onDestroy();

        Observable<Integer> getGpsStatusObservable();
    }
}
