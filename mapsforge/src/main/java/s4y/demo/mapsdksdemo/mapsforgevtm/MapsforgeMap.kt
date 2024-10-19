package s4y.demo.mapsdksdemo.mapsforgevtm

import android.app.Activity
import android.os.Handler
import android.os.Looper
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import org.mapsforge.map.view.InputListener
import s4y.demo.mapsdksdemo.map.MapId
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.BaseMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapState
import s4y.demo.mapsdksdemo.map.dependencies.IMapStateMutable
import s4y.demo.mapsdksdemo.map.dependencies.clone
import s4y.demo.mapsdksdemo.map.dependencies.layers.BaseLayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import s4y.demo.mapsdksdemo.mapsforge.maps.MapsforgeMaps
import s4y.demo.mapsdksdemo.mapsforgevtm.layers.MapsforgeCurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.mapsforgevtm.layers.MapsforgeTrackLayer
import java.io.FileInputStream

class MapsforgeMap(activity: Activity, initialState: IMapState) : BaseMap() {
    companion object {
        const val mapType = "mapsforge"
    }

    override val id: MapId
        get() = mapType

    override val view: MapView = object : MapView(activity) {
        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            destroyAll()
        }
    }

    override val state: IMapStateMutable = object : IMapStateMutable {
        override var center: MapPosition
            get() = view.model.mapViewPosition.center.mapPosition
            set(value) {
                view.setCenter(value.latLong)
            }

        override var zoom: Int
            get() =
                view.model.mapViewPosition.zoomLevel.toInt()
            set(value) {
                view.setZoomLevel(value.toByte())
            }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    private var _gpsPositionLayer: MapsforgeCurrentGPSPositionLayer? = null
    override val currentGPSPositionLayer: ICurrentGPSPositionLayer
        get() =
            _gpsPositionLayer
                ?: MapsforgeCurrentGPSPositionLayer(LatLong(0.0, 0.0), 0F)
                    .also {
                        _gpsPositionLayer = it
                        view.layerManager.layers.add(it.layer)
                    }

    override val trackLayers = object : BaseLayersContainer<ITrackLayer>() {

        override fun addLayer(layer: ITrackLayer) {
            super.addLayer(layer)
            view.layerManager.layers.add((layer as MapsforgeTrackLayer).layer)
        }

        override fun removeLayer(layer: ITrackLayer) {
            view.layerManager.layers.remove((layer as MapsforgeTrackLayer).layer)
            super.removeLayer(layer)
        }

        override fun createDefaultLayer(): ITrackLayer {
            return MapsforgeTrackLayer(view)
        }
    }

    private val handlerNotify = Handler(Looper.getMainLooper())
    private val runnableNotifyStateChanged = Runnable {
        notifyStateChangeListeners(state)
    }

    init {
        state.center = initialState.center
        state.zoom = initialState.zoom

        view.model.mapViewPosition.addObserver {
            handlerNotify.removeCallbacks(runnableNotifyStateChanged)
            handlerNotify.postDelayed(runnableNotifyStateChanged, 500)
        }

        view.addInputListener(object : InputListener {
            private fun notifyStateChange() {
                notifyStateChangeListeners(state)
            }

            override fun onMoveEvent() {
                notifyStateChangeListeners(state)
            }

            override fun onZoomEvent() {
                notifyStateChangeListeners(state)
            }
        })

        view.mapScaleBar.isVisible = true
        view.setBuiltInZoomControls(false)

        val tileCache = AndroidUtil.createTileCache(
            activity, "mapcache",
            view.model.displayModel.tileSize, 1f,
            view.model.frameBufferModel.overdrawFactor
        )

        val mapDataStore: MapDataStore =
            MapFile(FileInputStream(MapsforgeMaps().defaultMapFile(activity)))
        val tileRendererLayer = TileRendererLayer(
            tileCache, mapDataStore,
            view.model.mapViewPosition, AndroidGraphicFactory.INSTANCE
        )
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
        view.layerManager.layers.add(tileRendererLayer)
    }

    override fun close() {
    }
}