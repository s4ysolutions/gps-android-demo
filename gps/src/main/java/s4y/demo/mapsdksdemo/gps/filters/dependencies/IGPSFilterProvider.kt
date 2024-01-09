package s4y.demo.mapsdksdemo.gps.filters.dependencies

import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter

interface IGPSFilterProvider {
    var filter: GPSFilter
    fun asStateFlow(): StateFlow<GPSFilter>
}
