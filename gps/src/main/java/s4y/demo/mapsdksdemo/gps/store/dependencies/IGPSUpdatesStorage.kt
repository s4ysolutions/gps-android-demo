package s4y.demo.mapsdksdemo.gps.store.dependencies
interface IGPSUpdatesStorage {
    fun save(gpsUpdates: Array<s4y.demo.mapsdksdemo.gps.GPSUpdate>, serializer: IGPSUpdatesSerializer): String
}