package s4y.demo.mapsdksdemo.gps.implementation

import com.google.android.gms.location.Granularity
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider

internal val IGPSProvider.Granularity.gmsGranularity: Int
    get() = when (this) {
        IGPSProvider.Granularity.PERMISSION_LEVEL -> Granularity.GRANULARITY_PERMISSION_LEVEL
        IGPSProvider.Granularity.FINE -> Granularity.GRANULARITY_FINE
        IGPSProvider.Granularity.COARSE -> Granularity.GRANULARITY_COARSE
    }
