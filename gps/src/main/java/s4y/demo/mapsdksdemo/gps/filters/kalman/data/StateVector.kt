package s4y.demo.mapsdksdemo.gps.filters.kalman.data

import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanTransition
import kotlin.math.sqrt

sealed class StateVector private constructor() {
    abstract val vectorMeters: DoubleArray
    abstract val latitude: Units.Latitude
    abstract val longitude: Units.Longitude
    abstract fun toGpsUpdate(transition: GPSFilterKalmanTransition): GPSUpdate
    class LongitudeLatitude(override val vectorMeters: DoubleArray) :
        StateVector() {
        override val latitude: Units.Latitude = Units.Latitude.fromMeters(vectorMeters[1])
        override val longitude: Units.Longitude = Units.Longitude.fromMeters(
            vectorMeters[0],
            latitude
        )

        override fun toGpsUpdate(transition: GPSFilterKalmanTransition): GPSUpdate {
            return GPSUpdate(
                latitude.degrees,
                longitude.degrees,
                transition.velocity.kmPerH,
                transition.accuracy.meters.accuracy,
                transition.bearingDegrees,
                transition.ts
            )
        }
    }

    class Velocity(override val vectorMeters: DoubleArray) : StateVector() {
        /**
         * [longitude, latitude, velocityX, velocityY]
         */
        override val latitude: Units.Latitude = Units.Latitude.fromMeters(vectorMeters[1])
        override val longitude: Units.Longitude =
            Units.Longitude.fromMeters(vectorMeters[0], latitude)

        val velocityX get() = vectorMeters[2]
        val velocityY get() = vectorMeters[3]

        private val velocity by lazy {
            (sqrt(velocityX * velocityX + velocityY * velocityY) * 3.6).toFloat()
        }

        override fun toGpsUpdate(transition: GPSFilterKalmanTransition): GPSUpdate {
            return GPSUpdate(
                latitude.degrees,
                longitude.degrees,
                velocity,
                transition.accuracy.meters.accuracy,
                transition.bearingDegrees,
                transition.ts
            )
        }
    }

    class Acceleration(override val vectorMeters: DoubleArray) : StateVector() {
        /**
         * [longitude, latitude, velocityX, velocityY, accelerationX, accelerationY]
         */
        override val latitude: Units.Latitude = Units.Latitude.fromMeters(vectorMeters[1])
        override val longitude: Units.Longitude =
            Units.Longitude.fromMeters(vectorMeters[0], latitude)
        val velocityX get() = vectorMeters[2]
        val velocityY get() = vectorMeters[3]
        private val velocity by lazy {
            (sqrt(velocityX * velocityX + velocityY * velocityY) * 3.6).toFloat()
        }

        val accelerationX: Double = vectorMeters[4]
        val accelerationY: Double = vectorMeters[5]
        override fun toGpsUpdate(transition: GPSFilterKalmanTransition): GPSUpdate {
            return GPSUpdate(
                latitude.degrees,
                longitude.degrees,
                velocity,
                transition.accuracy.meters.accuracy,
                transition.bearingDegrees,
                transition.ts
            )
        }
    }
}