package s4y.demo.mapsdksdemo.mapbox.layers

import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.LocationProvider
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import s4y.demo.mapsdksdemo.map.MapPosition
import s4y.demo.mapsdksdemo.map.dependencies.layers.Accuracy
import s4y.demo.mapsdksdemo.map.dependencies.layers.ICurrentGPSPositionLayer
import s4y.demo.mapsdksdemo.mapbox.toPoint

class MapboxCurrentGPSPositionLayer(private val view: MapView) : ICurrentGPSPositionLayer {
    private var locationConsumer: LocationConsumer? = null;

    init {
        with(view.location) {
            locationPuck = createDefault2DPuck(withBearing = true)
            enabled = true
            puckBearing = PuckBearing.COURSE
            puckBearingEnabled = true
            setLocationProvider(object : LocationProvider {
                override fun registerLocationConsumer(locationConsumer: LocationConsumer) {
                    this@MapboxCurrentGPSPositionLayer.locationConsumer = locationConsumer
                }

                override fun unRegisterLocationConsumer(locationConsumer: LocationConsumer) {
                    this@MapboxCurrentGPSPositionLayer.locationConsumer = null
                }
            })
        }
    }

    override fun updatePosition(
        position: MapPosition,
        accuracy: Accuracy
    ) {
        with(view.location) {
            locationConsumer?.onLocationUpdated(position.toPoint())
            enabled = true
        }
    }

    override var isVisible: Boolean
        get() = view.location.enabled
        set(value) {
            view.location.enabled = value
        }
}