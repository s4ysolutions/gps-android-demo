package s4y.demo.mapsdksdemo.gps.tests.filters.kalman

import kotlin.math.cos


class KalmanFilterTest {
    companion object {
        const val latitude0 = 45.0
        const val longitude0 = 20.0
        val cosLatitude = cos(Math.toRadians(latitude0))
    }
/*
    @Test
    fun reset_shouldNotCrash() {
        // Arrange
        val filter = GPSFilter.Kalman.ConstantAcceleration.instance
        val ts = System.currentTimeMillis()
        try {
            filter.reset(GPSUpdate(latitude0, longitude0, 1.0f, 5.0f, 15.0f, ts))
        } catch (e: Exception) {
            fail("reset should not crash")
        }
    }


    @Test
    fun reset_shouldProvideFirstEstimation() {
        // Arrange
        val ts = System.currentTimeMillis()
        val bearing = 15.0f
        val cosBearing = cos(Math.toRadians(bearing.toDouble()))
        val sinBearing = sin(Math.toRadians(bearing.toDouble()))
        val velocity = mPerDegree // 1degree/s
        val vx = velocity.toX(cosBearing).longDg(cosLatitude).toDouble()
        val vy = velocity.toY(sinBearing).latDg.toDouble()
        val update = GPSUpdate(latitude0, longitude0, velocity, 5.0f, bearing, ts)
        val filter = GPSFilter.Kalman.ConstantAcceleration.instance
        filter.reset(update)
        // Act
        val estimation = filter.km.stateEstimation
        // Assert
        assertThat(estimation.latitude).isWithin(1e-6).of(latitude0)
        assertThat(estimation.longitude).isWithin(1e-6).of(longitude0)
        assertThat(estimation.vx).isWithin(1e-6).of(vx)
        assertThat(estimation.vy).isWithin(1e-6).of(vy)
        assertThat(
            moduleDegreesToMeters(
                estimation.vx,
                estimation.vy,
                cosLatitude
            ).toFloat()
        ).isWithin(1F).of(velocity)
        assertThat(estimation.ax).isEqualTo(0.0)
        assertThat(estimation.ay).isEqualTo(0.0)
    }


    private fun checkMatrixForSymmetry(matrix: Array2DRowRealMatrix) {
        val n = matrix.rowDimension
        for (i in 0 until n) {
            for (j in 0 until n) {
                val diff = FastMath.abs(matrix.getEntry(i, j) - matrix.getEntry(j, i))
                assertThat(diff).isLessThan(1e-6)
            }
        }
    }

    /*
        private fun checkMatrixForSymmetry(matrix: RealMatrix) {
            val n = matrix.rowDimension
            for (i in 0 until n) {
                for (j in 0 until n) {
                    assertThat(matrix.getEntry(i, j)).isEqualTo(matrix.getEntry(j, i))
                }
            }
        }
    */
    @Test
    fun reset_shouldCreateSymmetricMatrix() {
        // Arrange
        val filter = GPSFilter.Kalman.ConstantAcceleration.instance
        val ts = System.currentTimeMillis()
        val update = GPSUpdate(latitude0, longitude0, 1.0f, 5.0f, 15.0f, ts)
        filter.reset(update)
        // Act & Assert
        val measurementModel = filter.mm
        val errorCovariance = filter.km.errorCovarianceMatrix
        val measurementMatrix = filter.mm.measurementMatrix
        val measurementMatrixT = measurementMatrix.transpose()

        val ec = Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(104.4766206708, 0.0000508989, 0.0764821242, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0000508989, 2.652118205, 0.0, 0.0102466622, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0695292038, 0.0, 0.0001017978, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0093151475, 0.0, 6.6301502698, 0.0, 0.0256166555, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0002544946, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0232878687, 0.0, 0.0001799548, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0)
            )
        )
        /*
// S = H * P(k) * H' + R
val s: RealMatrix = measurementMatrix.multiply(errorCovariance)
    .multiply(measurementMatrixT)
    .add(measurementModel.measurementNoise)
         */
        checkMatrixForSymmetry(ec)
        //val ch = CholeskyDecomposition(s)
    }

    @Test
    fun apply_shouldNotCrash() {
        // Arrange
        val ts = System.currentTimeMillis()
        val dt = 1000L // 1 second
        val bearing = 15.0f
        val cosBearing = cos(Math.toRadians(bearing.toDouble()))
        val sinBearing = sin(Math.toRadians(bearing.toDouble()))
        val velocity = mPerDegree // 1degree/s
        val vx = velocity.toX(cosBearing).longDg(cosLatitude).toDouble()
        val vy = velocity.toY(sinBearing).latDg.toDouble()
        val update = GPSUpdate(latitude0, longitude0, velocity, 5.0f, bearing, ts)
        val filter = GPSFilter.Kalman.ConstantAcceleration.instance
        filter.reset(update, dt)
        // move to the next point
        val longitude1 = longitude0 + vx * dt + vx * 1e-3
        val latitude1 = latitude0 + vy * dt + vy * 1e-3
        val vx1 = vx
        val vy1 = vy
        val update1 = GPSUpdate(latitude1, longitude1, velocity, 5.0f, bearing, ts + dt + 100)
        // Act & Assert
        try {
            filter.apply(update1)
        } catch (e: Exception) {
            fail("apply should not crash")
        }
    }
 */
}