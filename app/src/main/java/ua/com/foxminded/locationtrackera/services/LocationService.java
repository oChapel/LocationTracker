package ua.com.foxminded.locationtrackera.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import javax.inject.Inject;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;

public class LocationService extends LifecycleService {

    private static final String CHANNEL_ID = "location_service_channel";

    @Inject
    LocationServiceContract.Presenter presenter;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().inject(this);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        presenter.onStart();
        startForeground(1, buildNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Channel_1", NotificationManager.IMPORTANCE_DEFAULT);
            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_running))
                .setSmallIcon(R.drawable.icon)
                .build();
    }
}
