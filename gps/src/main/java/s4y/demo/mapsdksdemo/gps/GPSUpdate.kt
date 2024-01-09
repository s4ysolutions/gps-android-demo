package s4y.demo.mapsdksdemo.gps

import android.location.Location

class GPSUpdate(private val location: Location) {
    companion object {
        val emptyGPSUpdate = GPSUpdate(Location("empty"))
    }
    val latitude: Double get() = location.latitude
    val longitude: Double get() = location.longitude
    val accuracy: Float get() = location.accuracy
    val isEmpty: Boolean get() = location == emptyGPSUpdate.location

    override fun toString(): String {
        return "GPSUpdate(lat=$latitude, long=$longitude, accuracy=$accuracy)"
    }
}