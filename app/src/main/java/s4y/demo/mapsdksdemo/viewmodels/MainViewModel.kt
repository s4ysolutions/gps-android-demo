package s4y.demo.mapsdksdemo.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import s4y.demo.mapsdksdemo.appstate.LastMapStatePreference
import s4y.demo.mapsdksdemo.di.Di
import s4y.demo.mapsdksdemo.gps.filters.GPSFilters
import s4y.demo.mapsdksdemo.map.dependencies.IMap

class MainViewModel : ViewModel() {
    val gpsManager = Di.gpsUpdatesManager
    private val mapViewManager = Di.mapsManager

    private var _map: IMap? = null
    val map: IMap
        get() = _map!!
    val filters = GPSFilters.instance

    fun initMap(activity: Activity) {
        _map = mapViewManager.defaultMapFactory.newMap(
            activity,
            LastMapStatePreference(activity).get()
        )
    }
}