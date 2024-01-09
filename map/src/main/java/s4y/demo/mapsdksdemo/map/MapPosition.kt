package s4y.demo.mapsdksdemo.map

import java.io.Serializable

class MapPosition(val latitude: Double, val longitude: Double): Serializable {
    constructor() : this(0.0, 0.0)
}