package s4y.demo.mapsdksdemo.lib.cast

import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.map.MapPosition

fun GPSUpdate.toMapPosition(): MapPosition = MapPosition(latitude, longitude)