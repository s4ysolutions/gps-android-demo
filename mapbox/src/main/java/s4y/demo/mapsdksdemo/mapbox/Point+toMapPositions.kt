package s4y.demo.mapsdksdemo.mapbox

import com.mapbox.geojson.Point
import s4y.demo.mapsdksdemo.map.MapPosition

fun Point.toMapPosition(): MapPosition = MapPosition(latitude(), longitude())