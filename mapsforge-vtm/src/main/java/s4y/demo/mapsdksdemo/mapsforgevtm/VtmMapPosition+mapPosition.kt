package s4y.demo.mapsdksdemo.mapsforgevtm

import org.oscim.core.MapPosition

val MapPosition.mapPosition get() =
    s4y.demo.mapsdksdemo.map.MapPosition(this.latitude, this.longitude)