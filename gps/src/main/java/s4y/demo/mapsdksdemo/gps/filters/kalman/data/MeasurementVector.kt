package s4y.demo.mapsdksdemo.gps.filters.kalman.data

import s4y.demo.mapsdksdemo.gps.filters.kalman.Transition

sealed class MeasurementVector(private val transition: Transition) {

    abstract val state: StateVector
    override fun toString(): String = with(transition) {
        "MeasurementVector(latitude=$latitude, longitude=$longitude, velocity=$velocity, bearing=$bearingDegrees, accuracy=$accuracy, ts=$ts)"
    }

    class LongitudeLatitude(transition: Transition) :
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
    class VelocityBearing(transition: Transition) : MeasurementVector(transition) {
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

    class AccelerationBearing(transition: Transition) :
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