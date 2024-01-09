package s4y.demo.mapsdksdemo.mapsforge

import org.mapsforge.core.model.LatLong
import s4y.demo.mapsdksdemo.map.MapPosition
val LatLong.mapPosition get() = MapPosition(latitude, longitude)