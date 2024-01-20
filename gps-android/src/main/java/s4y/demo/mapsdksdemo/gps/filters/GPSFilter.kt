@file:Suppress("unused")

package s4y.demo.mapsdksdemo.gps.filters

import org.apache.commons.math3.filter.DefaultMeasurementModel
import org.apache.commons.math3.filter.DefaultProcessModel
import org.apache.commons.math3.filter.KalmanFilter
import org.apache.commons.math3.filter.MeasurementModel
import org.apache.commons.math3.filter.ProcessModel
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.RealMatrix
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.filters.data.MeasurementVector
import s4y.demo.mapsdksdemo.gps.filters.data.StateVector
import s4y.demo.mapsdksdemo.gps.filters.data.Units
import kotlin.math.abs

private const val DEFAULT_DT = 5L // seconds

sealed class GPSFilter(val defaultDt: Long = DEFAULT_DT) {
    abstract val name: String
    abstract fun apply(gpsUpdate: GPSUpdate): GPSUpdate?

    private val lockApplyAll = Any()

    open fun apply(gpsUpdates: Array<GPSUpdate>): Array<GPSUpdate> = synchronized(lockApplyAll){
        val result = arrayOfNulls<GPSUpdate>(gpsUpdates.size)
        var head = 0
        for (gpsUpdate in gpsUpdates) {
            val filtered = apply(gpsUpdate)
            if (filtered != null) {
                result[head++] = filtered
            }
        }
        @Suppress("UNCHECKED_CAST")
        return when (head) {
            0 -> emptyArray()
            result.size -> result
            else -> result.copyOfRange(0, head)
        } as Array<GPSUpdate>
    }

    abstract fun reset(gpsUpdate: GPSUpdate, dt: Long = defaultDt)
    class Null : GPSFilter() {
        companion object {
            val instance = Null()
        }

        override val name: String = "No filtering"

        override fun apply(gpsUpdate: GPSUpdate): GPSUpdate {
            return gpsUpdate
        }

        override fun apply(gpsUpdates: Array<GPSUpdate>): Array<GPSUpdate> {
            return gpsUpdates
        }

        override fun reset(gpsUpdate: GPSUpdate, dt: Long) {
        }
    }

    class Proximity(var proximity: Float) : GPSFilter() {
        companion object {
            val instance0 = Proximity(0.0f)
        }

        private var prev: GPSUpdate? = null
        override val name: String = "Skip close points"
        override fun apply(gpsUpdate: GPSUpdate): GPSUpdate? {
            val p = prev
            prev = gpsUpdate
            return if (p == null)
                gpsUpdate
            else if (abs(gpsUpdate.latitude - p.latitude) > proximity ||
                abs(gpsUpdate.longitude - p.longitude) > proximity
            )
                gpsUpdate
            else null
        }

        override fun reset(gpsUpdate: GPSUpdate, dt: Long) {
            prev = null
        }
    }

    sealed class Kalman : GPSFilter() {
        internal companion object {
            /*

            // variances
            @Suppress("unused")
            val Float.V get(): Double = (this * this).toDouble() // 5 meters per second deviation
            val Double.V get() = this * this // 5 meters per second deviation

            @Suppress("unused")
            fun Float.vV(dt: Long): Double = (this / dt).toDouble()

            @Suppress("unused")
            fun Double.vV(dt: Long) = this / dt

            @Suppress("unused")
            fun Float.aV(dt: Long): Double = this * 2 / (dt * dt).toDouble()
            fun Double.aV(dt: Long) = this * 2 / (dt * dt)

            // projections to x and y axes (reverse)
            fun Float.toX(cosBearing: Double): Float = this * cosBearing.toFloat()

            @Suppress("unused")
            fun Double.toX(cosBearing: Double): Double = this * cosBearing
            fun Float.toY(sinBearing: Double): Float = this * sinBearing.toFloat()

            @Suppress("unused")
            fun Double.toY(sinBearing: Double): Double = this * sinBearing

            // conversions meters to degrees
            const val mPerDegree = 111139.0f // meters per degree
            val Float.latDg: Float get() = this / mPerDegree
            val Double.latDg: Double get() = this / mPerDegree
            fun Float.longDg(cosLatitude: Double): Float = (this.latDg / cosLatitude).toFloat()
            fun Double.longDg(cosLatitude: Double): Double = this.latDg / cosLatitude

            // conversions degrees to meters
            val Float.latMeters: Float get() = this * mPerDegree
            val Double.latMeters: Double get() = this * mPerDegree

            @Suppress("unused", "unused")
            fun Float.longMeters(cosLatitude: Double): Float =
                (this.latMeters * cosLatitude).toFloat()

            @Suppress("unused", "unused")
            fun Double.longMeters(cosLatitude: Double): Double =
                this.latMeters * cosLatitude

            // extract data from state array
            val DoubleArray.longitude: Double get() = this[0]

            @Suppress("unused")
            val DoubleArray.latitude: Double get() = this[1]
            val DoubleArray.vx: Double get() = this[2]
            val DoubleArray.vy: Double get() = this[3]

            val DoubleArray.ax: Double get() = this[4]
            val DoubleArray.ay: Double get() = this[5]

            val DoubleArray.bearing: Double get() = this[6]

            fun moduleDegreesToMeters(ax: Float, ay: Float): Float =
                sqrt(ax * ax + ay * ay)

            fun moduleDegreesToMeters(ax: Double, ay: Double): Double =
                sqrt(ax * ax + ay * ay)

            fun moduleDegreesToMeters(ax: Double, ay: Double, cosLatitude: Double): Double {
                val axm = ax.longMeters(cosLatitude)
                val aym = ay.latMeters
                return moduleDegreesToMeters(axm, aym)
            }

            fun moduleDegreesToMeters(ax: Float, ay: Float, cosLatitude: Double): Float =
                moduleDegreesToMeters(ax.toDouble(), ay.toDouble(), cosLatitude).toFloat()
             */
        }

