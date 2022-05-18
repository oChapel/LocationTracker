package ua.com.foxminded.locationtrackera.background;

import android.content.Context;
import android.location.Location;

import io.reactivex.rxjava3.core.Observable;

public interface LocationServiceContract {

    interface Presenter {

        void onStart(Context context);

        boolean saveUserLocation(Location location);

        void onDestroy();

        Observable<Integer> getGpsStatusObservable();
    }
}
