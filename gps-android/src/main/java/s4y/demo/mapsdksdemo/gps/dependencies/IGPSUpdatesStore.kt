package s4y.demo.mapsdksdemo.gps.dependencies

import kotlinx.coroutines.flow.SharedFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate

interface IGPSUpdatesStore: MutableCollection<GPSUpdate> {
    var capacity: Int
    val updates: Array<GPSUpdate>
    val lastUpdate: SharedFlow<GPSUpdate>
}