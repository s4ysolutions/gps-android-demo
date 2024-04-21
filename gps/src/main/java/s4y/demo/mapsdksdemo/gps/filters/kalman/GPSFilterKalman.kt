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
import s4y.demo.mapsdksdemo.gps.filters.GPSFilterProximity

abstract class GPSFilterKalman : GPSFilter() {
    private var logger: FilterLogger? = null

    interface FilterLogger {
        fun e(message: String, th: Throwable)
    }

    abstract val bearingImportant: Boolean
    internal lateinit var transition: Transition

    private val proximityFilter = GPSFilterProximity.instance1m

    private var km: KalmanFilter? = null
    private var processModel: ProcessModel? = null
    private var measurementModel: MeasurementModel? = null
    internal val kalmanFilter: KalmanFilter get() = km!!

    // A - state transition matrix
    abstract fun createAMatrix(dt: Double, stateVector: StateVector): RealMatrix

    // B - control input matrix - no input
    open fun createBMatrix(): RealMatrix? = null

    // Assume the process error depends on the time
    // Q - process noise covariance matrix
    abstract fun createQMatrix(dt: Double, stateVector: StateVector): RealMatrix

    // P - Filter error covariance matrix
    abstract fun createP0Matrix(dt: Double): RealMatrix?

    // H - measurement matrix
    abstract fun createHMatrix(): RealMatrix

    // R - measurement noise covariance matrix
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
        proximityFilter.reset()
        transition = Transition()
        km = null
    }

    override fun reset(gpsUpdate: GPSUpdate, dt: Double) {
        reset()

        val x0 = measurementVectorFromGPSUpdate(transition).state
        val p0 = createP0Matrix(dt)

        updateKalmanFilter(
            createProcessModel(dt, x0, p0),
            createMeasurementModel(Units.Accuracy(gpsUpdate))
        )
    }

    internal abstract fun measurementVectorFromGPSUpdate(transition: Transition): MeasurementVector

    abstract fun stateVectorFromEstimation(stateArray: DoubleArray): StateVector

    private val lockApply = Any()

    override fun apply(gpsUpdate: GPSUpdate): GPSUpdate =
        synchronized(lockApply) {
            km?.let { km ->
                // Kalman loop
                try {
                    transition.setCurrentState(gpsUpdate)
                    val z = measurementVectorFromGPSUpdate(transition)
                    val x0 = stateVectorFromEstimation(km.stateEstimation)

                    if (transition.accuracyChanged) {
                        updateRMatrix(transition.accuracy)
                    }

                    if (transition.accuracyChanged || transition.dtChanged) {
                        val dt = transition.dtSec
                        updateKalmanFilter(
                            createProcessModel(
                                dt,
                                x0,
                                processModel?.processNoise ?: createP0Matrix(dt)
                            ),
                            measurementModel ?: createMeasurementModel(
                                transition.accuracy
                            )
                        )
                    }

                    km.predict()
                    km.correct(z.state.vectorMeters)

                    val x = stateVectorFromEstimation(km.stateEstimation)
                    //x.toGpsUpdate(z)
                    proximityFilter.apply(x.toGpsUpdate(transition))
                } catch (e: Exception) {
                    logger?.e("KalmanFilter.apply ${e.message}", e)
                    gpsUpdate
                }
            } ?: run {
                reset(gpsUpdate)
                gpsUpdate
            }
        }

}