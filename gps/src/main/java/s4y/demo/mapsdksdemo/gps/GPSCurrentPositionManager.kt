package s4y.demo.mapsdksdemo.gps

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSCurrentPositionProvider

class GPSCurrentPositionManager(private val currentGPSPositionProvider: IGPSCurrentPositionProvider) {
    fun requestCurrentPosition(): Flow<GPSUpdate> =
        currentGPSPositionProvider.request()

    val status = object : IGPSCurrentPositionProvider.IStatus {
        override fun asStateFlow(): StateFlow<Boolean> = currentGPSPositionProvider.status.asStateFlow()
    }
}