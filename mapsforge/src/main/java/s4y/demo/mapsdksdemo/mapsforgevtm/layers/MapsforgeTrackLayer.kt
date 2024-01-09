package s4y.demo.mapsdksdemo.mapsforgevtm.layers

import org.mapsforge.core.graphics.Color
import org.mapsforge.core.graphics.Style
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.overlay.Polyline
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer
import s4y.demo.mapsdksdemo.mapsforgevtm.latLong

class MapsforgeTrackLayer(private val mapView: MapView) : ITrackLayer {
    internal val layer = Polyline(
        AndroidGraphicFactory.INSTANCE.createPaint()
            .apply {
                color = AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE)
                strokeWidth = 8 * mapView.model.displayModel.scaleFactor
                setStyle(Style.STROKE)
            }, AndroidGraphicFactory.INSTANCE
    ).apply {
        isVisible = true
    }

    override fun setPositions(positions: Array<MapPosition>) {
        layer.setPoints(positions.map { it.latLong })
        mapView.postInvalidate()
        layer.requestRedraw()
    }

    override fun addPosition(position: MapPosition) {
        layer.addPoint(position.latLong)
        layer.requestRedraw()
    }

    override var isVisible: Boolean
        get() = layer.isVisible
        set(value) {
            layer.isVisible = value
        }

}