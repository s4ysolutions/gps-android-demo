package s4y.demo.mapsdksdemo.gps.filters.kalman.data

import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalman
sealed class MeasurementVector(private val transition: GPSFilterKalman.Transition) {

    abstract val state: StateVector
    override fun toString(): String = with(transition) {
        "MeasurementVector(latitude=$latitude, longitude=$longitude, velocity=$velocity, bearing=$evaluatedBearing, accuracy=$accuracy, ts=$ts)"
    }

    class LongitudeLatitude(transition: GPSFilterKalman.Transition) :
        MeasurementVector(transition) {
        override val state =
            StateVector.LongitudeLatitude(
                doubleArrayOf(
                    transition.metersX,
                    transition.metersY
                )
            )
    }

    // It was noted GPS bearing is not reliable, so we use the bearing from the previous state
    // trying to keep the bearing constant
    // val bearing = transition.evaluatedBearing
    class VelocityBearing(transition: GPSFilterKalman.Transition) : MeasurementVector(transition) {
        override val state =
            StateVector.Velocity(
                doubleArrayOf(
                    transition.metersX,
                    transition.metersY,
                    transition.velocityX,
                    transition.velocityY,
                )
            )
    }

    class AccelerationBearing(transition: GPSFilterKalman.Transition) :
        MeasurementVector(transition) {
        override val state =
            StateVector.Acceleration(
                doubleArrayOf(
                    transition.metersX,
                    transition.metersY,
                    transition.velocityX,
                    transition.velocityY,
                    transition.accelerationX,
                    transition.accelerationY
                )
            )
    }
}