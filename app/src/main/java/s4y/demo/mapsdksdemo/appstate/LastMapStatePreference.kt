package s4y.demo.mapsdksdemo.appstate

import android.content.Context
import s4y.demo.mapsdksdemo.appstate.preferences.SerializedPreference
import s4y.demo.mapsdksdemo.di.Di
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.IMapState
import s4y.demo.mapsdksdemo.map.dependencies.IMapStateMutable
import kotlin.reflect.KProperty

class MapStatePreferenceWrapper(override val center: MapPosition, override val zoom: Int) :
    IMapState {
    constructor(state: IMapState) : this(state.center, state.zoom)
    }

private val defaultMapState = MapStatePreferenceWrapper(
    MapPosition(45.256733, 19.834251), 12
)

class LastMapStatePreference(context: Context) :
    SerializedPreference<IMapState>(context) {
    override val key: String = "last_map_state"
    override val default = defaultMapState

    override fun set(v: IMapState) {
        super.set(MapStatePreferenceWrapper(v))
    }
}