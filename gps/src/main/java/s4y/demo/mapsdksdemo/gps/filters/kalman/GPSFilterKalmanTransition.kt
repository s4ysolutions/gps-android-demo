package s4y.demo.mapsdksdemo.gps.filters.kalman

import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.StateVector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class GPSFilterKalmanTransition {
    private var _currentLatitude: Units.Latitude = Units.Latitude.fromDegrees(0.0)
    private var _currentLongitude: Units.Longitude =
        Units.Longitude.fromDegrees(0.0, _currentLatitude)

    private var _prevAccuracy: Float = 0.0f
    private var _currentAccuracy: Float = 0.0f
    private var _accuracyChanged: Boolean = false
    private var _prevTimeStamp: Long = System.currentTimeMillis()
    private var _currentTimeStamp: Long = System.currentTimeMillis()
    private var _prevDtSec: Double = 0.0
    private var _currentDtSec = 0.0
    private var _dtChanged: Boolean = false
    private var _prevBearing: Double = 0.0
    private var _currentBearing: Double = 0.0
    private var _bearingChanged: Boolean = false
    private var _bearingVariance = Units.Bearing.maxVarianceDegrees
    private var _bearingVariance2: Double = _bearingVariance * _bearingVariance
    private var _bearingAverage: Double = 0.0
    private var _n: Int = 0
    private var _prevX: Double = 0.0
    private var _currentX: Double = 0.0
    private var _prevY: Double = 0.0
    private var _currentY: Double = 0.0
    private var _prevVelocityX: Double = 0.0
    private var _currentVelocityX: Double = 0.0
    private var _prevVelocityY: Double = 0.0
    private var _currentVelocityY: Double = 0.0

    companion object {
        private const val PI2 = 2 * PI
    }

    private fun updateLongitudeLatitude(longitude: Units.Longitude, latitude: Units.Latitude) {
        _currentLatitude = latitude
        _currentLongitude = longitude
        _currentX = _currentLongitude.meters
        _currentY = _currentLatitude.meters
    }

    private fun updateLongitudeLatitude(gpsUpdate: GPSUpdate) {
        val lat = Units.Latitude.fromDegrees(gpsUpdate.latitude)
        val lon = Units.Longitude.fromDegrees(gpsUpdate.longitude, lat)
        updateLongitudeLatitude(lon, lat)
    }

    private fun updateLongitudeLatitude(estimation: StateVector) =
        updateLongitudeLatitude(estimation.longitude, estimation.latitude)

    private fun updateVelocity() {
        _currentVelocityX = (_currentX - _prevX) / _currentDtSec
        if (_currentVelocityX > 60)
            _currentVelocityX = 60.0
        _currentVelocityY = (_currentY - _prevY) / _currentDtSec
        if (_currentVelocityY > 60)
            _currentVelocityY = 60.0
    }

    private fun updateBearing() {
        val dy = _currentY - _prevY
        val dx = _currentX - _prevX
        if (abs(dx) > 3 || abs(dy) > 3) {
            _currentBearing =
                if (dx >= 0)
                    atan2(dx, dy)
                else
                    PI2 + atan2(dx, dy)
        }
        _bearingChanged = abs(_currentBearing - _prevBearing) > 1.0
    }

    fun addMeasurement(gpsUpdate: GPSUpdate) = synchronized(this) {
        _n++

        // save prev values if not the first update
        if (_n > 1) {
            _prevTimeStamp = _currentTimeStamp
            _prevAccuracy = _currentAccuracy
            _prevX = _currentX
            _prevY = _currentY
            _prevBearing = _currentBearing
            if (_n > 2) {
                // save prev differences
                _prevDtSec = _currentDtSec
                _prevVelocityX = _currentVelocityX
                _prevVelocityY = _currentVelocityY
            }
        }
        // save current values
        _currentTimeStamp = gpsUpdate.ts
        _currentAccuracy = gpsUpdate.accuracy

        updateLongitudeLatitude(gpsUpdate)

        if (_n > 1) {
            // from the 2nd update bearing is calculated from x,y
            updateBearing()

            // detect if values changed
            _accuracyChanged = abs(_currentAccuracy - _prevAccuracy) > 1.0f

            // calculate differences
            _currentDtSec = (_currentTimeStamp - _prevTimeStamp).toDouble() / 1000
            updateVelocity()

            if (_n > 2) {
                // detect if differences changed
                _dtChanged = abs(_currentDtSec - _prevDtSec) > 0.333
            }
        } else {
            // very first bearing if from GPS
            _currentBearing = Math.toRadians(gpsUpdate.bearing)
        }

        // calculate statistics for gps bearing
        _bearingAverage += (gpsUpdate.bearing - _bearingAverage) / _n
        val bearingDistance = (gpsUpdate.bearing - _bearingAverage)
        val bearingDistance2 = bearingDistance * bearingDistance
        _bearingVariance2 += (bearingDistance2 - _bearingVariance2) / _n
        _bearingVariance = sqrt(_bearingVariance2)
    }

    fun updateWithEstimation(estimation: StateVector) = synchronized(this) {
        updateLongitudeLatitude(estimation)
        if (_n > 1) {
            updateVelocity()
            updateBearing()
        }
    }

    fun updateWithEstimation(update: GPSUpdate) = synchronized(this) {
        updateLongitudeLatitude(update)
        if (_n > 1) {
            updateVelocity()
            updateBearing()
        }
    }

    val latitude: Units.Latitude get() = _currentLatitude
    val longitude: Units.Longitude get() = _currentLongitude
    val metersX: Double get() = _currentX
    val metersY: Double get() = _currentY
    val ts: Long get() = _currentTimeStamp
    val accuracyChanged: Boolean get() = _accuracyChanged
    val dtChanged: Boolean get() = _dtChanged
    val dtSec get() = _currentDtSec

    val accuracy: Units.Accuracy get() = Units.Accuracy(_currentAccuracy)
    val bearingDegrees get() = Math.toDegrees(_currentBearing)
    val velocity: Units.Velocity
        get() = Units.Velocity(
            _currentVelocityX, _currentVelocityY
            /*
    sqrt(_currentVelocityX * _currentVelocityX + _currentVelocityY * _currentVelocityY),
    Units.Bearing.fromRadians(_currentBearing),
    longitude,
    latitude
             */
        )
    val velocityX: Double get() = _currentVelocityX
    val velocityY: Double get() = _currentVelocityY
    val accelerationX: Double
        get() = let {
            val acc =
                if (dtSec == 0.0 || _n < 3) 0.0 else (_currentVelocityX - _prevVelocityX) / dtSec
            if (acc > 5) 5.0 else acc
        }
    val accelerationY: Double
        get() = let {
            val acc =
                if (dtSec == 0.0 || _n < 3) 0.0 else (_currentVelocityY - _prevVelocityY) / dtSec
            if (acc > 5) 5.0 else acc
        }

    val distanceMeters: Double get() = sqrt((_currentX - _prevX) * (_currentX - _prevX) + (_currentY - _prevY) * (_currentY - _prevY))

    val count: Int get() = _n
}