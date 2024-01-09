package s4y.demo.mapsdksdemo.mapsforgevtm

//import org.oscim.android.mvt.tiling.source.mbtiles.MBTilesMvtTileSource
//import org.oscim.android.tiling.source.mbtiles.MBTilesTileSource
import android.app.Activity
import org.oscim.android.MapView
import org.oscim.backend.CanvasAdapter
import org.oscim.layers.GroupLayer
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.map.Map
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.ImperialUnitAdapter
import org.oscim.scalebar.MapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.scalebar.MetricUnitAdapter
import org.oscim.theme.VtmThemes
import org.oscim.tiling.source.mapfile.MapFileTileSource
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.BaseMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapState
import s4y.demo.mapsdksdemo.map.dependencies.IMapStateMutable
import s4y.demo.mapsdksdemo.map.dependencies.layers.BaseLayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ILayersContainer
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import s4y.demo.mapsdksdemo.mapsforge.maps.MapsforgeMaps
import s4y.demo.mapsdksdemo.mapsforgevtm.layers.VtmTrackLayer


class VtmMap(activity: Activity, initialState: IMapState) : BaseMap() {
    companion object {
        const val mapType = "vtm"
        // const val USE_CACHE = true
    }

    override val id = mapType

    @Suppress("RedundantOverride")
    override val view: MapView = object : MapView(activity) {
        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
        }
    }

    private val mMap = view.map()

    override val state: IMapStateMutable = object : IMapStateMutable {
        override var center: MapPosition
            get() = view.map().mapPosition.mapPosition
            set(value) {
                val position = view.map().mapPosition
                position.setPosition(value.latitude, value.longitude)
                view.map().mapPosition = position
                notifyStateChangeListeners(this)
            }

        override var zoom: Int
            get() = view.map().mapPosition.zoom.toInt()
            set(value) {
                val position = view.map().mapPosition
                position.zoom = value.toDouble()
                view.map().mapPosition = position
                notifyStateChangeListeners(this)
            }
    }

    // var mTileSource: TileSource? = null
    private var mBaseLayer: VectorTileLayer? = null
    private var mBuildingLayer: BuildingLayer? = null
    private val mShadow = false

    init {
        /*
        val builder: OkHttpClient.Builder = OkHttpClient
            .Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
        if (USE_CACHE) {
            // Cache the tiles into file system
            val cacheDirectory: File = File(activity.externalCacheDir, "tiles")
            val cacheSize = 10L * 1024 * 1024 // 10 MB
            val cache = Cache(cacheDirectory, cacheSize)
            builder.cache(cache)
        }

        mTileSource = OSciMap4TileSource.builder()
            .httpFactory(OkHttpEngine.OkHttpFactory(builder))
            .build()
            */

        // val tileSource: MBTilesTileSource = MBTilesMvtTileSource(MapsforgeMaps().defaultMapFile(activity).absolutePath)//, "en")
        val tileSource = MapFileTileSource()
        tileSource.setMapFile(MapsforgeMaps().defaultMapFile(activity).absolutePath)
        mBaseLayer = mMap.setBaseMap(tileSource)

        val groupLayer = GroupLayer(mMap)
        mBuildingLayer = BuildingLayer(mMap, mBaseLayer, false, mShadow)
        groupLayer.layers.add(mBuildingLayer)
        groupLayer.layers.add(LabelLayer(mMap, mBaseLayer))
        mMap.layers().add(groupLayer)

        val mapScaleBar = DefaultMapScaleBar(mMap)
        mapScaleBar.scaleBarMode = DefaultMapScaleBar.ScaleBarMode.BOTH
        mapScaleBar.distanceUnitAdapter = MetricUnitAdapter.INSTANCE
        mapScaleBar.secondaryDistanceUnitAdapter = ImperialUnitAdapter.INSTANCE
        mapScaleBar.scaleBarPosition = MapScaleBar.ScaleBarPosition.BOTTOM_LEFT

        val mapScaleBarLayer = MapScaleBarLayer(mMap, mapScaleBar)
        val renderer = mapScaleBarLayer.renderer
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
        renderer.setOffset(5 * CanvasAdapter.getScale(), 0 * CanvasAdapter.getScale())
        mMap.layers().add(mapScaleBarLayer)

        mMap.setTheme(VtmThemes.DEFAULT)

        state.center = initialState.center
        state.zoom = initialState.zoom

        mMap.events.bind(Map.UpdateListener { _, mapPosition ->
            mapPosition?.let {
                if (
                    it.latitude != state.center.latitude ||
                    it.longitude != state.center.longitude ||
                    it.zoom.toInt() != state.zoom
                ) {
                    notifyStateChangeListeners(state)
                }
            }
        })

    }

    override val currentGPSPositionLayer: ICurrentGPSPositionLayer
        get() = TODO("Not yet implemented")
    override val trackLayers: ILayersContainer<ITrackLayer> =
        object : BaseLayersContainer<ITrackLayer>() {
            override fun addLayer(layer: ITrackLayer) {
                super.addLayer(layer)
                mMap.layers().add((layer as VtmTrackLayer).layer)
            }

            override fun removeLayer(layer: ITrackLayer) {
                mMap.layers().remove((layer as VtmTrackLayer).layer)
                super.removeLayer(layer)
            }

            override fun createDefaultLayer(): ITrackLayer {
                return VtmTrackLayer(mMap)
            }
        }

    override fun close() {
        view.onDestroy()
    }

    override fun pause() {
        view.onPause()
    }

    override fun resume() {
        view.onResume()
    }
}