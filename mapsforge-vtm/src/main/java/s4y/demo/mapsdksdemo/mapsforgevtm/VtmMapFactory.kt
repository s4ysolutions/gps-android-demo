package s4y.demo.mapsdksdemo.mapsforgevtm

import android.app.Activity
import s4y.demo.mapsdksdemo.map.MapId
import s4y.demo.mapsdksdemo.map.dependencies.IMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapFactory
import s4y.demo.mapsdksdemo.map.dependencies.IMapState

// TODO: cache lifecycle should be bigger than activity lifecycle
class VtmMapFactory() : IMapFactory {
    override val mapId: MapId = VtmMap.mapType
    override val name: String = "Mapsforge VTM"

    override fun newMap(activity: Activity, initialState: IMapState): IMap =
        VtmMap(activity, initialState)

    override fun close() {
    }
}