package s4y.demo.mapsdksdemo.composables.icons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import s4y.demo.mapsdksdemo.R
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantAcceleration
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantPosition
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantVelocity
import s4y.demo.mapsdksdemo.gps.filters.GPSFilterNull
import s4y.demo.mapsdksdemo.gps.filters.GPSFilterProximity

@Composable
fun iconGPSFilter(
    filter: GPSFilter
) = Icon(
    painter = painterResource(
        id = when (filter) {
            GPSFilterNull.instance -> R.drawable.gps_filter_null
            GPSFilterProximity.instance1m -> R.drawable.gps_filter_proximity_1m
            GPSFilterProximity.instance5m -> R.drawable.gps_filter_proximity_5m
            GPSFilterKalmanConstantPosition.instance -> R.drawable.gps_filter_kalman_position
            GPSFilterKalmanConstantVelocity.instance -> R.drawable.gps_filter_kalman_velocity
            GPSFilterKalmanConstantAcceleration.instance -> R.drawable.gps_filter_kalman_acceleration
            else -> R.drawable.gps_filter_unknown
        }
    ),
    contentDescription = "Localized description"
)
