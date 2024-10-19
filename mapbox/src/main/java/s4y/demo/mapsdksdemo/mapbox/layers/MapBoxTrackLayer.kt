package s4y.demo.mapsdksdemo.mapbox.layers

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import s4y.demo.mapsdksdemo.mapbox.toPoint

class MapBoxTrackLayer(mapView: MapView) : ITrackLayer {
    private var lineString: LineString =  LineString.fromLngLats(mutableListOf())
    private var geoJsonSource: GeoJsonSource? = null
    private var lineLayer: LineLayer? = null

    init {
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
            val source = geoJsonSource("track-source") {
                featureCollection(FeatureCollection.fromFeature(Feature.fromGeometry(lineString)))
            }
            style.addSource(source)
            geoJsonSource = source

            // Create a LineLayer using the GeoJson source
            LineLayer("track-layer", "track-source").also {
                lineLayer = it
                it.lineColor("blue") // Set the line color (e.g., blue)
                it.lineWidth(3.0) // Set the line width
                style.addLayer(it)
            }
        }
    }

    override fun setPositions(positions: Array<MapPosition>) {
        geoJsonSource?.let { source ->
            val updateCoordinates = lineString.coordinates().toMutableList()
            positions.forEach {
                updateCoordinates.add(
                    it.toPoint()
                )
            }
            lineString = LineString.fromLngLats(updateCoordinates)

            updateSource()
        }
    }

    override fun addPosition(position: MapPosition) {
        geoJsonSource?.let { source ->
            val updateCoordinates = lineString.coordinates().toMutableList()
            updateCoordinates.add(
                position.toPoint()
            )
            lineString = LineString.fromLngLats(updateCoordinates)
            updateSource()
        }
    }

    override var isVisible: Boolean
        get() = lineLayer?.visibility == Visibility.VISIBLE
        set(value) {
            lineLayer?.visibility(if (value) Visibility.VISIBLE else Visibility.NONE)
        }

    private fun updateSource() {
        geoJsonSource?.let { source ->
            val updateFeatureCollection =
                FeatureCollection.fromFeature(Feature.fromGeometry(lineString))

            source.featureCollection(updateFeatureCollection)
        }
    }
}