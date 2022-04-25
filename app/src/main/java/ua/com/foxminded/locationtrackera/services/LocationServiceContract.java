package ua.com.foxminded.locationtrackera.services;

import android.location.Location;
import android.location.LocationListener;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public interface LocationServiceContract {

    interface ServiceInteractor {
        void showAlertToast(int stringId);
        void stopService();
    }

    interface Presenter {
        void init();
        void saveUserLocation(Location location);
        void onDestroy();
    }

    interface GpsServices {
        void setUpServices();
        void startLocationUpdates();
        void registerGpsOrGnssStatusChanges();
        void setStatusListener(StatusListener listener);
        void setLocationListener(LocationListener listener);
        void onDestroy();
    }

    interface Repository {
        void saveLocation(UserLocation userLocation);
        List<UserLocation> getAll();
    }

    interface Cache {
        void serviceStatusChanged(boolean isRunning);
        void setStatusListener(StatusListener listener);
        void setGpsStatus(int status);
    }
}
