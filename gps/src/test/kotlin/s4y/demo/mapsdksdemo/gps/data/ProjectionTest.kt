package s4y.demo.mapsdksdemo.gps.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class ProjectionTest {
    @Test
    fun x_shouldBeProjectedAtEquator() {
        val x = Projection.X.fromMeters( 1.0, Units.Bearing.fromDegrees(45.0))
        assertEquals(sqrt(2.0) / 2, x.meters)
    }

    @Test
    fun y_shouldBeProjectedAtEquator() {
        val y = Projection.Y.fromMeters(1.0, Units.Bearing.fromDegrees(45.0))
        assertEquals(sqrt(2.0) / 2, y.meters, 1e-7)
    }
}