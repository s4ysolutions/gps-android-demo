package s4y.demo.mapsdksdemo.gps.filters.kalman

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import s4y.demo.mapsdksdemo.gps.GPSUpdate

class TransitionTest {
    @Test
    fun transition_shouldCalculateDt() {
        // Arrange
        val transition = GPSFilterKalman.Transition()
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
        val transition = GPSFilterKalman.Transition()
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
            GPSUpdate(0.0, 0.0, 0.0f, 2.0f, 0.0, 5000)
        )
        // Assert
        assertFalse(accuracy0)
        assertFalse(accuracy1)
        assertFalse(accuracy2)
        assertFalse(accuracy2)
    }

}