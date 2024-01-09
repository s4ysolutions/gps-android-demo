package s4y.demo.mapsdksdemo.mapsforgevtm.layers

import org.oscim.backend.canvas.Color
import org.oscim.backend.canvas.Paint
import org.oscim.core.GeoPoint
import org.oscim.layers.vector.PathLayer
import org.oscim.layers.vector.VectorLayer
import org.oscim.layers.vector.geometries.LineDrawable
import org.oscim.layers.vector.geometries.Style
import org.oscim.map.Map
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.layers.ITrackLayer

class VtmTrackLayer(mMap: Map): ITrackLayer {
    private val style: Style = Style.builder()
        .strokeWidth(10f)
        .strokeColor(Color.setA(Color.BLUE, 127))
        // .cap(Paint.Cap.BUTT)
        //.dropDistance(10f)
        .fixed(true)
        .build()

    internal val layer = PathLayer(mMap,style).apply {
        isEnabled = true
    }

    override fun setPositions(positions: Array<MapPosition>) {
        layer.clearPath()
        layer.setPoints(positions.map { GeoPoint(it.latitude, it.longitude) })
    }

    override fun addPosition(position: MapPosition) {
        layer.addPoint(GeoPoint(position.latitude, position.longitude))
    }

    override var isVisible: Boolean
        get() = layer.isEnabled
        set(value) {
            layer.isEnabled = value
        }
}