package s4y.demo.mapsdksdemo.gps.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import s4y.demo.mapsdksdemo.gps.data.Units.Companion.mPer1DegreeEq
import s4y.demo.mapsdksdemo.gps.data.Units.Companion.mPer1DegreePolar
import kotlin.math.cos

class UnitsTest {
    companion object {
        val longitudeDegreeLength45 = mPer1DegreePolar * cos(Math.toRadians(45.0)) - 93.5 * cos(3 * Math.toRadians(45.0))
    }
    @Test
    fun metersLatitude_shouldBeConvertedToDegree() {
        val latitude = Units.Latitude.fromMeters(mPer1DegreeEq)
        assertEquals(1.0, latitude.degrees)
    }

    @Test
    fun metersPositiveLatitude_shouldBeConvertedToDegrees() {
        val latitude = Units.Latitude.fromMeters(45 * mPer1DegreeEq)
        assertEquals(45.0, latitude.degrees)
    }

    @Test
    fun metersNegativeLatitude_shouldBeConvertedToDegrees() {
        val latitude = Units.Latitude.fromMeters(-45 * mPer1DegreeEq)
        assertEquals(-45.0, latitude.degrees)
    }

    @Test
    fun degreePositiveLatitude_shouldBeConvertedToMeters() {
        val latitude = Units.Latitude.fromDegrees(1.0)
        assertEquals(mPer1DegreeEq, latitude.meters)
    }

    @Test
    fun degreesPositiveLatitude_shouldBeConvertedToMeters() {
        val latitude = Units.Latitude.fromDegrees(45.0)
        assertEquals(45 * mPer1DegreeEq, latitude.meters)
    }

    @Test
    fun degreesNegativeLatitude_shouldBeConvertedToMeters() {
        val latitude = Units.Latitude.fromDegrees(-45.0)
        assertEquals(-45 * mPer1DegreeEq, latitude.meters)
    }

    @Test
    fun metersLongitude_shouldBeConvertedToDegreeOnLatitude0() {
        val longitude = Units.Longitude.fromMeters(mPer1DegreeEq, Units.Latitude.fromDegrees(0.0))
        assertEquals(1.0, longitude.degrees)
    }

    @Test
    fun metersLongitude_shouldBeConvertedToDegreeOnLatitude45() {
        val longitude = Units.Longitude.fromMeters(
            longitudeDegreeLength45,
            Units.Latitude.fromDegrees(45.0)
        )
        assertEquals(1.0, longitude.degrees, 1e-7)
    }

    @Test
    fun degreeLongitude_shouldBeConvertedToMetersOnLatitude0() {
        val longitude = Units.Longitude.fromDegrees(1.0, Units.Latitude.fromDegrees(0.0))
        assertEquals(mPer1DegreeEq, longitude.meters)
    }

    @Test
    fun degreeLongitude_shouldBeConvertedToMetersOnLatitude45() {
        val longitude = Units.Longitude.fromDegrees(1.0, Units.Latitude.fromDegrees(45.0))
        assertEquals(longitudeDegreeLength45, longitude.meters)
    }
}