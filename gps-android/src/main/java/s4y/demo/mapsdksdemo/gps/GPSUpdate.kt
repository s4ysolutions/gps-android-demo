package s4y.demo.mapsdksdemo.gps

import android.location.Location

class GPSUpdate(
    val latitude: Double,
    val longitude: Double,
    val velocity: Float,
    val accuracy: Float,
    val bearing: Float,
    val ts: Long
) {
    companion object {
        val emptyGPSUpdate = GPSUpdate(0.0, 0.0, 0.0f, 0.0f, 0.0f, 0L)
    }

    constructor(location: Location) : this(
        location.latitude,
        location.longitude,
        location.speed,
        location.accuracy,
        location.bearing,
        location.time
    )

    val isEmpty: Boolean get() = this == emptyGPSUpdate

    override fun toString(): String =
        "GPSUpdate(lat=$latitude, long=$longitude, velocity=$velocity, bearing=$bearing, accuracy=$accuracy, ts=$ts)"
}