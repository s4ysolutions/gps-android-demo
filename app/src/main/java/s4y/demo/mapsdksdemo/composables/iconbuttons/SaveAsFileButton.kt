package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import s4y.demo.mapsdksdemo.R
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel


@Composable
fun SaveAsFileButton(
    vm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val saveAsFileActive = vm.saveAsFileActive.value
    return IconButton(
        onClick = {
            // TODO: synchronize this
            if (!saveAsFileActive) {
                val ioScope = CoroutineScope(Dispatchers.IO)
                vm.saveAsFileActive.value = true
                ioScope.launch {
                    try {
                        val s = vm.gpsUpdatesManager.saveAsFile()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Saved as $s",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }finally {
                        withContext(Dispatchers.Main) {
                            vm.saveAsFileActive.value = false
                        }
                    }
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(
                id = if (saveAsFileActive)
                    R.drawable.pending
                else
                    R.drawable.gps_save_as_file
            ),
            contentDescription = "Localized description"
        )
    }
}