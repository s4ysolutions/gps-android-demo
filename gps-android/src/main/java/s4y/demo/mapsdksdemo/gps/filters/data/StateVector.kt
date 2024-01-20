package s4y.demo.mapsdksdemo.gps.filters.data

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealVector
import s4y.demo.mapsdksdemo.gps.GPSUpdate

sealed class StateVector private constructor() {
    abstract val vector: RealVector
    abstract fun toGpsUpdate(z: MeasurementVector): GPSUpdate
    class LongitudeLatitude( val longitude: Units.Longitude, val latitude: Units.Latitude): StateVector() {
        override val vector: RealVector
        // val longitude: Units.Longitude
        // val latitude: Units.Latitude
        /*
        override val vector: RealVector = ArrayRealVector(doubleArrayOf(
            longitude.degree,
            latitude.degree
        ))*/

        init {
            this.vector = ArrayRealVector(doubleArrayOf(longitude.degree, latitude.degree))
        }

        private constructor(longitude: Double, latitude: Units.Latitude): this(Units.Longitude(longitude, latitude.cos), latitude)

        constructor(vector: DoubleArray): this(vector[0], Units.Latitude(vector[1]))

        override fun toGpsUpdate(z: MeasurementVector): GPSUpdate {
            return GPSUpdate(
                latitude.degree,
                longitude.degree,
                z.velocity.mPerSec,
                z.accuracy.meters.accuracy,
                z.bearing.degree,
                z.ts
            )
        }
    }
}