package s4y.demo.mapsdksdemo.gps.dependencies

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.SharedFlow

interface IGPSUpdatesProvider: IGPSProvider {
    // locationUpdates parameters
    var intervalMillis: Long
    var maxUpdateDelayMillis: Long
    var maxUpdates: Int
    var minUpdateDistanceMeters: Float
    var minUpdateIntervalMillis: Long
    var waitForAccurateLocation: Boolean
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startUpdates(
        context: Context,
        looper: Looper? = null
    )

    fun stopUpdates()

    interface IUpdates {
        fun asSharedFlow(): SharedFlow<s4y.demo.mapsdksdemo.gps.GPSUpdate>
    }
    val updates: IUpdates
}