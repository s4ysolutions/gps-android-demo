package s4y.demo.mapsdksdemo.map.dependencies.layers

import s4y.demo.mapsdksdemo.map.MapPosition

typealias Accuracy = Float
interface ICurrentGPSPositionLayer: ILayer {
    fun updatePosition(position: MapPosition, accuracy: Accuracy)
}