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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

abstract class GPSFilterKalman : GPSFilter() {
    private var logger: FilterLogger? = null

    interface FilterLogger {
        fun e(message: String, th: Throwable)
    }

    class Transition {
        private lateinit var _currentLatitude: Units.Latitude
        private lateinit var _currentLongitude: Units.Longitude

        private var _prevAccuracy: Float = 0.0f
        private var _currentAccuracy: Float = 0.0f
        private var _accuracyChanged: Boolean = false
        private var _prevTimeStamp: Long = System.currentTimeMillis()
        private var _currentTimeStamp: Long = System.currentTimeMillis()
        private var _prevDtSec: Double = 0.0
        private var _currentDtSec: Double = 0.0
        private var _dtChanged: Boolean = false
        /*
        private var _prevBearing: Double = 0.0
         */
        private var _currentBearing: Double = 0.0
        /*
        private var _bearingChanged: Boolean = false
        private var _bearingVariance = Units.Bearing.maxVarianceDegrees
        private var _bearingVariance2: Double = _bearingVariance * _bearingVariance
         */
        private var _bearingAverage: Double = 0.0
        private var _n: Int = 0
        private var _prevX: Double = 0.0
        private var _currentX: Double = 0.0
        private var _prevY: Double = 0.0
        private var _currentY: Double = 0.0
        private var _prevVelocityX: Double = 0.0
        private var _currentVelocityX: Double = 0.0
        private var _prevVelocityY: Double = 0.0
        private var _currentVelocityY: Double = 0.0

        fun setCurrentState(gpsUpdate: GPSUpdate) = synchronized(this) {
            _n++

            // save prev values if not the first update
            if (_n > 1) {
                _prevTimeStamp = _currentTimeStamp
                _prevAccuracy = _currentAccuracy
                _prevX = _currentX
                _prevY = _currentY
                // _prevBearing = _currentBearing
                if (_n > 2) {
                    // save prev differences
                    _prevDtSec = _currentDtSec
                    _prevVelocityX = _currentVelocityX
                    _prevVelocityY = _currentVelocityY
                }
            }
            // save current values
            _currentTimeStamp = gpsUpdate.ts
            _currentAccuracy = gpsUpdate.accuracy
            _currentBearing = gpsUpdate.bearing

            val lat = Units.Latitude.fromDegrees(gpsUpdate.latitude)
            _currentLatitude = Units.Latitude.fromDegrees(gpsUpdate.latitude)
            _currentLongitude = Units.Longitude.fromDegrees(gpsUpdate.longitude, lat)
            _currentX = _currentLongitude.meters
            _currentY = _currentLatitude.meters

            // calculate statistics
            _bearingAverage += (gpsUpdate.bearing - _bearingAverage) / _n
            /*
            _currentBearing = gpsUpdate.bearing
            val bearingDistance = (gpsUpdate.bearing - _bearingAverage)
            val bearingDistance2 = bearingDistance * bearingDistance
            _bearingVariance2 += (bearingDistance2 - _bearingVariance2) / _n
            _bearingVariance = sqrt(_bearingVariance2)
             */

            if (_n > 1) {
                // detect if values changed
                _accuracyChanged = abs(_currentAccuracy - _prevAccuracy) > 1.0f
                //   _bearingChanged = abs(_currentBearing - _prevBearing) > 1.0

                // calculate differences
                _currentDtSec = (_currentTimeStamp - _prevTimeStamp).toDouble() / 1000
                _currentVelocityX = (_currentX - _prevX) / _currentDtSec
                _currentVelocityY = (_currentY - _prevY) / _currentDtSec

                if (_n > 2) {
                    // detect if differences changed
                    _dtChanged = abs(_currentDtSec - _prevDtSec) > 0.333
                }
            }

            /*


                        _prevVelocityX = _currentVelocityX
                        _prevVelocityY = _currentVelocityY
                        if (_prevLongitude != null) {
                            _currentVelocityX =
                                (_currentLongitude!!.meters - _prevLongitude!!.meters) / _currentDtSec
                        }
                        if (_currentLatitude != null && _prevLatitude != null) {
                            _currentVelocityY =
                                (_currentLatitude!!.meters - _prevLatitude!!.meters) / _currentDtSec
                        }
             */
        }

        val latitude: Units.Latitude get() = _currentLatitude
        val longitude: Units.Longitude get() = _currentLongitude
        val metersX: Double get() = _currentX
        val metersY: Double get() = _currentY
        val ts: Long get() = _currentTimeStamp
        val accuracyChanged: Boolean get() = _accuracyChanged
        val dtChanged: Boolean get() = _dtChanged
        val dtSec get() = _currentDtSec

        private val evaluatedBearingDegree: Double
            get() = if (_n < 2) {
                _currentBearing
            } else {
                val x = _currentX - _prevX
                val y = _currentY - _prevY
                val bearing = atan2(y, x) * 180 / Math.PI
                if (bearing < 0) {
                    bearing + 360
                } else {
                    bearing
                }
            }

        val accuracy: Units.Accuracy get() = Units.Accuracy(_currentAccuracy)
        val bearing = _currentBearing
        val evaluatedBearing: Units.Bearing get() = Units.Bearing(evaluatedBearingDegree)
        val velocity: Units.Velocity
            get() = Units.Velocity(
                sqrt(_currentVelocityX * _currentVelocityX + _currentVelocityY * _currentVelocityY),
                evaluatedBearing,
                longitude,
                latitude
            )
        val velocityX: Double get() = _currentVelocityX
        val velocityY: Double get() = _currentVelocityY
        val accelerationX: Double get() = if (dtSec == 0.0) 0.0 else (_currentVelocityX - _prevVelocityX) / dtSec
        val accelerationY: Double get() = if (dtSec == 0.0) 0.0 else (_currentVelocityY - _prevVelocityY) / dtSec
    }

    abstract val bearingImportant: Boolean
    protected lateinit var transition: Transition

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

    abstract fun measurementVectorFromGPSUpdate(transition: Transition): MeasurementVector

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