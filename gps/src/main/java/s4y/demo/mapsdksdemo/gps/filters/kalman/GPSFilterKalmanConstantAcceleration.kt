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

    override val bearingImportant: Boolean = true

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

        // max acceleration is 4m/s^2 ~3
        // so covariance is 3^2 = 9.0

        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 9.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 9.0),
            )
        )
    }

    override fun createP0Matrix(dt: Double): RealMatrix? {
        return null
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
            val vxx = varianceXX * varianceXX
            val vyy = varianceYY * varianceYY
            val vvx = vxx / (if (transition.dtSec == 0.0) defaultDt else transition.dtSec)
            val vvy = vyy / (if (transition.dtSec == 0.0) defaultDt else transition.dtSec)
            val dt2 = if (transition.dtSec == 0.0)
                defaultDt * defaultDt
            else
                transition.dtSec * transition.dtSec
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

    override fun measurementVectorFromGPSUpdate(
        transition: Transition
    ): MeasurementVector =
        MeasurementVector.AccelerationBearing(transition)

    override fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector =
        StateVector.Acceleration(stateArray)
}