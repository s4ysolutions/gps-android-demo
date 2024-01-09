package s4y.demo.mapsdksdemo.gps.filters

import s4y.demo.mapsdksdemo.gps.GPSUpdate

private const val DEFAULT_DT = 5.0 // seconds

abstract class GPSFilter(val defaultDt: Double = DEFAULT_DT) {
    abstract val name: String
    abstract fun apply(gpsUpdate: GPSUpdate): GPSUpdate?

    private val lockApplyAll = Any()

    open fun apply(gpsUpdates: Array<GPSUpdate>): Array<GPSUpdate> =
        synchronized(lockApplyAll) {
            val result = arrayOfNulls<GPSUpdate>(gpsUpdates.size)
            var head = 0
            for (gpsUpdate in gpsUpdates) {
                val filtered = apply(gpsUpdate)
                if (filtered != null) {
                    result[head++] = filtered
                }
            }
            @Suppress("UNCHECKED_CAST")
            return when (head) {
                0 -> emptyArray()
                result.size -> result
                else -> result.copyOfRange(0, head)
            } as Array<GPSUpdate>
        }


    abstract fun reset()
    abstract fun reset(gpsUpdate: GPSUpdate, dt: Double = defaultDt)
}