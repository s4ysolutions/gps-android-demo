package s4y.demo.mapsdksdemo.gps.implementation

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSCurrentPositionProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider

class AndroidGPSCurrentPositionProvider :
    s4y.demo.mapsdksdemo.gps.dependencies.IGPSCurrentPositionProvider {

    // both requestCurrentLocation and locationUpdates parameters
    override var granularity: s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider.Granularity =
        s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider.Granularity.PERMISSION_LEVEL
    override var maxUpdateAgeMillis: Long = 5000L
    override var priority: s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider.Priority =
        s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider.Priority.HIGH_ACCURACY

    // requestCurrentLocation parameters
    override var durationMillis: Long = Long.MAX_VALUE // infinity

    private var currentLocationTask: Task<Location>? = null
    private val currentLocationTaskLock = Any()

    val _status = MutableStateFlow(false)
    override val status = object : s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider.IStatus {
        override fun asStateFlow() = _status.asStateFlow()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun request(
        context: Context,
        cancellationToken: CancellationToken?
    ): Flow<s4y.demo.mapsdksdemo.gps.GPSUpdate> = callbackFlow {
        status.asStateFlow()
        synchronized(currentLocationTaskLock) {
            val task = currentLocationTask ?: run {
                _status.value = true
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val currentLocationRequest = CurrentLocationRequest.Builder()
                    .setDurationMillis(durationMillis)
                    .setGranularity(granularity.gmsGranularity)
                    .setMaxUpdateAgeMillis(maxUpdateAgeMillis)
                    .setPriority(priority.gmsPriority)
                    .build()
                val task = fusedLocationClient.getCurrentLocation(
                    currentLocationRequest,
                    cancellationToken
                )
                currentLocationTask = task
                task
            }

            task.addOnCompleteListener {
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null) {
                        trySend(s4y.demo.mapsdksdemo.gps.GPSUpdate(task.result))
                        close()
                    } else {
                        cancel("No location available", Exception("No location available"))
                    }
                } else {
                    cancel("No location available", task.exception)
                }
            }

        }
        awaitClose {
            synchronized(currentLocationTaskLock) {
                if (currentLocationTask != null) {
                    _status.value = false
                    currentLocationTask = null
                }
            }
        }
    }
}