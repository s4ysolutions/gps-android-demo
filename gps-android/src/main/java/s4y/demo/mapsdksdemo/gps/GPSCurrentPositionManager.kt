package s4y.demo.mapsdksdemo.gps

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSCurrentPositionProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider

class GPSCurrentPositionManager(private val currentGPSPositionProvider: IGPSCurrentPositionProvider) {
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestCurrentPosition(context: Context): Flow<GPSUpdate> =
        currentGPSPositionProvider.request(context)

    val status = object : IGPSProvider.IStatus {
        override fun asStateFlow(): StateFlow<Boolean> = currentGPSPositionProvider.status.asStateFlow()
    }
}