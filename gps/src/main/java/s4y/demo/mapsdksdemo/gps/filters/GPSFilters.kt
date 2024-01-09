package s4y.demo.mapsdksdemo.gps.filters

import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantAcceleration
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantPosition
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantVelocity

/**
 * 1 based A list of GPS filters.
 *
 * @property instances The list of filters.
 */
class GPSFilters private constructor(
    private val instances: List<GPSFilter> = listOf(
        GPSFilterNull.instance,
        GPSFilterProximity.instance1m,
        GPSFilterProximity.instance5m,
        GPSFilterKalmanConstantPosition.instance,
        GPSFilterKalmanConstantVelocity.instance,
        GPSFilterKalmanConstantAcceleration.instance,
    )
) : List<GPSFilter> by instances {
    companion object {
        val instance = GPSFilters()
    }

    override operator fun get(index: Int): GPSFilter = if (index in 1..size) {
        instances.elementAt(index - 1)
    } else {
        throw IndexOutOfBoundsException()
    }

    val default = instances[0]

    val proximity: GPSFilterProximity?
        get() = instances.firstOrNull { it is GPSFilterProximity }
                as? GPSFilterProximity?

    override fun indexOf(element: GPSFilter): Int =
        instances.indexOf(element) + 1

    // TODO: override forEachIndexed to be 1 based

}