        // keep them as properties for debug purposes
        // https://commons.apache.org/proper/commons-math/userguide/filter.html
        // https://github.com/dsame/kalman-2d/blob/main/README.md
        internal lateinit var a: RealMatrix
        internal var b: RealMatrix? = null
        internal lateinit var q: RealMatrix
        internal lateinit var p0: RealMatrix
        internal lateinit var h: RealMatrix
        internal lateinit var r: RealMatrix
        internal lateinit var pm: ProcessModel
        internal lateinit var mm: MeasurementModel
        private var km: KalmanFilter? = null
        internal val kalmanFilter: KalmanFilter get() = km!!

        protected var prevTimeStamp: Long = System.currentTimeMillis()
        protected var prevDt: Long = 0
        abstract fun createAMatrix(dt: Long, stateVector: StateVector): RealMatrix;
        abstract fun createBMatrix(): RealMatrix?
        abstract fun createQMatrix(): RealMatrix
        abstract fun createP0Matrix(): RealMatrix
        private fun createProcessModel(dt: Long, x0: StateVector): ProcessModel {
            a = createAMatrix(dt, x0)

            b = createBMatrix()

            q = createQMatrix()

            p0 = createP0Matrix()
            return DefaultProcessModel(a, b, q, x0.vector, p0)
        }

        abstract fun createHMatrix(): RealMatrix
        abstract fun createRMatrix(accuracy: Units.Accuracy): RealMatrix
        private fun createMeasurementModel(
            accuracy: Units.Accuracy
        ): MeasurementModel {
            h = createHMatrix()
            r = createRMatrix(accuracy)
            return DefaultMeasurementModel(h, r)
        }

        override fun reset(gpsUpdate: GPSUpdate, dt: Long) {
            println("===> KalmanFilter.reset $gpsUpdate")
            prevTimeStamp = gpsUpdate.ts
            prevDt = defaultDt
            val x0 = measurement(gpsUpdate).state

            pm = createProcessModel(dt, x0)
            mm = createMeasurementModel(Units.Accuracy(gpsUpdate))

            println("===> KalmanFilter.reset will set km")
            km = KalmanFilter(pm, mm)
            println("===> KalmanFilter.reset has set km")
        }

        abstract fun measurement(gpsUpdate: GPSUpdate): MeasurementVector
        abstract fun state(stateArray: DoubleArray): StateVector

        private val lockApply = Any()
        override fun apply(gpsUpdate: GPSUpdate): GPSUpdate = synchronized(lockApply) {
            km?.let { km ->
                println("===> KalmanFilter.apply $gpsUpdate")
                val dt = gpsUpdate.ts - prevTimeStamp
                // val dtChanged = abs(dt - prevDt) > 500
                prevDt = dt
                prevTimeStamp = gpsUpdate.ts
                val z = measurement(gpsUpdate)
                val x0 = state(km.stateEstimation)

                /*
        if (dtChanged) {
            updateAMatrix(dt, x0)
        }*/

                // val preCosLatitude = cos(Math.toRadians(x0.latitude))
                // val cosLatitude = cos(Math.toRadians(z.latitude))
                /*
        val prevLong1DegreeLength = 1.0.longDg(preCosLatitude)
        val long1DegreeLength = 1.0.longDg(cosLatitude)

        val long1DegreeChanged = abs(prevLong1DegreeLength - long1DegreeLength) > 1
        if (long1DegreeChanged) {
            updateQMatrix(cosLatitude)
        }*/

                km.predict()

                /*
        if (dtChanged || long1DegreeChanged) {
            updateRMatrix(dt, gpsUpdate)
        }
             */

                km.correct(z.state.vector)

                val x = state(km.stateEstimation)
                x.toGpsUpdate(z)
            } ?: run {
                reset(gpsUpdate)
                gpsUpdate
            }
        }

