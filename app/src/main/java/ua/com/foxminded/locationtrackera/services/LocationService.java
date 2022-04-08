package ua.com.foxminded.locationtrackera.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.data.model.UserLocation;

public class LocationService extends Service {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 10;
    private static final String CHANNEL_ID = "location_service_channel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            final NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Channel_1", NotificationManager.IMPORTANCE_DEFAULT);
            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.service_running))
                    .build();

            startForeground(1, builder);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_NOT_STICKY;
    }

    private void setUpServices() {
        locationRequest = LocationRequest.create()
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //final Location location = locationResult.getLastLocation();
                //final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                //saveUserLocation(geoPoint);
                saveUserLocation(locationResult.getLastLocation());
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            return;
        }
        setUpServices();
        fusedLocationClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void saveUserLocation(Location location) {
        if (location != null) {
            try {
                final DocumentReference locationRef = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("User Locations")
                        .document();
                locationRef
                        //.set(new UserLocation(geoPoint, null, locationRef.getId()));
                        .set(new UserLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                Calendar.getInstance().getTimeInMillis()
                        ));
            } catch (NullPointerException e) {
                stopSelf();
            }
        }
    }
}
