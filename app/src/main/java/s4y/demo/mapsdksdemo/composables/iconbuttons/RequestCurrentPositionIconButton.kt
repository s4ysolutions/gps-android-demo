package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel


@SuppressLint("MissingPermission")
@Composable
fun RequestCurrentPositionIconButton(
    isRequestingCurrentPosition: Boolean,
    safeGps: (() -> Unit)->Unit,
    vm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    return IconButton(
        onClick = {
            if (!isRequestingCurrentPosition) safeGps {
                TODO()
                //vm.updateCurrentPosition(context)
            }
        }
    ) {
        Icon(
            imageVector = if (isRequestingCurrentPosition)
                Icons.Filled.Close
            else
                Icons.Filled.LocationOn,
            contentDescription = "Localized description"
        )
    }
}