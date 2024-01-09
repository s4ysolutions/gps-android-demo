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
class GPSFilterKalmanConstantPosition : GPSFilterKalman() {
    companion object {
        val instance = GPSFilterKalmanConstantPosition()
    }

    override val name: String = "Kalman (constant position)"

    override val bearingImportant: Boolean = false

    /**
     * A - state transition matrix
     */
    override fun createAMatrix(dt: Double, stateVector: StateVector): RealMatrix =
        Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(1.0, 0.0),
                doubleArrayOf(0.0, 1.0)
            )
        )

    /**
     * Q - Process noise covariance matrix
     */
    override fun createQMatrix(dt: Double, stateVector: StateVector): RealMatrix {
        // in fact the process is extremely noisy
        // lets assume the speed is 5km/h = 1.5m/s
        // so the position is changed by 3 meters * dt / 1000
        val variance = 1.5 * dt
        val covariance = variance * variance

        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(covariance, 0.0),
                doubleArrayOf(0.0, covariance)
            )
        )
    }

    override fun createP0Matrix(dt: Double): RealMatrix {
        // the same as Q
        val variance = 1.5 * dt
        val covariance = variance * variance

        return Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(covariance, 0.0),
                doubleArrayOf(0.0, covariance)
            )
        )
    }

    // measurements
    override fun createHMatrix(): RealMatrix = Array2DRowRealMatrix(
        arrayOf(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(0.0, 1.0)
        )
    )

    override fun createRMatrix(accuracy: Units.Accuracy): RealMatrix =
        with(accuracy.meters) {
            Array2DRowRealMatrix(
                arrayOf(
                    doubleArrayOf(varianceXX, 0.0),
                    doubleArrayOf(0.0, varianceYY)
                )
            )
        }

    override fun measurementVectorFromGPSUpdate(transition: Transition): MeasurementVector =
        MeasurementVector.LongitudeLatitude(transition)

    override fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector =
        StateVector.LongitudeLatitude(stateArray)

}