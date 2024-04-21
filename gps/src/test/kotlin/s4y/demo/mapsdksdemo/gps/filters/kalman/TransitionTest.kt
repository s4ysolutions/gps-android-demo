package s4y.demo.mapsdksdemo.gps.filters.kalman

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import kotlin.math.sqrt

class TransitionTest {
    @Test
    fun transition_shouldCalculateDt() {
        // Arrange
        val transition = Transition()
        // Act
        val dt0 = transition.dtSec
        val dtChanged0 = transition.dtChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 0.0f, 0.0, 1000)
        )
        val dt1 = transition.dtSec
        val dtChanged1 = transition.dtChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 0.0f, 0.0, 3000)
        )
        val dt2 = transition.dtSec
        val dtChanged2 = transition.dtChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 0.0f, 0.0, 5000)
        )
        val dt3 = transition.dtSec
        val dtChanged3 = transition.dtChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 0.0f, 0.0, 8000)
        )
        val dt4 = transition.dtSec
        val dtChanged4 = transition.dtChanged
        // Assert
        assertEquals(0.0, dt0)
        assertFalse(dtChanged0)
        assertEquals(0.0, dt1)
        assertFalse(dtChanged1)
        assertEquals(2.0, dt2)
        assertFalse(dtChanged2)
        assertEquals(2.0, dt3)
        assertFalse(dtChanged3)
        assertEquals(3.0, dt4)
        assertTrue(dtChanged4)
    }

    @Test
    fun transition_shouldCalculateAccuracy() {
        // Arrange
        val transition = Transition()
        // Act
        val accuracy0 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 1.0f, 0.0, 1000)
        )
        val accuracy1 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 1.0f, 0.0, 3000)
        )
        val accuracy2 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 1.0f, 0.0, 5000)
        )
        val accuracy3 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 2.1f, 0.0, 5000)
        )
        val accuracy4 = transition.accuracyChanged
        // Assert
        assertFalse(accuracy0)
        assertFalse(accuracy1)
        assertFalse(accuracy2)
        assertFalse(accuracy3)
        assertTrue(accuracy4)
    }

    @Test
    fun transition_shouldCalculateAccuracyChangedAtFirst() {
        // Arrange
        val transition = Transition()
        // Act
        val accuracy0 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 1.0f, 0.0, 1000)
        )
        val accuracy1 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 2.1f, 0.0, 3000)
        )
        val accuracy2 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 1.0f, 0.0, 5000)
        )
        val accuracy3 = transition.accuracyChanged
        transition.setCurrentState(
            GPSUpdate(0.0, 0.0, 0.0f, 1.0f, 0.0, 5000)
        )
        val accuracy4 = transition.accuracyChanged
        // Assert
        assertFalse(accuracy0)
        assertFalse(accuracy1)
        assertTrue(accuracy2)
        assertTrue(accuracy3)
        assertFalse(accuracy4)
    }
    @Test
    fun transition_shouldTrackMeters() {
        // Arrange
        val transition = Transition()
        val lat0 = Units.Latitude.fromDegrees(45.0)
        val lon0 = Units.Longitude.fromDegrees(45.0, lat0)
        // Act
        transition.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        val x = transition.metersX
        val y = transition.metersY
        // Assert
        assertEquals(lon0.meters, x)
        assertEquals(lat0.meters, y)
    }

    @Test
    fun transition_shouldTrackLongitudeLatitude() {
        // Arrange
        val transition = Transition()
        val lat0 = Units.Latitude.fromDegrees(45.0)
        val lon0 = Units.Longitude.fromDegrees(45.0, lat0)
        // Act
        transition.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        val lat1 = transition.latitude
        val lon1 = transition.longitude
        val x1 = transition.metersX
        val y1 = transition.metersY

        transition.setCurrentState(
            GPSUpdate(lat0.degrees + 1, lon0.degrees + 1, 0.0f, 1.0f, 0.0, 1000)
        )
        val lat2 = transition.latitude
        val lon2 = transition.longitude
        val x2 = transition.metersX
        val y2 = transition.metersY

        // Assert
        assertEquals(lat0.degrees, lat1.degrees)
        assertEquals(lon0.degrees, lon1.degrees)
        assertEquals(lat1.meters, y1)
        assertEquals(lon1.meters, x1)

        assertEquals(lat0.degrees + 1, lat2.degrees)
        assertEquals(lon0.degrees + 1, lon2.degrees)
        assertEquals(lat2.meters, y2)
        assertEquals(lon2.meters, x2)
    }
    @Test
    fun transition_shouldCalculateProjections() {
        // Arrange
        val transition = Transition()
        val lat1 = Units.Latitude.fromDegrees(45.0)
        val lon1 = Units.Longitude.fromDegrees(45.0, lat1)
        val lat2 = Units.Latitude.fromDegrees(46.0)
        val lon2 = Units.Longitude.fromDegrees(46.0, lat2)
        // Act
        val x0 = transition.metersX
        val y0 = transition.metersY
        transition.setCurrentState(
            GPSUpdate(lat1.degrees, lon1.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        val x1 = transition.metersX
        val y1 = transition.metersY
        transition.setCurrentState(
            GPSUpdate(lat2.degrees, lon2.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val x2 = transition.metersX
        val y2 = transition.metersY

        // Assert
        assertEquals(0.0, x0)
        assertEquals(0.0, y0)
        assertEquals(lon1.meters, x1)
        assertEquals(lat1.meters, y1)
        assertEquals(lon2.meters, x2)
        assertEquals(lat2.meters, y2)
    }

    @Test
    fun transition_shouldCalculateVelocity() {
        // Arrange
        val transition = Transition()
        val lat0 = Units.Latitude.fromDegrees(45.0)
        val lon0 = Units.Longitude.fromDegrees(45.0, lat0)
        val lat1 = Units.Latitude.fromDegrees(46.0)
        val lon1 = Units.Longitude.fromDegrees(46.0, lat1)
        // Act
        transition.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        val velocity1 = transition.velocity
        transition.setCurrentState(
            GPSUpdate(lat1.degrees, lon1.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val velocity2 = transition.velocity
        // Assert
        assertEquals(0.0, velocity1.mPerSec)

        val dx = lon1.meters - lon0.meters
        assertEquals(dx / 2.0, velocity2.x.meters)
        val dy = lat1.meters - lat0.meters
        assertEquals(dy / 2.0, velocity2.y.meters)
        val distance = sqrt(dx * dx + dy * dy)
        val velocity = (distance / 2.0)
        assertEquals(velocity, velocity2.mPerSec, 1e-2)
    }
    @Test
    fun transition_shouldCalculateBearing() {
        // Arrange
        val lat0 = Units.Latitude.fromDegrees(0.0)
        val lon0 = Units.Longitude.fromDegrees(0.0, lat0)

        val latN = Units.Latitude.fromDegrees(1.0)
        val lonN = Units.Longitude.fromDegrees(0.0, latN)

        val latNE = Units.Latitude.fromDegrees(1.0)
        val lonNE = Units.Longitude.fromDegrees(1.0, latNE)

        val latE = Units.Latitude.fromDegrees(0.0)
        val lonE = Units.Longitude.fromDegrees(1.0, latNE)

        val latSE = Units.Latitude.fromDegrees(-1.0)
        val lonSE = Units.Longitude.fromDegrees(1.0, latNE)

        val latS = Units.Latitude.fromDegrees(-1.0)
        val lonS = Units.Longitude.fromDegrees(0.0, latNE)

        val latSW = Units.Latitude.fromDegrees(-1.0)
        val lonSW = Units.Longitude.fromDegrees(-1.0, latNE)

        val latW = Units.Latitude.fromDegrees(0.0)
        val lonW = Units.Longitude.fromDegrees(-1.0, latNE)

        val latNW = Units.Latitude.fromDegrees(1.0)
        val lonNW = Units.Longitude.fromDegrees(-1.0, latNE)
        // Act
        val transitionN = Transition()
        transitionN.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionN.setCurrentState(
            GPSUpdate(latN.degrees, lonN.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingN = transitionN.bearingDegrees

        val transitionNE = Transition()
        transitionNE.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionNE.setCurrentState(
            GPSUpdate(latNE.degrees, lonNE.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingNE = transitionNE.bearingDegrees

        val transitionE = Transition()
        transitionE.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionE.setCurrentState(
            GPSUpdate(latE.degrees, lonE.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingE = transitionE.bearingDegrees

        val transitionSE = Transition()
        transitionSE.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionSE.setCurrentState(
            GPSUpdate(latSE.degrees, lonSE.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingSE = transitionSE.bearingDegrees

        val transitionS = Transition()
        transitionS.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionS.setCurrentState(
            GPSUpdate(latS.degrees, lonS.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingS = transitionS.bearingDegrees

        val transitionSW = Transition()
        transitionSW.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionSW.setCurrentState(
            GPSUpdate(latSW.degrees, lonSW.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingSW = transitionSW.bearingDegrees

        val transitionW = Transition()
        transitionW.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionW.setCurrentState(
            GPSUpdate(latW.degrees, lonW.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingW = transitionW.bearingDegrees

        val transitionNW = Transition()
        transitionNW.setCurrentState(
            GPSUpdate(lat0.degrees, lon0.degrees, 0.0f, 1.0f, 0.0, 1000)
        )
        transitionNW.setCurrentState(
            GPSUpdate(latNW.degrees, lonNW.degrees, 0.0f, 1.0f, 0.0, 3000)
        )
        val bearingNW = transitionNW.bearingDegrees
        // Assert
        assertEquals(0.0, bearingN)
        assertEquals(45.0, bearingNE, 1e-2)
        assertEquals(90.0, bearingE)
        assertEquals(135.0, bearingSE, 1e-2)
        assertEquals(180.0, bearingS)
        assertEquals(225.0, bearingSW, 1e-2)
        assertEquals(270.0, bearingW)
        assertEquals(315.0, bearingNW, 1e-2)
    }
}