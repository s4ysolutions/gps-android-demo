package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.R
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel


@SuppressLint("MissingPermission")
@Composable
fun RecordingIconButton(
    safeGps: (() -> Unit) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val updatingStatus by vm.gpsUpdatesManager.last.status.asStateFlow().collectAsState()

    return IconButton(
        onClick = {
            if (updatingStatus != IGPSUpdatesProvider.Status.IDLE) {
                vm.gpsUpdatesManager.stopRecording()
                Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
            } else safeGps {
                vm.gpsUpdatesManager.startRecording()
                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Icon(
            painter = painterResource(id = when(updatingStatus){
                IGPSUpdatesProvider.Status.IDLE -> R.drawable.gps_is_idle_ready_to_start
                IGPSUpdatesProvider.Status.WARMING_UP -> R.drawable.gps_is_warming_up
                IGPSUpdatesProvider.Status.ACTIVE -> R.drawable.gps_is_active
            }),
            contentDescription = "Localized description"
        )
    }
}