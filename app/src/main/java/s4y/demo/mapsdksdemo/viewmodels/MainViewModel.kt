package s4y.demo.mapsdksdemo.viewmodels

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.di.Di
import s4y.demo.mapsdksdemo.lib.cast.toMapPosition
import s4y.demo.mapsdksdemo.map.dependencies.IMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapState

class MainViewModel : ViewModel() {
    val gpsUpdatesManager = Di.gpsUpdatesManager
    val gpsCurrentPositionManager = Di.gpsCurrentPositionManager
    val filters = Di.gpsFilters

    val mapsManager = Di.mapsManager
    private val persistedMapState by Di.lastMapStatePreference
    private val _mapStateFlow = MutableStateFlow<IMap?>(null)
    val mapStateFlow = _mapStateFlow as StateFlow<IMap?>
    private fun mapViewInputListener(state: IMapState) {
        Di.lastMapStatePreference.set(state)
    }

    fun setupMap(activity: Activity) {
        val m = mapsManager.current.newMap(activity, persistedMapState)
        with(gpsUpdatesManager.all) {
            val mapPositions = Array(snapshot.size) {
                snapshot[it].toMapPosition()
            }
            m.trackLayers.defaultLayer.setPositions(mapPositions)
        }
        m.addStateChangeListener(::mapViewInputListener)
        _mapStateFlow.value = m
    }

    override fun onCleared() {
        _mapStateFlow.value?.let {
            it.removeStateChangeListener(::mapViewInputListener)
            it.close()
        }
        _mapStateFlow.value = null
    }

    val filtersPopup = mutableStateOf(false)
    val mapsPopup = mutableStateOf(false)
    val saveAsFileActive = mutableStateOf(false)
}