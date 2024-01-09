package s4y.demo.mapsdksdemo.gps.store.dependencies

import kotlinx.coroutines.flow.SharedFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate

interface IGPSUpdatesStore: MutableCollection<s4y.demo.mapsdksdemo.gps.GPSUpdate> {
    var capacity: Int
    val snapshot: Array<s4y.demo.mapsdksdemo.gps.GPSUpdate>
    val lastUpdate: SharedFlow<s4y.demo.mapsdksdemo.gps.GPSUpdate>
    fun saveAsFile(): String
}