package s4y.demo.mapsdksdemo.gps.implementation

import com.google.android.gms.location.Priority
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider


internal val IGPSProvider.Priority.gmsPriority: Int
    get() = when (this) {
        IGPSProvider.Priority.HIGH_ACCURACY -> Priority.PRIORITY_HIGH_ACCURACY
        IGPSProvider.Priority.BALANCED_POWER_ACCURACY -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
        IGPSProvider.Priority.LOW_POWER -> Priority.PRIORITY_LOW_POWER
        IGPSProvider.Priority.PASSIVE -> Priority.PRIORITY_PASSIVE
    }
