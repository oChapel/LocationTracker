package ua.com.foxminded.locationtrackera.background;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;

public class LocationService extends LifecycleService {

    private static final String CHANNEL_ID = "location_service_channel";
    private static final int NOTIFICATION_ID = 1;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;

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
        presenter.onStart(getApplicationContext());
        startForeground(NOTIFICATION_ID, getNotificationBuilder().build());
        setGpsStatusObserver();
    }

    private void setGpsStatusObserver() {
        compositeDisposable.add(
                presenter.getGpsStatusObservable().subscribe(status -> {
                    notification.setContentText(getString(R.string.gps_status_notification, getString(status)));
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                })
        );
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Channel_1", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_running))
                .setSmallIcon(R.drawable.icon);
        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        presenter.onDestroy();
    }
}
