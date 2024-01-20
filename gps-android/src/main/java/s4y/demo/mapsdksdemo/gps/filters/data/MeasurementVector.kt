package s4y.demo.mapsdksdemo.gps.filters.data

import s4y.demo.mapsdksdemo.gps.GPSUpdate

sealed class MeasurementVector(gpsUpdate: GPSUpdate){
    val latitude = Units.Latitude(gpsUpdate.latitude)
    val longitude = Units.Longitude(gpsUpdate.longitude, latitude.cos)
    val velocity = Units.Velocity(gpsUpdate.velocity)
    val bearing = Units.Bearing(gpsUpdate.bearing)
    val accuracy = Units.Accuracy(gpsUpdate)
    val ts: Long = gpsUpdate.ts

    abstract val state: StateVector
    override fun toString(): String {
        return "MeasurementVector(latitude=$latitude, longitude=$longitude, velocity=$velocity, bearing=$bearing, accuracy=$accuracy, ts=$ts)"
    }
    class LongitudeLatitude(gpsUpdate: GPSUpdate) : MeasurementVector(gpsUpdate) {
        override val state = StateVector.LongitudeLatitude(longitude, latitude)
    }
}