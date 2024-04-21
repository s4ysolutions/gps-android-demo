package s4y.demo.mapsdksdemo.gps.data

class Projection private constructor() {
    class X private constructor(val meters: Double) {
        companion object {
            fun fromX(
                x: Double,
            ): X = X(x)
            fun fromModule(
                module: Double,
                bearing: Units.Bearing,
            ): X = X(module * bearing.cos)
        }

        val degrees: Double by lazy {
            throw NotImplementedError()
            // latitude.longitudeMetersToDegrees(meters) * bearing.cos
        }
    }
    class _X private constructor(
        fromMeters: Boolean,
        module: Double,
        bearing: Units.Bearing
    ) {
        companion object {
            fun fromMeters(
                module: Double,
                bearing: Units.Bearing,
            ): _X =
                _X(true, module, bearing)
            fun fromMeters(
                module: Float,
                bearing: Units.Bearing,
                longitude: Units.Longitude,
            ): _X = fromMeters(module.toDouble(), bearing)
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

    class Y private constructor(val meters: Double) {
        companion object {
        fun fromY(
                y: Double,
            ): Y = Y(y)
            fun fromModule(
                module: Double,
                bearing: Units.Bearing,
            ): Y = Y(module * bearing.sin)
        }

        val degrees: Double by lazy {
            throw NotImplementedError()
            // longitude.latitudeMetersToDegrees(meters) * bearing.sin
        }
    }
    class _Y private constructor(
        fromMeters: Boolean,
        module: Double,
        bearing: Units.Bearing
    ) {
        companion object {
            fun fromMeters(
                module: Double,
                bearing: Units.Bearing,
            ): _Y =
                _Y(true, module, bearing)

            fun fromMeters(
                module: Float,
                bearing: Units.Bearing,
                latitude: Units.Latitude,
            ): _Y = fromMeters(module.toDouble(), bearing)

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