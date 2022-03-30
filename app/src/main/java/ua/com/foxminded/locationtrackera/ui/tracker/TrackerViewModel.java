package ua.com.foxminded.locationtrackera.ui.tracker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import ua.com.foxminded.locationtrackera.model.UserLocation;

public class TrackerViewModel extends AndroidViewModel {

    private static final int DEFAULT_UPDATE_INTERVAL = 60;
    private static final int FAST_UPDATE_INTERVAL = 10;

    private final MutableLiveData<Location> locationData = new MutableLiveData<>();

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public TrackerViewModel(Application app) {
        super(app);
    }

    public void setUpLocationServices() {
        locationRequest = LocationRequest.create()
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                final Location location = locationResult.getLastLocation();
                final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                saveUserLocation(geoPoint);
                locationData.setValue(location);
            }
        };
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        LocationServices
                .getFusedLocationProviderClient(getApplication())
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void saveUserLocation(GeoPoint geoPoint) {
        if (geoPoint != null) {
            final DocumentReference locationRef = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("User Locations")
                    .document();
            locationRef
                    .set(new UserLocation(geoPoint, null, locationRef.getId()));
        }
    }

    public LiveData<Location> getLocationData() {
        return locationData;
    }
}
