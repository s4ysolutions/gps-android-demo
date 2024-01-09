package s4y.demo.mapsdksdemo.gps.filters

/**
 * 1 based A list of GPS filters.
 *
 * @property instances The list of filters.
 */
class GPSFilters private constructor(
    private val instances: List<GPSFilter> = listOf(
        GPSFilter.Null.instance,
        GPSFilter.Proximity(0.0f),
        GPSFilter.Kalman.instance
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

    val proximity: GPSFilter.Proximity?
        get() =
            instances.firstOrNull { it is GPSFilter.Proximity }
                    as? GPSFilter.Proximity?

    override fun indexOf(element: GPSFilter): Int =
        instances.indexOf(element) + 1

    // TODO: override forEachIndexed to be 1 based

}
