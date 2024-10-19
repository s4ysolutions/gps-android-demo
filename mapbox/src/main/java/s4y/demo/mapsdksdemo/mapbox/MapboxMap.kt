package s4y.demo.mapsdksdemo.mapbox

import android.app.Activity
import android.view.ViewGroup
import com.mapbox.common.Cancelable
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView

import s4y.demo.mapsdksdemo.map.MapId
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.BaseMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapState
import s4y.demo.mapsdksdemo.map.dependencies.IMapStateMutable
import s4y.demo.mapsdksdemo.map.dependencies.layers.BaseLayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import s4y.demo.mapsdksdemo.mapbox.layers.MapBoxTrackLayer
import s4y.demo.mapsdksdemo.mapbox.layers.MapboxCurrentGPSPositionLayer

class MapboxMap(activity: Activity, initialState: IMapState) : BaseMap() {
    companion object {
        const val mapType = "mapbox"
    }

    override val id: MapId
        get() = mapType

    override val view: MapView = MapView(activity).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private var _gpsPositionLayer: MapboxCurrentGPSPositionLayer? = null
    override val currentGPSPositionLayer: ICurrentGPSPositionLayer
        get() = _gpsPositionLayer
            ?: MapboxCurrentGPSPositionLayer(view).also {
                _gpsPositionLayer = it
            }

    override val trackLayers = object : BaseLayersContainer<ITrackLayer>() {
        override fun createDefaultLayer(): ITrackLayer {
            return MapBoxTrackLayer(view)
        }
    }

    override val state: IMapStateMutable = object : IMapStateMutable {
        override var center: MapPosition
            get() = view.mapboxMap.cameraState.center.toMapPosition()
            set(value) {
                view.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(com.mapbox.geojson.Point.fromLngLat(value.longitude, value.latitude))
                        .zoom(view.mapboxMap.cameraState.zoom)
                        .build()
                )
            }
        override var zoom: Int
            get() = view.mapboxMap.cameraState.zoom.toInt()
            set(value) {
                view.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(view.mapboxMap.cameraState.center)
                        .zoom(value.toDouble())
                        .build()
                )
            }
    }

    private val onCameraChangeCancelable: Cancelable
    init {
        state.center = initialState.center
        state.zoom = initialState.zoom

        // TODO: order by timestamp?
        onCameraChangeCancelable = view.mapboxMap.subscribeCameraChanged{
            notifyStateChangeListeners(state)
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun close() {
        onCameraChangeCancelable.cancel()
    }
}