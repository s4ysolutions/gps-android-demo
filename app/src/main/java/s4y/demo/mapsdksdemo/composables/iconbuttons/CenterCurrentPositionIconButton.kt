package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.lib.cast.toMapPosition
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel


@SuppressLint("MissingPermission")
@Composable
fun CenterCurrentPositionIconButton(
    safeGps: (() -> Unit) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope: CoroutineScope = rememberCoroutineScope()
    val waitingGpsUpdate = vm.gpsCurrentPositionManager.status.asStateFlow().collectAsState()
    return IconButton(
        onClick = {
            safeGps {
                if (!waitingGpsUpdate.value) {
                    safeGps {
                        scope.launch {
                            vm.gpsCurrentPositionManager
                                .requestCurrentPosition(context)
                                .collect {
                                    vm.map.state.center = it.toMapPosition()
                                }
                        }
                    }
                }
            }
        }
    ) {
        Icon(
            imageVector = if (waitingGpsUpdate.value)
                Icons.Filled.Warning
            else
                Icons.Filled.LocationOn,
            contentDescription = "Localized description"
        )
    }
}