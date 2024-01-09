package s4y.demo.mapsdksdemo.gps.filters

import s4y.demo.mapsdksdemo.gps.GPSUpdate

class GPSFilterNull : GPSFilter() {
    companion object {
        val instance = GPSFilterNull()
    }

    override val name: String = "No filtering"

    override fun apply(gpsUpdate: GPSUpdate): GPSUpdate {
        return gpsUpdate
    }

    override fun apply(gpsUpdates: Array<GPSUpdate>): Array<GPSUpdate> {
        return gpsUpdates
    }

    override fun reset() {
    }

    override fun reset(gpsUpdate: GPSUpdate, dt: Double) {
    }
}