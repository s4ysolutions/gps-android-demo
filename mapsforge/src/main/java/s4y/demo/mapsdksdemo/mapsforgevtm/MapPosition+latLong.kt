package s4y.demo.mapsdksdemo.mapsforgevtm

import org.mapsforge.core.model.LatLong
import s4y.demo.mapsdksdemo.map.MapPosition

val MapPosition.latLong: LatLong
    get() = LatLong(latitude, longitude)