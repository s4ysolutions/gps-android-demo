package s4y.demo.mapsdksdemo.gps

class GPSUpdate(
    val latitude: Double,
    val longitude: Double,
    val velocity: Float,
    val accuracy: Float,
    val bearing: Double,
    val ts: Long
) {
    companion object {
        val emptyGPSUpdate = GPSUpdate(0.0, 0.0, 0.0f, 0.0f, 0.0, 0L)
    }

    val isEmpty: Boolean get() = this == emptyGPSUpdate

    override fun toString(): String =
        "GPSUpdate(lat=$latitude, long=$longitude, velocity=$velocity, bearing=$bearing, accuracy=$accuracy, ts=$ts)"
}