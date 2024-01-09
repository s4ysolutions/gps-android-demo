package s4y.demo.mapsdksdemo.appstate

import android.content.Context
import androidx.compose.runtime.Composable
import s4y.demo.mapsdksdemo.appstate.preferences.SerializedPreference
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.IMapState

private val defaultMapState = object : IMapState {
    override var center: MapPosition = MapPosition(45.256733, 19.834251)
    override var zoom: Int = 12
}

class LastMapStatePreference(context: Context) : SerializedPreference<IMapState>(context) {
    override val key: String = "last_map_state"
    override val default: IMapState = defaultMapState
}