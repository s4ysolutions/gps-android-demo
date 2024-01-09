package s4y.demo.mapsdksdemo.gps.dependencies

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate

interface IGPSUpdatesProvider : IGPSProvider {
    // locationUpdates parameters
    var intervalMillis: Long
    var maxUpdateDelayMillis: Long
    var maxUpdates: Int
    var minUpdateDistanceMeters: Float
    var minUpdateIntervalMillis: Long
    var waitForAccurateLocation: Boolean

    fun startUpdates()

    fun stopUpdates()

    interface IUpdates {
        fun asSharedFlow(): SharedFlow<GPSUpdate>
    }

    val updates: IUpdates

    enum class Status {
        IDLE,
        WARMING_UP,
        ACTIVE,
    }

    interface IStatus {
        fun asStateFlow(): StateFlow<Status>
        val isIdle: Boolean get() = asStateFlow().value == Status.IDLE
        val isWarmingUp: Boolean get() = asStateFlow().value == Status.WARMING_UP
        val isActive: Boolean get() = asStateFlow().value == Status.ACTIVE
    }

    val status: IStatus
}