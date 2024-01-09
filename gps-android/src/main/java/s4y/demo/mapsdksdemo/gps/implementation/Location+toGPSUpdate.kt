package s4y.demo.mapsdksdemo.gps.implementation

import android.location.Location
import s4y.demo.mapsdksdemo.gps.GPSUpdate

fun Location.toGPSUpdate(): GPSUpdate = GPSUpdate(
    latitude,
    longitude,
    speed,
    accuracy,
    bearing.toDouble(),
    time
)

