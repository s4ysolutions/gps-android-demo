package s4y.demo.mapsdksdemo.gps.dependencies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate

interface IGPSCurrentPositionProvider : IGPSProvider {
    interface IStatus {
        fun asStateFlow(): StateFlow<Boolean>
    }

    val status: IStatus

    fun cancel()
    fun request(): Flow<GPSUpdate>
}