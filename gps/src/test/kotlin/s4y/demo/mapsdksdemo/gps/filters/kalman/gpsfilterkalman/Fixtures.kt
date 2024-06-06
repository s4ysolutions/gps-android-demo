package s4y.demo.mapsdksdemo.gps.filters.kalman.gpsfilterkalman

import org.junit.jupiter.api.Named
import org.junit.jupiter.params.provider.Arguments
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantAcceleration
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantPosition
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantVelocity
import java.util.stream.Stream

open class Fixtures {
    class Estimation(val count: Int, val dt: Double, val update: GPSUpdate)
    class Estimations(private val entries: List<Estimation>) : List<Estimation> by entries
    companion object {
        private const val accuracy = 3.0f

        private val positionsUpdates = listOf(
            GPSUpdate(0.0001, 0.0002, 0.01f, accuracy, 180.0, 10000),
            GPSUpdate(0.0003, 0.0004, 0.01f, accuracy, 180.0, 15000),
            GPSUpdate(-0.0001, -0.0002, 0.01f, accuracy, 180.0, 18000),
            GPSUpdate(-0.0001, -0.0002, 0.01f, accuracy, 180.0, 20000),
        )

        private val positionsEstimationsWalking1ms = listOf(
            Estimation(1, 0.0, GPSUpdate(0.0001, 0.0002, 0.01f, accuracy, 180.0, 10000)),
            Estimation(2, 5.0, GPSUpdate(0.0003, 0.0004, 0.01f, accuracy, 45.0, 15000)),
            Estimation(3, 3.0, GPSUpdate(0.0, 0.0, 0.01f, accuracy, 235.0, 18000)),
            Estimation(4, 2.0, GPSUpdate(-0.0001, -0.0002, 0.01f, accuracy, 235.0, 20000)),
        )

        private val positionsEstimations = positionsEstimationsWalking1ms

        private val velocityReal = let {
            var ts = 10000L
            var lat = Units.Latitude.fromDegrees(0.0001)
            var lon = Units.Longitude.fromDegrees(0.0002, lat)

            var dt = 5
            val u1 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 180.0, ts)

            var velocityX = 100.0
            var velocityY = 100.0

            lat = Units.Latitude.fromMeters(lat.meters + velocityY * dt)
            lon = Units.Longitude.fromMeters(lon.meters + velocityX * dt, lat)
            ts += dt * 1000
            val u2 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 45.0, ts)

            lat = Units.Latitude.fromMeters(lat.meters + velocityY * dt)
            lon = Units.Longitude.fromMeters(lon.meters + velocityX * dt, lat)
            ts += dt * 1000
            val u3 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 45.0, ts)

            lat = Units.Latitude.fromMeters(lat.meters + velocityY * dt)
            lon = Units.Longitude.fromMeters(lon.meters + velocityX * dt, lat)
            ts += dt * 1000
            val u4 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 45.0, ts)

            velocityX = 150.0
            velocityY = 150.0
            lat = Units.Latitude.fromMeters(lat.meters + velocityY * dt)
            lon = Units.Longitude.fromMeters(lon.meters + velocityX * dt, lat)
            ts += dt * 1000
            val u5 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 45.0, ts)

            dt = 2
            lat = Units.Latitude.fromMeters(lat.meters + velocityY * dt)
            lon = Units.Longitude.fromMeters(lon.meters + velocityX * dt, lat)
            ts += dt * 1000
            val u6 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 45.0, ts)

            velocityX = -50.0
            velocityY = -50.0
            lat = Units.Latitude.fromMeters(lat.meters + velocityY * dt)
            lon = Units.Longitude.fromMeters(lon.meters + velocityX * dt, lat)
            ts += dt * 1000
            val u7 = GPSUpdate(lat.degrees, lon.degrees, 0.01f, accuracy, 45.0, ts)

            listOf(u1, u2, u3, u4, u5, u6, u7)
        }

        private val velocityUpdates = velocityReal.map {
            with(it) {
                GPSUpdate(
                    latitude + 0.00005,
                    longitude - 0.00005,
                    velocity,
                    accuracy,
                    bearing,
                    ts
                )
            }
        }

        private val velocityEstimations = listOf(
            Estimation(1, 0.0, velocityReal[0]),
            Estimation(2, 5.0, velocityReal[1]),
            Estimation(3, 5.0, velocityReal[2]),
            Estimation(4, 5.0, velocityReal[3]),
            Estimation(5, 5.0, velocityReal[4]),
            Estimation(6, 2.0, velocityReal[5]),
            Estimation(7, 2.0, velocityReal[6]),
        )

        @JvmStatic
        fun provideAllFilters(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    Named.named(
                        GPSFilterKalmanConstantPosition::class.java.simpleName,
                        GPSFilterKalmanConstantPosition(1.0)
                    ),
                    positionsUpdates,
                    Estimations(positionsEstimations)
                ),
                Arguments.arguments(
                    Named.named(
                        GPSFilterKalmanConstantVelocity::class.java.simpleName,
                        GPSFilterKalmanConstantVelocity()
                    ),
                    positionsUpdates,
                    Estimations(positionsEstimations)
                ),
                Arguments.arguments(
                    Named.named(
                        GPSFilterKalmanConstantAcceleration::class.simpleName,
                        GPSFilterKalmanConstantAcceleration()
                    ),
                    positionsUpdates,
                    Estimations(positionsEstimations)
                )
            )
        }

        @JvmStatic
        fun providePositionFilters(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    Named.named(
                        GPSFilterKalmanConstantPosition::class.java.simpleName,
                        GPSFilterKalmanConstantPosition(1.0)
                    ),
                    positionsUpdates,
                    Estimations(positionsEstimations)
                )
            )
        }

        @JvmStatic
        fun provideVelocityFilters(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    Named.named(
                        GPSFilterKalmanConstantVelocity::class.java.simpleName,
                        GPSFilterKalmanConstantVelocity()
                    ),
                    velocityUpdates,
                    Estimations(velocityEstimations)
                ),
            )
        }
    }
}