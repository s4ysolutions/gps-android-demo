package s4y.demo.mapsdksdemo.map.dependencies.layers

import s4y.demo.mapsdksdemo.map.MapPosition

interface ITrackLayer: ILayer {
    fun setPositions(positions: Array<MapPosition>)
    fun addPosition(position: MapPosition)
}