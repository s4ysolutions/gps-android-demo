package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel


@SuppressLint("MissingPermission")
@Composable
fun RecordingIconButton(
    isRecording: Boolean,
    safeGps: (() -> Unit) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    return IconButton(
        onClick = {
            if (isRecording)
                vm.gpsUpdatesManager.stopRecording()
            else safeGps {
                vm.gpsUpdatesManager.startRecording(context)
            }
        }
    ) {
        Icon(
            imageVector = if (isRecording)
                Icons.Filled.Close
            else
                Icons.Filled.PlayArrow,
            contentDescription = "Localized description"
        )
    }
}