package s4y.demo.mapsdksdemo.mapbox

import android.app.Activity
import com.mapbox.common.MapboxOptions
import s4y.demo.mapsdksdemo.map.MapId
import s4y.demo.mapsdksdemo.map.dependencies.IMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapFactory
import s4y.demo.mapsdksdemo.map.dependencies.IMapState

class MapboxMapFactory(mapBoxAccessToken: String) : IMapFactory {
    init {
        MapboxOptions.accessToken = mapBoxAccessToken
    }
    override val mapId: MapId = MapboxMap.mapType

    override val name: String = "Mapbox"

    override fun close() {
    }

    override fun newMap(
        activity: Activity,
        initialState: IMapState
    ): IMap = MapboxMap(activity, initialState)
}