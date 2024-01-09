package s4y.demo.mapsdksdemo.gps.filters.kalman.data

import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalman
import kotlin.math.sqrt

sealed class StateVector private constructor() {
    abstract val vectorMeters: DoubleArray
    abstract fun toGpsUpdate(transition: GPSFilterKalman.Transition): GPSUpdate
    class LongitudeLatitude(override val vectorMeters: DoubleArray) :
        StateVector() {
        private val latitude: Units.Latitude = Units.Latitude.fromMeters(vectorMeters[1])
        private val longitude: Units.Longitude = Units.Longitude.fromMeters(
            vectorMeters[0],
            latitude
        )

        override fun toGpsUpdate(transition: GPSFilterKalman.Transition): GPSUpdate {
            return GPSUpdate(
                latitude.degrees,
                longitude.degrees,
                transition.velocity.kmPerH,
                transition.accuracy.meters.accuracy,
                transition.bearing,
                transition.ts
            )
        }
    }

    class Velocity(override val vectorMeters: DoubleArray) : StateVector() {
        /**
         * [longitude, latitude, velocityX, velocityY]
         */
        private val latitude: Units.Latitude = Units.Latitude.fromMeters(vectorMeters[1])
        private val longitude: Units.Longitude =
            Units.Longitude.fromMeters(vectorMeters[0], latitude)

        private val velocity by lazy {
            (sqrt(vectorMeters[2] * vectorMeters[2] + vectorMeters[3] * vectorMeters[3]) * 3.6)
                .toFloat()
        }

        override fun toGpsUpdate(transition: GPSFilterKalman.Transition): GPSUpdate {
            return GPSUpdate(
                latitude.degrees,
                longitude.degrees,
                velocity,
                transition.accuracy.meters.accuracy,
                transition.evaluatedBearing.degrees,
                transition.ts
            )
        }
    }

    class Acceleration(override val vectorMeters: DoubleArray) : StateVector() {
        /**
         * [longitude, latitude, velocityX, velocityY, accelerationX, accelerationY]
         */
        private val latitude: Units.Latitude = Units.Latitude.fromMeters(vectorMeters[1])
        private val longitude: Units.Longitude =
            Units.Longitude.fromMeters(vectorMeters[0], latitude)
        private val velocity by lazy {
            (sqrt(vectorMeters[2] * vectorMeters[2] + vectorMeters[3] * vectorMeters[3]) * 3.6)
                .toFloat()
        }

        /*
         these 2 are not needed for the GPSUpdate, so we do not store them
        val accelerationX: Double = array[5]
        val accelerationY: Double = array[6]
         */
        override fun toGpsUpdate(transition: GPSFilterKalman.Transition): GPSUpdate {
            return GPSUpdate(
                latitude.degrees,
                longitude.degrees,
                velocity,
                transition.accuracy.meters.accuracy,
                transition.evaluatedBearing.degrees,
                transition.ts
            )
        }
    }
}