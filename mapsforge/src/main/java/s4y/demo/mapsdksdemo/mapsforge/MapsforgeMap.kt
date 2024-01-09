package s4y.demo.mapsdksdemo.mapsforge

import android.app.Activity
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.IMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapState
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ILayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import s4y.demo.mapsdksdemo.mapsforge.layers.MapsforgeCurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.mapsforge.layers.MapsforgeTrackLayer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MapsforgeMap(activity: Activity, initialState: IMapState) : IMap {
    companion object {
        const val mapType = "mapsforge"
    }

    override val view = object : MapView(activity) {
        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            destroyAll()
        }
    }

    private lateinit var _center: MapPosition
    private var _zoom: Int = 0
    override val state: IMapState = object : IMapState {
        override var center: MapPosition
            get() = _center
            set(value) {
                _center = value
                view.setCenter(value.latLong)
            }

        override var zoom: Int
            get() = _zoom
            set(value) {
                _zoom = value
                view.setZoomLevel(value.toByte())
            }
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

    override val trackLayers = object : ILayersContainer<ITrackLayer> {
        private val layers = mutableListOf<ITrackLayer>()
        override fun addLayer(layer: ITrackLayer) {
            layers.add(layer)
            view.layerManager.layers.add((layer as MapsforgeTrackLayer).layer)
        }

        override fun removeLayer(layer: ITrackLayer) {
            view.layerManager.layers.remove((layer as MapsforgeTrackLayer).layer)
            layers.remove(layer)
        }

        override val defaultLayer: ITrackLayer
            get() {
                if (layers.isEmpty()) {
                    val layer = MapsforgeTrackLayer(view)
                    addLayer(layer)
                }
                return layers[0]
            }
    }

    init {
        state.center = initialState.center
        state.zoom = initialState.zoom

        view.mapScaleBar.isVisible = true
        view.setBuiltInZoomControls(true)

        val tileCache = AndroidUtil.createTileCache(
            activity, "mapcache",
            view.model.displayModel.tileSize, 1f,
            view.model.frameBufferModel.overdrawFactor
        )

        val tileRendererLayer = activity.resources.openRawResource(R.raw.serbia).use {
            val tempFile = File.createTempFile("temp", null, activity.cacheDir)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile).use { fileOutputStream ->
                it.copyTo(fileOutputStream)
            }
            val mapDataStore: MapDataStore = MapFile(FileInputStream(tempFile))
            TileRendererLayer(
                tileCache, mapDataStore,
                view.model.mapViewPosition, AndroidGraphicFactory.INSTANCE
            )
        }
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
        view.layerManager.layers.add(tileRendererLayer)
    }

    override fun close() {
    }
}