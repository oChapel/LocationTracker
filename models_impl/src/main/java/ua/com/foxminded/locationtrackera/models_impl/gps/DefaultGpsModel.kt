package ua.com.foxminded.locationtrackera.models_impl.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ua.com.foxminded.locationtrackera.models.gps.GpsSource
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.models_impl.BuildConfig
import java.util.*

class DefaultGpsModel(private val context: Context) : GpsSource {

    private val gpsStatusSupplier = BehaviorSubject.create<Int>()
    private val locationSupplier = BehaviorSubject.create<UserLocation>()
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (!serviceRunningFlag
                || !locationManager.isProviderEnabled(BuildConfig.LOCATION_PROVIDER)
            ) {
                return
            }
            val loc = locationResult.lastLocation
            if (loc != null) {
                locationSupplier.onNext(
                    UserLocation(
                        loc.latitude, loc.longitude,
                        Calendar.getInstance().timeInMillis
                    )
                )
            }
        }
    }
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest =
        LocationRequest.create()
            .setInterval((1000 * BuildConfig.UPDATE_INTERVAL).toLong())
            .setSmallestDisplacement(BuildConfig.TRACKING_SENSIVITY.toFloat())
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private lateinit var gnssStatusCallback: GnssStatus.Callback
    private lateinit var gpsStatusListener: GpsStatus.Listener

    private var serviceRunningFlag = false
    private var locationUpdatesFlag = false

    override fun onServiceStarted() {
        serviceRunningFlag = true
        if (!locationUpdatesFlag) {
            startLocationUpdates()
            registerGpsOrGnssStatusChanges()
        }
    }

    override fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationUpdatesFlag = true
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    override fun registerGpsOrGnssStatusChanges() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!locationManager.isProviderEnabled("gps")) {
                gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                gnssStatusCallback = object : GnssStatus.Callback() {
                    override fun onStarted() {
                        gpsStatusSupplier.onNext(GpsStatusConstants.ACTIVE)
                    }

                    override fun onFirstFix(ttffMillis: Int) {
                        gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED)
                    }

                    override fun onStopped() {
                        gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED)
                    }
                }
                locationManager.registerGnssStatusCallback(gnssStatusCallback, null)
            } else {
                gpsStatusListener = GpsStatus.Listener { event: Int ->
                    when (event) {
                        GpsStatus.GPS_EVENT_STARTED -> gpsStatusSupplier.onNext(GpsStatusConstants.ACTIVE)
                        GpsStatus.GPS_EVENT_FIRST_FIX -> gpsStatusSupplier.onNext(GpsStatusConstants.FIX_ACQUIRED)
                        GpsStatus.GPS_EVENT_STOPPED -> gpsStatusSupplier.onNext(GpsStatusConstants.FIX_NOT_ACQUIRED)
                    }
                }
                locationManager.addGpsStatusListener(gpsStatusListener)
            }
        } else {
            gpsStatusSupplier.onNext(GpsStatusConstants.NO_PERMISSION)
        }
    }

    override val gpsStatusObservable: Observable<Int>
        get() = gpsStatusSupplier
    override val locationObservable: Observable<UserLocation>
        get() = locationSupplier

    override fun onServiceStopped() {
        serviceRunningFlag = false
    }

    override fun onDestroy() {
        locationUpdatesFlag = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
        } else {
            locationManager.removeGpsStatusListener(gpsStatusListener)
        }
    }
}