package s4y.demo.mapsdksdemo.gps.filters

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.RealMatrix
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import kotlin.math.abs

sealed class GPSFilter {
    abstract val name: String
    abstract fun apply(gpsUpdate: GPSUpdate): GPSUpdate?
    open fun apply(gpsUpdates: Array<GPSUpdate>): Array<GPSUpdate> {
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
    class Null : GPSFilter() {
        companion object {
            val instance = Null()
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
    }

    class Proximity(var proximity: Float) : GPSFilter() {
        companion object {
            val instance0 = Proximity(0.0f)
        }

        private var prev: GPSUpdate? = null
        override val name: String = "Skip close points"
        override fun apply(gpsUpdate: GPSUpdate): GPSUpdate? {
            val p = prev
            prev = gpsUpdate
            return if (p == null)
                gpsUpdate
            else if (abs(gpsUpdate.latitude - p.latitude) > proximity ||
                abs(gpsUpdate.longitude - p.longitude) > proximity
            )
                gpsUpdate
            else null
        }

        override fun reset() {
            prev = null
        }
    }

    class Kalman : GPSFilter() {
        companion object {
            val instance = Kalman()
        }

        // A - state transition matrix
        var A: RealMatrix = Array2DRowRealMatrix(doubleArrayOf(1.0))

        override val name: String = "Kalman"

        override fun apply(gpsUpdate: GPSUpdate): GPSUpdate? {
            TODO("Not yet implemented")
        }

        override fun reset() {
            TODO("Not yet implemented")
        }
    }
}