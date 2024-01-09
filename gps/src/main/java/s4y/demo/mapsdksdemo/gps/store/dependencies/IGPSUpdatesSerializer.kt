package s4y.demo.mapsdksdemo.gps.store.dependencies

import s4y.demo.mapsdksdemo.gps.GPSUpdate

interface IGPSUpdatesSerializer {
    fun serialize(gpsUpdate: Array<s4y.demo.mapsdksdemo.gps.GPSUpdate>): String
}