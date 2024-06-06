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
class GPSFilterKalmanConstantVelocity : GPSFilterKalman() {
    companion object {
        val instance = GPSFilterKalmanConstantVelocity()
    }

    override val name: String = "Kalman (constant velocity)"

    // override val bearingImportant: Boolean = true

    /**
     * Process equation is:
     * x = x0 + velocityX * dt;
     * y = y0 + velocityY * dt;
     * velocityX = velocityX0;
     * velocityY = velocityY0;
     */
    override fun createAMatrix(dt: Double, stateVector: StateVector): RealMatrix {
        if (stateVector !is StateVector.Velocity)
            throw IllegalArgumentException("StateVector is not VelocityBearing")
        // [longitude, latitude, velocityX, velocityY]
        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(1.0, 0.0, dt, 0.0),
                doubleArrayOf(0.0, 1.0, 0.0, dt),
                doubleArrayOf(0.0, 0.0, 1.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0),
            )
        )
    }

    /**
     * Q - Process noise covariance matrix
     *     velocity can change up to max expected acceleration
     */
    override fun createQMatrix(dt: Double, stateVector: StateVector): RealMatrix {
        if (stateVector !is StateVector.Velocity)
            throw IllegalArgumentException("StateVector is not VelocityBearing")

        val maxA = 4 // m/s^2
        val varV = maxA * dt
        val covV = varV * varV

        val varX = stateVector.velocityX * dt
        val varY = stateVector.velocityY * dt
        val covX = varX * varX
        val covY = varY * varY

        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(covX, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, covY, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, covV, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, covV),
            )
        )
    }
    override fun createP0Matrix(dt: Double): RealMatrix {
        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0),
            )
        )
    }

    /**
     * H - measurement matrix
     */
    override fun createHMatrix(): RealMatrix = Array2DRowRealMatrix(
        arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 1.0),
        )
    )

    /**
     * R - measurement noise covariance matrix
     */
    override fun createRMatrix(accuracy: Units.Accuracy): RealMatrix =
        with(accuracy.meters) {
            val vxx = covarianceXX
            val vyy = covarianceYY
            if (transition.dtSec != 0.0) {
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(vxx, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, vyy, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0),
                    )
                )
            } else {
                val vvx = vxx / transition.dtSec
                val vvy = vyy / transition.dtSec
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(vxx, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, vyy, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, vvx, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, vvy),
                    )
                )
            }
        }

    override fun measurementVectorFromLastTransition(
        transition: GPSFilterKalmanTransition
    ): MeasurementVector = MeasurementVector.VelocityBearing(transition)

    override fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector =
        StateVector.Velocity(stateArray)
}