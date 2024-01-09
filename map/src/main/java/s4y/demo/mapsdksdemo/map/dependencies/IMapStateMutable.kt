package s4y.demo.mapsdksdemo.map.dependencies

import s4y.demo.mapsdksdemo.map.MapPosition
import java.io.Serializable

interface IMapState : Serializable {
    companion object {
        val empty: IMapState = object : IMapState {
            override val center: MapPosition = MapPosition(0.0, 0.0)
            override val zoom: Int = 8
        }
    }
    val center: MapPosition
    val zoom: Int
}

fun IMapState.clone(): IMapState {
    // cloning position is precaution against further changes
    // in MapPosition class which might make it mutable
    val position =  MapPosition(this.center.latitude, this.center.longitude)
    val zoom = this.zoom
    return object : IMapState, Serializable {
        override val center = position
        override val zoom = zoom
    } as IMapState
}

interface IMapStateMutable : IMapState {
    override var center: MapPosition
    override var zoom: Int
}

val IMapState.isEmpty: Boolean
    get() = this == IMapState.empty