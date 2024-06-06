package s4y.demo.mapsdksdemo.gps.filters.kalman.gpsfilterkalman

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalman

class ResetTest : Fixtures() {
    @ParameterizedTest(name = "{0}.reset should reset to defaults")
    @MethodSource("provideAllFilters")
    fun reset_shouldReset(filter: GPSFilterKalman) {
        // Arrange
        // Act
        filter.reset()
        // Assert
        assertEquals(0.0, filter.transition.dtSec)
        assertEquals(0.0, filter.transition.latitude.degrees)
        assertEquals(0.0, filter.transition.longitude.degrees)
        assertEquals(0.0, filter.transition.bearingDegrees)
        assertEquals(0, filter.transition.count )
    }
}