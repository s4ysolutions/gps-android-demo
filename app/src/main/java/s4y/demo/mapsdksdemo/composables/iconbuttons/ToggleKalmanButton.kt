package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalman
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantPosition
import s4y.demo.mapsdksdemo.gps.filters.GPSFilterNull
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@SuppressLint("MissingPermission")
@Composable
fun ToggleKalmanButton(
    safeGps: (() -> Unit) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val currentFilter = vm.gpsUpdatesManager.currentFilter.asStateFlow().collectAsState()
    val waitingGpsUpdate = vm.gpsCurrentPositionManager.status.asStateFlow().collectAsState()

    return IconButton(
        onClick = {
            if (currentFilter.value is GPSFilterKalman)
                vm.gpsUpdatesManager.currentFilter.set(GPSFilterNull.instance)
            else {
                safeGps {
                    scope.launch {
                        vm.gpsCurrentPositionManager
                            .requestCurrentPosition()
                            .collect {
                                val filter = GPSFilterKalmanConstantPosition.instance
                                filter.reset(it)
                                vm.gpsUpdatesManager.currentFilter.set(filter)
                            }
                    }
                }
            }
        }
    ) {
        Icon(
            imageVector =
            if (waitingGpsUpdate.value)
                Icons.Filled.Warning
            else if (currentFilter.value is GPSFilterKalman)
                Icons.Filled.Favorite
            else
                Icons.Filled.FavoriteBorder,
            contentDescription = "Localized description"
        )
    }
}