        class ConstantPosition : Kalman() {
            override val name: String = "Kalman (constant position)"

            companion object {
                val instance = ConstantPosition()
            }

            /**
             * Creates a process model
             * ignores the velocity and acceleration and keeps the position constant
             */
            override fun createAMatrix(dt: Long, stateVector: StateVector): RealMatrix =
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(1.0, 0.0),
                        doubleArrayOf(0.0, 1.0)
                    )
                )

            /**
             * No control matrix
             */
            override fun createBMatrix(): RealMatrix? =
                null

            /**
             * Process noise covariance matrix
             */
            override fun createQMatrix(): RealMatrix =
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(1e-9, 0.0),
                        doubleArrayOf(0.0, 1e-9)
                    )
                )

            override fun createP0Matrix(): RealMatrix =
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(1e-4, 0.0),
                        doubleArrayOf(0.0, 1e-4)
                    )
                )


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
                            doubleArrayOf(varianceXX, varianceXY),
                            doubleArrayOf(varianceYX, varianceYY)
                        )
                    )
                }

            override fun measurement(gpsUpdate: GPSUpdate): MeasurementVector =
                MeasurementVector.LongitudeLatitude(gpsUpdate)

            override fun state(stateArray: DoubleArray): StateVector =
                StateVector.LongitudeLatitude(stateArray)

        }
        /*
                class ConstantAcceleration : Kalman() {
                    override val name: String = "Kalman (constant acceleration)"

                    companion object {
                        val instance = ConstantAcceleration()
                    }

                    private fun createAMatrix(dt: Long, x0: RealVector): RealMatrix {
                        val v0x = x0.vx
                        val v0y = x0.vy
                        val a0x = x0.ax
                        val a0y = x0.ay
                        return Array2DRowRealMatrix(
                            arrayOf(
                                doubleArrayOf(1.0, 0.0, v0x * dt, 0.0, a0x * dt * dt / 2, 0.0, 0.0),
                                doubleArrayOf(1.0, 0.0, 0.0, v0y * dt, 0.0, a0y * dt * dt / 2, 0.0),
                                doubleArrayOf(0.0, 0.0, 1.0, 0.0, a0x * dt, 0.0, 0.0),
                                doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, v0y * dt, 0.0),
                                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                            )
                        )
                    }

                    // 7 dimensions identity matrix
                    private fun createBMatrix(): RealMatrix = Array2DRowRealMatrix(
                        arrayOf(
                            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                        )
                    )

                    // org.apache.commons.math3.filter.KalmanFilter
                    // makes the same assumption if p0 is null
                    private fun createP0Matrix(): RealMatrix =
                        Array2DRowRealMatrix(q.data, true)

                    /**
                     * https://github.com/dsame/kalman-2d/blob/main/README.md#process-noise-covariance-matrix
                     * Creates a process noise covariance matrix for the given latitude.
                     * While the process noise is constant but it unit changes with latitude.
                     * @param cl The cosine of the latitude.
                     * @return The process noise covariance matrix.
                     */
                    private fun createQMatrix(cl: Double): RealMatrix = Array2DRowRealMatrix(
                        arrayOf(
                            /*
                            doubleArrayOf(4.0.longDg(cl), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 4.0.latDg, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 4.0.longDg(cl), 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 4.0.latDg, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 10.0.longDg(cl), 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 10.0.latDg, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                           */
                            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                        )
                    )

                    /**
                     * Creates a process model for the given accuracy.
                     * @param dt The time difference between measurements.
                     * @param cl The cosine of the latitude.
                     * @param x The initial state.
                     * @return The process model.
                     */
                    private fun createProcessModel(dt: Long, cl: Double, x0: RealVector): ProcessModel {

                        a = createAMatrix(dt, x0)

                        b = createBMatrix()

                        q = createQMatrix(cl)

                        p0 = createP0Matrix()
                        return DefaultProcessModel(a, b, q, x0, p0)
                    }

                    /**
                     * Creates a Measurement noise covariance matrix for the given accuracy.
                     * @param dt The time difference between measurements.
                     * @param cl The cosine of the latitude.
                     * @param accuracy The accuracy of the GPS sensor
                     */
                    private fun createRMatrix(dt: Long, cl: Double, accuracy: Float): Array<DoubleArray> {
                        // https://github.com/dsame/kalman-2d/blob/main/README.md#measurement-noise-covariance-matrix
                        val xyV = accuracy * accuracy
                        val xyVlat = xyV.latDg
                        val xyVlong = xyV.longDg(cl)
                        return arrayOf(
                            doubleArrayOf(xyVlong.V, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, xyVlat.V, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, xyVlong.vV(dt), 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, xyVlat.vV(dt), 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, xyVlong.aV(dt), 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, xyVlat.aV(dt), 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                        )
                    }

                    // https://github.com/dsame/kalman-2d/blob/main/README.md#measurement-matrix
                    /**
                     * Creates a measurement matrix which transforms the state vector into the measurement vector.
                     */
                    private fun createHMatrix(): Array<DoubleArray> {
                        return arrayOf(
                            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                        )
                    }


                    /**
                     * Creates a measurement model for the given accuracy.
                     * @param dt The time difference between measurements.
                     * @param cl The cosine of the latitude.
                     * @param accuracy The accuracy of the GPS sensor.
                     * @return The measurement model.
                     */
                    private fun createMeasurementModel(
                        @Suppress("SameParameterValue") dt: Long,
                        cl: Double,
                        accuracy: Float
                    ): MeasurementModel {
                        h = createHMatrix()
                        r = createRMatrix(dt, cl, accuracy)
                        return DefaultMeasurementModel(h, r)
                    }

                    private fun updateMatrix(dest: RealMatrix, src: Array<DoubleArray>) {
                        dest.data.forEachIndexed { i, row ->
                            row.forEachIndexed { j, _ ->
                                dest.setEntry(i, j, src[i][j])
                            }
                        }
                    }

                    private fun updateAMatrix(dt: Long, x0: DoubleArray) =
                        updateMatrix(pm.stateTransitionMatrix, createAMatrix(dt, x0))

                    private fun updateRMatrix(dt: Long, gpsUpdate: GPSUpdate) =
                        updateMatrix(
                            mm.measurementMatrix,
                            createRMatrix(
                                dt,
                                cos(Math.toRadians(km.stateEstimation.latitude)),
                                gpsUpdate.accuracy
                            )
                        )

                    private fun updateQMatrix(cl: Double) =
                        updateMatrix(pm.processNoise, createQMatrix(cl))

                    private var isKmInitialized: Boolean = false
                    fun reset(gpsUpdate: GPSUpdate, dt: Long = defaultDt) {
                        prevTimeStamp = gpsUpdate.ts
                        prevDt = defaultDt
                        val x = measurement0(gpsUpdate)
                        val cl = cos(Math.toRadians(x.latitude))

                        pm = createProcessModel(dt, cl, x)
                        mm = createMeasurementModel(dt, cl, gpsUpdate.accuracy)

                        km = KalmanFilter(pm, mm)
                        isKmInitialized = true
                    }

                    // TODO: filter out the same
                    override fun apply(gpsUpdate: GPSUpdate): GPSUpdate {
                        // TODO: ugly hack
                        if (!isKmInitialized) {
                            reset(gpsUpdate)
                            return gpsUpdate
                        }
                        val dt = gpsUpdate.ts - prevTimeStamp
                        val dtChanged = abs(dt - prevDt) > 500
                        prevDt = dt
                        prevTimeStamp = gpsUpdate.ts
                        val z = measurement(gpsUpdate)
                        val x0 = km.stateEstimation

                        if (dtChanged) {
                            updateAMatrix(dt, x0)
                        }

                        val preCosLatitude = cos(Math.toRadians(x0.latitude))
                        val cosLatitude = cos(Math.toRadians(z.latitude))
                        val prevLong1DegreeLength = 1.0.longDg(preCosLatitude)
                        val long1DegreeLength = 1.0.longDg(cosLatitude)

                        val long1DegreeChanged = abs(prevLong1DegreeLength - long1DegreeLength) > 1
                        if (long1DegreeChanged) {
                            updateQMatrix(cosLatitude)
                        }

                        km.predict()

                        if (dtChanged || long1DegreeChanged) {
                            updateRMatrix(dt, gpsUpdate)
                        }

                        km.correct(z)

                        val x = km.stateEstimation
                        return GPSUpdate(
                            x.latitude,
                            x.longitude,
                            moduleDegreesToMeters(x.vx, x.vy, cosLatitude).toFloat(),
                            gpsUpdate.accuracy,
                            Math.toDegrees(x.bearing).toFloat(),
                            gpsUpdate.ts
                        )
                    }
                }
         */
    }
}