package s4y.demo.mapsdksdemo.gps.data

// bearing in radians
class Projection private constructor() {
    class X private constructor(
        fromMeters: Boolean,
        module: Double,
        bearing: Units.Bearing
    ) {
        companion object {
            fun fromMeters(
                module: Double,
                bearing: Units.Bearing,
            ): X =
                X(true, module, bearing)
            fun fromMeters(
                module: Float,
                bearing: Units.Bearing,
                latitude: Units.Latitude
            ): X = fromMeters(module.toDouble(), bearing)
        }

        val meters: Double = if (fromMeters)
            module * bearing.cos
        else
            throw NotImplementedError()

        // latitude.longitudeDegreesToMeters(module) * bearing.cos
        val degrees: Double by lazy {
            throw NotImplementedError()
            /*
            if (fromMeters)
                latitude.longitudeMetersToDegrees(meters) * bearing.cos
            else
                module * bearing.cos
             */
        }
    }

    class Y private constructor(
        fromMeters: Boolean,
        module: Double,
        bearing: Units.Bearing
    ) {
        companion object {
            fun fromMeters(
                module: Double,
                bearing: Units.Bearing,
            ): Y =
                Y(true, module, bearing)

            fun fromMeters(
                module: Float,
                bearing: Units.Bearing,
                longitude: Units.Longitude
            ): Y = fromMeters(module.toDouble(), bearing)

        }

        val meters: Double = if (fromMeters)
            module * bearing.sin
        else
            throw NotImplementedError()
        // longitude.latitudeDegreesToMeters(module) * bearing.sin
        val degrees: Double by lazy {
            throw NotImplementedError()
            /*
            Double = if (fromMeters)
                longitude.latitudeMetersToDegrees(meters) * bearing.sin
            else
                module * bearing.sin
             */
        }
    }
}