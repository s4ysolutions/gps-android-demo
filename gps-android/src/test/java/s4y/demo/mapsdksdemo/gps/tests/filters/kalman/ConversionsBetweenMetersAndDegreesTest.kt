package s4y.demo.mapsdksdemo.gps.tests.filters.kalman

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.assertEquals

class ConversionsBetweenMetersAndDegreesTest {
    /*
    @Test
    fun latDg_shouldBeInvertible() {
        // Arrange
        val lengthD = 1.0 // degree
        val expectedLengthM = mPerDegree.toDouble()
        // Act
        val lengthM = lengthD.latMeters
        val lengthDHat = lengthM.latDg
        // Assert
        assertEquals(expectedLengthM, lengthM)
        assertEquals(lengthD, lengthDHat)
    }
    @Test
    fun longDg_shouldBeInvertible() {
        // Arrange
        val latitude = 45.0
        val cosLatitude = cos(Math.toRadians(latitude))
        val lengthD = 1.0 // degree
        val expectedLengthM = mPerDegree * cosLatitude // degrees become shorter as latitude increases
        // Act
        val lengthM = lengthD.longMeters(cosLatitude)
        val lengthDHat = lengthM.longDg(cosLatitude)
        // Assert
        assertThat(expectedLengthM).isWithin(1e-6).of(lengthM)
        assertThat(lengthD).isWithin(1e-6).of(lengthDHat)
    }

    @Test
    fun velocity_shouldBeInvertible() {
        // Arrange
        val latitude = 45.256733
        val cosLatitude = cos(Math.toRadians(latitude))
        val v = 1.0f // 1m/s
        val bearing = 15.0f
        val cosBearing = cos(Math.toRadians(bearing.toDouble()))
        val sinBearing = sin(Math.toRadians(bearing.toDouble()))
        // Act
        val vx = v.toX(cosBearing).longDg(cosLatitude)
        val vy = v.toX(sinBearing).latDg
        val vHat = GPSFilter.Kalman.moduleDegreesToMeters(vx, vy, cosLatitude)
        // Assert
        assertThat(vHat).isWithin(1.0E-6F).of(v)
    }

    @Test
    fun velocityProjected_shouldBeInvertible() {
        // Arrange
        val latitude0 = 45.0
        val longitude0 = 20.0
        val cosLatitude = cos(Math.toRadians(latitude0))
        val bearing = 15.0f
        val velocity = mPerDegree // 1degree/s
        // Act
        // velocity projected to x in degrees
        val cosBearing = cos(Math.toRadians(bearing.toDouble()))
        val vx = velocity.toX(cosBearing).longDg(KalmanFilterTest.cosLatitude).toDouble()

        // velocity projected to y in degrees
        val sinBearing = sin(Math.toRadians(bearing.toDouble()))
        val vy = velocity.toY(sinBearing).latDg.toDouble()

        // Assert
        // restore velocity from projected x and y
        assertThat(moduleDegreesToMeters(vx, vy, cosLatitude).toFloat()).isWithin(1.0F).of(velocity)
    }
     */
}