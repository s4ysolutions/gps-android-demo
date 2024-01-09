package s4y.demo.mapsdksdemo.gps.implementation

import android.Manifest
import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider

class FusedGPSUpdatesProvider(private val context: Context, private val looper: Looper? = null) : IGPSUpdatesProvider {
    override var granularity: IGPSProvider.Granularity =
        IGPSProvider.Granularity.PERMISSION_LEVEL
    override var priority: IGPSProvider.Priority =
        IGPSProvider.Priority.HIGH_ACCURACY
    override var maxUpdateAgeMillis: Long = 5000L
    override var durationMillis: Long = Long.MAX_VALUE // infinity
    override var intervalMillis: Long = 0L // ASAP
    override var maxUpdateDelayMillis: Long = 0L // no batching
    override var maxUpdates: Int = Integer.MAX_VALUE // infinity
    override var minUpdateDistanceMeters: Float = 0f // update even if the user did not move
    override var minUpdateIntervalMillis: Long = -1L // TODO: double check
    override var waitForAccurateLocation: Boolean = false

    private val _status = MutableStateFlow(IGPSUpdatesProvider.Status.IDLE)
    override val status = object : IGPSUpdatesProvider.IStatus {
        override fun asStateFlow() = _status.asStateFlow()
    }

    private val _updates = MutableSharedFlow<s4y.demo.mapsdksdemo.gps.GPSUpdate>(1,0,BufferOverflow.DROP_OLDEST)
    override val updates = object : IGPSUpdatesProvider.IUpdates {
        override fun asSharedFlow(): SharedFlow<s4y.demo.mapsdksdemo.gps.GPSUpdate> = _updates
    }

    private var client: FusedLocationProviderClient? = null
    private val clientLock = Any()

    private val locationUpdatesListener =
        LocationListener { location ->
            if (_status.value == IGPSUpdatesProvider.Status.WARMING_UP) {
                _status.value = IGPSUpdatesProvider.Status.ACTIVE
            }
            val update = location.toGPSUpdate()
            _updates.tryEmit(update)
        }

    private var gpsHandlerThread: HandlerThread? = null
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startUpdates() {
        stopUpdates()
        synchronized(clientLock) {
            val actualLooper = looper ?: run {
                gpsHandlerThread = HandlerThread("LocationUpdatesThread").apply {
                    start()
                }
                gpsHandlerThread!!.looper
            }
            // first cancel previous updates
            val locationUpdatesRequest = LocationRequest.Builder(intervalMillis)
                .setDurationMillis(durationMillis)
                .setGranularity(granularity.gmsGranularity)
                .setMaxUpdateAgeMillis(maxUpdateAgeMillis)
                .setMaxUpdateDelayMillis(maxUpdateDelayMillis)
                .setMaxUpdates(maxUpdates)
                .setMinUpdateDistanceMeters(minUpdateDistanceMeters)
                // TODO:
                // .setMinUpdateIntervalMillis(minUpdateIntervalMillis)
                .setPriority(priority.gmsPriority)
                // .setWaitForAccurateLocation(waitForAccurateLocation)
                .build()

            // i want to recreate it in order to do not keep reference to the context
            client = LocationServices.getFusedLocationProviderClient(context).apply {
                requestLocationUpdates(locationUpdatesRequest, locationUpdatesListener, actualLooper)
                _status.value = IGPSUpdatesProvider.Status.WARMING_UP
            }
        }
    }

    override fun stopUpdates() = synchronized(clientLock) {
        gpsHandlerThread?.quitSafely()
        client?.removeLocationUpdates(locationUpdatesListener)
        _status.value = IGPSUpdatesProvider.Status.IDLE
    }

}