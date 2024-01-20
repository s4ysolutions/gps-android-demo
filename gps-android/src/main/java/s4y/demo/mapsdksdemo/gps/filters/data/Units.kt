package s4y.demo.mapsdksdemo.gps.filters.data

import org.apache.commons.math3.util.FastMath
import s4y.demo.mapsdksdemo.gps.GPSUpdate

sealed class Units {
    internal companion object {
        const val mPerDegree = 111139.0f // meters per degree
        val Float.latDg: Float get() = this / mPerDegree
        val Double.latDg: Double get() = this / mPerDegree
        fun Float.longDg(cosLatitude: Double): Float = (this.latDg / cosLatitude).toFloat()
        fun Double.longDg(cosLatitude: Double): Double = this.latDg / cosLatitude

        // conversions degrees to meters
        val Float.latMeters: Float get() = this * mPerDegree
        val Double.latMeters: Double get() = this * mPerDegree

        @Suppress("unused", "unused")
        fun Float.longMeters(cosLatitude: Double): Float =
            (this.latMeters * cosLatitude).toFloat()

        @Suppress("unused", "unused")
        fun Double.longitudeMeters(cosLatitude: Double): Double =
            this.latMeters * cosLatitude

        // extract data from state array
    }


    class Acceleration(mPerSec2: Double) : Units() {
        val mPerSec2 = mPerSec2.toFloat()
    }

    class Accuracy private constructor(val gpsAccuracy: Float) {
        constructor(gpsUpdate: GPSUpdate): this(gpsUpdate.accuracy)
        interface XYVariances {
            val accuracy: Float
            val varianceXX: Double
            val varianceYY: Double
            val varianceXY: Double
            val varianceYX: Double
        }
        val meters = object: XYVariances{
            override val accuracy: Float = gpsAccuracy
            private val variance: Double = (gpsAccuracy * gpsAccuracy).toDouble()
            override val varianceXX: Double = variance
            override val varianceYY: Double = variance
            override val varianceXY: Double = variance
            override val varianceYX: Double = variance
        }
    }
    class Bearing(val degree: Float) {
        constructor(degree: Double): this(degree.toFloat())
        val radian: Float = FastMath.toRadians(degree.toDouble()).toFloat()
    }
    class Longitude(longitude: Double, cosLatitude: Double) {
        val degree: Double = longitude
        val meters: Double

        init {
            meters = longitude.longitudeMeters(cosLatitude)
        }
    }

    class Latitude(latitude: Double) : Units() {
        val degree = latitude
        val meters = latitude.latMeters
        val cos by lazy { FastMath.cos(Math.toRadians(latitude)) }
    }

    class Velocity(val mPerSec: Float) {
        constructor(mPerSec: Double): this(mPerSec.toFloat())
    }

}