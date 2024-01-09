package s4y.demo.mapsdksdemo.gps.dependencies

import kotlinx.coroutines.flow.StateFlow

interface IGPSProvider {
    enum class Priority {
        HIGH_ACCURACY,
        BALANCED_POWER_ACCURACY,
        LOW_POWER,
        PASSIVE, ;
        internal val gmsPriority: Int
            get() = when (this) {
                HIGH_ACCURACY -> com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
                BALANCED_POWER_ACCURACY -> com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
                LOW_POWER -> com.google.android.gms.location.Priority.PRIORITY_LOW_POWER
                PASSIVE -> com.google.android.gms.location.Priority.PRIORITY_PASSIVE
            }
    }

    enum class Granularity {
        PERMISSION_LEVEL,
        FINE,
        COARSE, ;
        internal val gmsGranularity: Int
            get() = when (this) {
                PERMISSION_LEVEL -> com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL
                FINE -> com.google.android.gms.location.Granularity.GRANULARITY_FINE
                COARSE -> com.google.android.gms.location.Granularity.GRANULARITY_COARSE
            }
    }

    interface IStatus{
        fun asStateFlow(): StateFlow<Boolean>
    }
    val status: IStatus

    var durationMillis: Long
    var granularity: Granularity
    var priority: Priority
    var maxUpdateAgeMillis: Long
}
