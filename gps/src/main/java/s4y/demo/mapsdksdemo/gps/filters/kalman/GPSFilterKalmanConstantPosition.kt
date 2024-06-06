package s4y.demo.mapsdksdemo.gps.filters.kalman

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.RealMatrix
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.MeasurementVector
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.StateVector
import s4y.demo.mapsdksdemo.gps.data.Units

/**
 * ignores the velocity and acceleration and keeps the position constant
 * StateVector is [longitude, latitude]
 * Process equation is x = x0
 */
class GPSFilterKalmanConstantPosition(private val maxSpeed: Double) : GPSFilterKalman() {
    companion object {
        // GPS variance is about 10 meters
        // so the process error 30 meters
        // makes GPS data to have ~3 times more weight than the process
        val instance = GPSFilterKalmanConstantPosition(1.4)
    }

    override val name: String = "Kalman (constant position)"

    // A - state transition matrix
    override fun createAMatrix(dt: Double, stateVector: StateVector): RealMatrix =
        Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(1.0, 0.0),
                doubleArrayOf(0.0, 1.0)
            )
        )

    // Q - process noise covariance matrix
    override fun createQMatrix(dt: Double, stateVector: StateVector): RealMatrix {
        // Keep the position as much as possible
        // in fact the process is extremely noisy
        // lets assume the speed is at least 5km/h = 1.5m/s
        // so the position is changed by 3 meters * dt / 1000
        val variance = maxSpeed * dt
        val covariance = variance * variance

        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(covariance, 0.0),
                doubleArrayOf(0.0, covariance)
            )
        )
    }

    override fun createP0Matrix(dt: Double): RealMatrix {
        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(0.0, 0.0),
                doubleArrayOf(0.0, 0.0)
            )
        )
    }

    // H - measurement matrix
    override fun createHMatrix(): RealMatrix = Array2DRowRealMatrix(
        arrayOf(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(0.0, 1.0)
        )
    )

    // R - measurement noise covariance matrix
    override fun createRMatrix(accuracy: Units.Accuracy): RealMatrix =
        // derived from the accuracy of GPS
        with(accuracy.meters) {
            Array2DRowRealMatrix(
                arrayOf(
                    doubleArrayOf(covarianceXX, 0.0),
                    doubleArrayOf(0.0, covarianceYY)
                )
            )
        }

    override fun measurementVectorFromLastTransition(transition: GPSFilterKalmanTransition): MeasurementVector =
        MeasurementVector.LongitudeLatitude(transition)

    override fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector =
        StateVector.LongitudeLatitude(stateArray)

}