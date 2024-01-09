package s4y.demo.mapsdksdemo.map.dependencies

import s4y.demo.mapsdksdemo.map.MapPosition
import java.io.Serializable

interface IMapState: Serializable {
    companion object {
        val empty: IMapState = object: IMapState {
            override var center: MapPosition = MapPosition(0.0, 0.0)
            override var zoom: Int = 8
        }
    }
    var center: MapPosition
    var zoom: Int
}

val IMapState.isEmpty: Boolean
    get() = this == IMapState.empty