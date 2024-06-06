package s4y.demo.mapsdksdemo.gps.filters.kalman

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.RealMatrix
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.MeasurementVector
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.StateVector
import s4y.demo.mapsdksdemo.gps.data.Units

/**
 * Assumes the velocity is constant
 * StateVector is [longitude, latitude, velocity, bearing]
 */
class GPSFilterKalmanConstantAcceleration : GPSFilterKalman() {
    companion object {
        val instance = GPSFilterKalmanConstantAcceleration()
    }

    override val name: String = "Kalman (constant acceleration)"

    /**
     * Process equation is:
     * x = x0 + velocityX * dt + 0.5 * accelerationX * dt^2;
     * y = y0 + velocityY * dt + 0.5 * accelerationY * dt^2;
     * velocityX = velocityX0;
     * velocityY = velocityY0;
     * accelerationX = accelerationX0;
     * accelerationY = accelerationY0;
     */
    override fun createAMatrix(dt: Double, stateVector: StateVector): RealMatrix {
        if (stateVector !is StateVector.Acceleration)
            throw IllegalArgumentException("StateVector is not AccelerationBearing")
        val dt2 = (dt * dt / 2)
        return Array2DRowRealMatrix(
            // x0, y0, v,  ax, ay
            arrayOf(
                //            x    y    vx   vy    ax   ay
                doubleArrayOf(1.0, 0.0, dt, 0.0,  dt2, 0.0),
                doubleArrayOf(0.0, 1.0, 0.0, dt,  0.0, dt2),
                doubleArrayOf(0.0, 0.0, 1.0, 0.0, dt,  0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, dt),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
            )
        )
    }

    /**
     * Q - Process noise covariance matrix
     *     velocity can change up to max expected acceleration
     */
    override fun createQMatrix(dt: Double, stateVector: StateVector): RealMatrix {
        if (stateVector !is StateVector.Acceleration)
            throw IllegalArgumentException("StateVector is not VelocityBearing")

        val varA = 1.0 // acceleration expected to do not change more than 1m/s^2
        val cvA = varA * varA

        val varVx = stateVector.accelerationX * dt
        val cVx = varVx * varVx

        val varVy = stateVector.accelerationY * dt
        val cVy = varVy * varVy

        val varX = stateVector.velocityX * dt + 0.5 * stateVector.accelerationX * dt * dt
        val cvX = varX * varX
        val varY = stateVector.velocityY * dt + 0.5 * stateVector.accelerationY * dt * dt
        val cvY = varY * varY

        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(cvX, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, cvY, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, cVx, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, cVy, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, cvA, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, cvA),
            )
        )
    }

    /**
     * H - measurement matrix
     */
    override fun createHMatrix(): RealMatrix = Array2DRowRealMatrix(
        arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        )
    )

    /**
     * R - measurement noise covariance matrix
     */
    override fun createRMatrix(accuracy: Units.Accuracy): RealMatrix =
        with(accuracy.meters) {
            val vxx = covarianceXX * covarianceXX
            val vyy = covarianceYY * covarianceYY
            if (transition.dtSec != 0.0) {
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(vxx, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, vyy, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    )
                )
            } else {
                val vvx = vxx / transition.dtSec
                val vvy = vyy / transition.dtSec
                val dt2 = transition.dtSec * transition.dtSec
                val vax = vxx / dt2
                val vay = vyy / dt2
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(vxx, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, vyy, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, vvx, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, vvy, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, vax, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, vay),
                    )
                )
            }
        }

    override fun measurementVectorFromLastTransition(
        transition: GPSFilterKalmanTransition
    ): MeasurementVector =
        MeasurementVector.AccelerationBearing(transition)

    override fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector =
        StateVector.Acceleration(stateArray)
}