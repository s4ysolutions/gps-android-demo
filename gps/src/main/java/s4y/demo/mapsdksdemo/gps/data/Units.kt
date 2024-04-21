package s4y.demo.mapsdksdemo.gps.data

import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

sealed class Units {
    internal companion object {
        const val mPer1DegreeEq = 111319.3 // meters per degree
        const val mPer1DegreePolar = 111412.8 // meters per degree
        const val dPer5Meter = 0.00005 // roughly 5m
        const val dPer1Meter = 0.00001 // roughly 1m
        const val dPerHalfMeter = 0.000005 // roughly 0.5m
    }


    @Suppress("unused")
    class Acceleration(mPerSec2: Double) : Units() {
        val mPerSec2 = mPerSec2.toFloat()
    }

    class Accuracy (val gpsAccuracy: Float) {
        constructor(gpsUpdate: s4y.demo.mapsdksdemo.gps.GPSUpdate) : this(gpsUpdate.accuracy)

        interface XYVariances {
            val accuracy: Float
            val varianceXX: Double
            val varianceYY: Double
            val varianceXY: Double
            val varianceYX: Double
        }

        val meters = object : XYVariances {
            override val accuracy: Float = gpsAccuracy
            private val variance: Double = (gpsAccuracy * gpsAccuracy).toDouble()
            override val varianceXX: Double = variance
            override val varianceYY: Double = variance
            override val varianceXY: Double = variance
            override val varianceYX: Double = variance
        }

        val degrees = object : XYVariances {
            private val gpsAccuracyDegrees = gpsAccuracy.toDouble() / mPer1DegreeEq
            override val accuracy: Float = gpsAccuracyDegrees.toFloat()
            private val variance: Double = (gpsAccuracy * gpsAccuracy / (mPer1DegreeEq * mPer1DegreeEq))
            override val varianceXX: Double = variance
            override val varianceYY: Double = variance
            override val varianceXY: Double = variance
            override val varianceYX: Double = variance
        }
    }

    class Bearing private constructor(val degrees: Double) {
        private val radian: Double = toRadians(degrees)
        val cos: Double = cos(radian)
        val sin: Double = sin(radian)
        companion object {
            const val maxVarianceDegrees: Double = 180.0
            fun fromDegrees(degrees: Double): Bearing {
                return Bearing(degrees)
            }
            fun fromRadians(radians: Double): Bearing {
                return Bearing(Math.toDegrees(radians))
            }
        }
    }

    class Longitude private constructor(val degrees: Double, val meters: Double) {
        fun latitudeDegreesToMeters(degrees: Double): Double = degrees * mPer1DegreeEq
        fun latitudeMetersToDegrees(meters: Double): Double = meters / mPer1DegreeEq

        companion object {
            fun fromMeters(meters: Double, latitude: Latitude): Longitude {
                return Longitude(latitude.longitudeMetersToDegrees(meters), meters)
            }

            fun fromDegrees(degrees: Double, latitude: Latitude): Longitude {
                return Longitude(degrees, latitude.longitudeDegreesToMeters(degrees))
            }
        }
    }

    class Latitude private constructor (val degrees: Double, val meters: Double) : Units() {

        private val longitude1DegreeLength by lazy {
            val latitudeRadians = toRadians(degrees)
            mPer1DegreePolar * cos(latitudeRadians) - 93.5 * cos(3 * latitudeRadians)
        }

        fun longitudeDegreesToMeters(degrees: Double): Double = degrees * longitude1DegreeLength
        fun longitudeMetersToDegrees(meters: Double): Double = meters / longitude1DegreeLength

        companion object {
            fun fromMeters(meters: Double): Latitude {
                return Latitude(meters / mPer1DegreeEq, meters)
            }

            fun fromDegrees(degrees: Double): Latitude {
                return Latitude(degrees, degrees * mPer1DegreeEq)
            }
        }
    }

    class Proximity(val meters: Float) {
        val degrees: Double = meters / mPer1DegreeEq
    }

    /**
     * Velocity in meters per second
     * @param mPerSec velocity in meters per second
     * @param bearing bearing in degrees, velocity is a vector and it does not matter with bearing
     * param longitude longitude, it is needed to convert y projection meters to degrees
     * param latitude latitude, it is needed to convert x projection meters to degrees
     */
    class Velocity private  constructor(val mPerSec: Double, val x: Projection.X, val y: Projection.Y) {
                   //bearing: Bearing, longitude: Longitude, latitude: Latitude) {

        constructor(metersX: Double, metersY: Double) : this(
            sqrt(metersX * metersX + metersY * metersY),
            Projection.X.fromX(metersX),
            Projection.Y.fromY(metersY))
        constructor(
            mPerSec: Double,
            bearing: Bearing,
        ) : this(mPerSec, Projection.X.fromModule(mPerSec, bearing), Projection.Y.fromModule(mPerSec, bearing))

        val kmPerH = (mPerSec * 3600 / 1000).toFloat()
    }

}