package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.R
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
                        Toast.makeText(context, "Getting current position", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            vm.gpsCurrentPositionManager
                                .requestCurrentPosition()
                                .collect {
                                    vm.mapStateFlow.value?.state?.center = it.toMapPosition()
                                }
                        }
                    }
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(id = when(waitingGpsUpdate.value){
                true -> R.drawable.pending
                false -> R.drawable.map_center_to_my_position
            }),
            contentDescription = "Localized description"
        )
    }
}