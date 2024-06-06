package s4y.demo.mapsdksdemo.gps.filters.kalman

import org.apache.commons.math3.filter.DefaultMeasurementModel
import org.apache.commons.math3.filter.DefaultProcessModel
import org.apache.commons.math3.filter.KalmanFilter
import org.apache.commons.math3.filter.MeasurementModel
import org.apache.commons.math3.filter.ProcessModel
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealMatrix
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.MeasurementVector
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.StateVector
import s4y.demo.mapsdksdemo.gps.data.Units
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter

abstract class GPSFilterKalman : GPSFilter() {
    private var logger: FilterLogger? = null

    interface FilterLogger {
        fun e(message: String, th: Throwable)
    }

    // abstract val bearingImportant: Boolean
    internal lateinit var transition: GPSFilterKalmanTransition
    private var km: KalmanFilter? = null
    private var processModel: ProcessModel? = null
    private var measurementModel: MeasurementModel? = null
    internal val kalmanFilter: KalmanFilter get() = km!!

    // A - transitionMatrix - state estimation, operates on previous state
    abstract fun createAMatrix(dt: Double, stateVector: StateVector): RealMatrix
    private fun updateAMatrix(dt: Double, x0: StateVector) = processModel?.let { pm ->
        val a = createAMatrix(dt, x0)
        a.data.forEachIndexed { i, row ->
            row.forEachIndexed { j, value ->
                pm.stateTransitionMatrix.setEntry(i, j, value)
            }
        }
    }

    // B - controlMatrix - control input, operates on control vector
    //                     and the result is added to the estimated state
    open fun createBMatrix(): RealMatrix? = null

    // Q - process noise covariance matrix
    //     to be added to the process error covariance matrix
    abstract fun createQMatrix(dt: Double, stateVector: StateVector): RealMatrix
    private fun updateQMatrix(dt: Double, x0: StateVector) = processModel?.let { pm ->
        val q = createQMatrix(dt, x0)
        q.data.forEachIndexed { i, row ->
            row.forEachIndexed { j, value ->
                pm.processNoise.setEntry(i, j, value)
            }
        }
    }

    // P0 - Initial process error covariance matrix
    //      default is Q
    open fun createP0Matrix(dt: Double): RealMatrix? = null

    // H - measurement matrix
    abstract fun createHMatrix(): RealMatrix

    // R - measurement noise covariance matrix
    //    to be added to be used as an addition in the calculation of S matrix
    //    where S matrix = H * P * H^T + R
    abstract fun createRMatrix(accuracy: Units.Accuracy): RealMatrix
    private fun updateRMatrix(accuracy: Units.Accuracy) = measurementModel?.let { mm ->
        val r = createRMatrix(accuracy)
        r.data.forEachIndexed { i, row ->
            row.forEachIndexed { j, value ->
                if (i == j) {
                    mm.measurementNoise.setEntry(i, j, value)
                }
            }
        }
    }


    private fun createProcessModel(
        dt: Double,
        x0: StateVector,
        p0: RealMatrix?
    ): ProcessModel {
        val a = createAMatrix(dt, x0)
        val b = createBMatrix()
        val q = createQMatrix(dt, x0)

        return DefaultProcessModel(a, b, q, ArrayRealVector(x0.vectorMeters), p0)
    }

    private fun createMeasurementModel(
        accuracy: Units.Accuracy
    ): MeasurementModel {
        val h = createHMatrix()
        val r = createRMatrix(accuracy)
        return DefaultMeasurementModel(h, r)
    }

    private fun updateKalmanFilter(
        processModel: ProcessModel,
        measurementModel: MeasurementModel
    ) {
        this.processModel = processModel
        this.measurementModel = measurementModel
        km = KalmanFilter(processModel, measurementModel)
    }

    override fun reset() {
        transition = GPSFilterKalmanTransition()
        km = null
    }

    // internal abstract fun measurementVectorFromGPSUpdate(transition: GPSFilterKalmanTransition): MeasurementVector
    internal abstract fun measurementVectorFromLastTransition(transition: GPSFilterKalmanTransition): MeasurementVector

    abstract fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector

    private val lockApply = Any()

    override fun apply(gpsUpdate: GPSUpdate): GPSUpdate? =
        synchronized(lockApply) {
            transition.addMeasurement(gpsUpdate)
            val z = measurementVectorFromLastTransition(transition)

            km?.let { km ->
                // Kalman loop
                try {
                    // just to save battery a bit
                    if (transition.distanceMeters < 1)
                        return null

                    if (transition.accuracyChanged) {
                        updateRMatrix(transition.accuracy)
                    }

                    if (transition.dtChanged) {
                        val dt = transition.dtSec
                        val x0 = stateVectorFromEstimation(km.stateEstimation)
                        updateAMatrix(dt, x0)
                        updateQMatrix(dt, x0)
                    }

                    km.predict()
                    km.correct(z.state.vectorMeters)

                    val estimation = stateVectorFromEstimation(km.stateEstimation)
                    transition.updateWithEstimation(estimation)

                    estimation.toGpsUpdate(transition)
                } catch (e: Exception) {
                    logger?.e("KalmanFilter.apply ${e.message}", e)
                    gpsUpdate
                }
            } ?: run {
                val x0 = z.state
                val p0 = createP0Matrix(defaultDt)

                updateKalmanFilter(
                    createProcessModel(defaultDt, x0, p0),
                    createMeasurementModel(Units.Accuracy(gpsUpdate))
                )
                gpsUpdate
            }
        }
}