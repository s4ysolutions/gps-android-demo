package s4y.demo.mapsdksdemo.mapsforgevtm.layers

import org.mapsforge.core.graphics.Color
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Circle
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.mapsforgevtm.latLong

class MapsforgeCurrentGPSPositionLayer(position: LatLong, accuracy: Float) :
    ICurrentGPSPositionLayer {

    internal val layer =
        Circle(position, accuracy, AndroidGraphicFactory.INSTANCE.createPaint()
            .apply {
                setColor(Color.RED)
                setStyle(Style.FILL)
            }, null
        )


    override fun updatePosition(position: MapPosition, accuracy: Float) {
        layer.setLatLong(position.latLong)
        layer.radius = accuracy
    }

    override var isVisible: Boolean
        get() = layer.isVisible
        set(value) {
            layer.isVisible = value
        }
}
