package s4y.demo.mapsdksdemo.gps.store

import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesSerializer

class GPSUpdatesSerializerCSV : IGPSUpdatesSerializer {
    companion object {
        const val separator = ","
        const val header = "latitude,longitude,velocity,accuracy,bearing,ts"
    }

    override fun serialize(gpsUpdates: Array<s4y.demo.mapsdksdemo.gps.GPSUpdate>): String {
        val sb = StringBuilder()
        sb.append(header)
        sb.append("\n")
        gpsUpdates.forEach {
            sb.append(it.latitude)
            sb.append(separator)
            sb.append(it.longitude)
            sb.append(separator)
            sb.append(it.velocity)
            sb.append(separator)
            sb.append(it.accuracy)
            sb.append(separator)
            sb.append(it.bearing)
            sb.append(separator)
            sb.append(it.ts)
            sb.append("\n")
        }
        return sb.toString()
    }
}