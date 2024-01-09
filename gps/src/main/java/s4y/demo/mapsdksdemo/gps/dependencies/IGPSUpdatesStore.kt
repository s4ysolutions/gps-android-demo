package s4y.demo.mapsdksdemo.gps.dependencies

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter

interface IGPSUpdatesStore: MutableCollection<GPSUpdate> {
    var capacity: Int
    val updates: StateFlow<Array<GPSUpdate>>
    val lastUpdate: SharedFlow<GPSUpdate>
}