package s4y.demo.mapsdksdemo.gps.filters
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import kotlin.math.abs

class GPSFilterProximity(val proximity: Units.Proximity) : GPSFilter() {
    private var proximityDegrees = proximity.degrees

    companion object {
        val instance5m = GPSFilterProximity(Units.Proximity(5.0f))
        val instance1m = GPSFilterProximity(Units.Proximity(1.0f))
        @Suppress("unused")
        val instance05m = GPSFilterProximity(Units.Proximity(0.5f))
    }

    private var initialized = false
    private var prevLat: Double = 0.0
    private var prevLong: Double = 0.0
    override val name: String = "Skip points closer than ${proximity.meters} meters"
    override fun apply(gpsUpdate: GPSUpdate): GPSUpdate? {
        return if (initialized) {
            if (abs(gpsUpdate.latitude - prevLat) > proximityDegrees ||
                abs(gpsUpdate.longitude - prevLong) > proximityDegrees
            ) {
                prevLat = gpsUpdate.latitude
                prevLong = gpsUpdate.longitude
                gpsUpdate
            } else null
        } else {
            initialized = true
            prevLat = gpsUpdate.latitude
            prevLong = gpsUpdate.longitude
            gpsUpdate
        }
    }

    override fun reset(){
        initialized = false
        prevLat = 0.0
        prevLong = 0.0
    }
}