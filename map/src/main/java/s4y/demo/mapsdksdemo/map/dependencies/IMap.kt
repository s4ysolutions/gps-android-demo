package s4y.demo.mapsdksdemo.map.dependencies

import android.view.View
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ILayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import java.io.Closeable

interface IMap: Closeable {
    val view: View
    val state: IMapState
    val currentGPSPositionLayer: ICurrentGPSPositionLayer
    val trackLayers: ILayersContainer<ITrackLayer>
}