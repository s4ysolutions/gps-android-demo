package s4y.demo.mapsdksdemo.appstate

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.appstate.preferences.BasePreference
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import s4y.demo.mapsdksdemo.gps.filters.GPSFilters
import kotlin.reflect.KProperty

class GPSFilterPreference(context: Context) {
    private val filters = GPSFilters.instance

    private val preference = object : BasePreference<Int>(context) {
        override val key: String = "gps_filter"
        override val default: Int = 0
    }

    private val proximity = object : BasePreference<Float>(context) {
        override val key: String = "gps_filter_proximity"
        override val default: Float = filters.proximity?.proximity ?: 0.0f
    }

    fun get(): GPSFilter = preference.get<Int>().let {
        if (it == 0)
            filters.default
        else try {
            val filter = filters[it]
            if (filter is GPSFilter.Proximity) {
                filter.proximity = proximity.get()
            }
            filter
        } catch (e: IndexOutOfBoundsException) {
            filters.default
        }
    }

    fun set(filter: GPSFilter) {
        preference.set(filters.indexOf(filter))
        if (filter is GPSFilter.Proximity) {
            proximity.set(filter.proximity)
        }
        mutableState.value = filter
    }


    private val mutableState = MutableStateFlow(get())
    fun asStateFlow(): StateFlow<GPSFilter> = mutableState

    operator fun getValue(companion: Any, property: KProperty<*>): GPSFilter = get()
    operator fun setValue(companion: Any, property: KProperty<*>, gpsFilter: GPSFilter) =
        set(gpsFilter)

}