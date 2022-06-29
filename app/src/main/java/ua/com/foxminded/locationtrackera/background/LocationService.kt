package ua.com.foxminded.locationtrackera.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.background.LocationServiceContract.Presenter
import ua.com.foxminded.locationtrackera.ui.TrackerActivity
import javax.inject.Inject

class LocationService : LifecycleService() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var presenter: Presenter

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        presenter.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, notificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        }
        setGpsStatusObserver()
        return START_STICKY
    }

    private fun setGpsStatusObserver() {
        val notificationManager = NotificationManagerCompat.from(this)
        compositeDisposable.add(
            presenter.gpsStatusObservable.subscribe { status: Int ->
                if (status != 0) {
                    notification.setContentText(
                        getString(R.string.gps_status_notification, getString(status))
                    )
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }
            }
        )
    }

    private val notificationBuilder: NotificationCompat.Builder
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, "Channel_1", NotificationManager.IMPORTANCE_DEFAULT
                )
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }

            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_running))
                .setSmallIcon(R.drawable.ic_location_icon)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)

            return notification
        }

    private val pendingIntent: PendingIntent
        get() {
            val notifyIntent = Intent(this, TrackerActivity::class.java)
            notifyIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val flags: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getActivity(this, 0, notifyIntent, flags)
        }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        presenter.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 1

        @JvmStatic
        fun getIntent(context: Context): Intent = Intent(context, LocationService::class.java)
    }
}
