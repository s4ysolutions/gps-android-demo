package s4y.demo.mapsdksdemo.map.dependencies

import android.view.View
import s4y.demo.mapsdksdemo.map.MapId
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ILayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import java.io.Closeable

interface IMap: Closeable {
    val id: MapId
    val view: View
    val currentGPSPositionLayer: ICurrentGPSPositionLayer
    val trackLayers: ILayersContainer<ITrackLayer>
    val state: IMapStateMutable
    fun addStateChangeListener(listener: (IMapState) -> Unit)
    fun removeStateChangeListener(listener: (IMapState) -> Unit)

    fun pause()
    fun resume()
}