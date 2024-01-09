package s4y.demo.mapsdksdemo.composables.iconbuttons

import android.annotation.SuppressLint
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.composables.icons.iconGPSFilter
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@SuppressLint("MissingPermission")
@Composable
fun ToggleFilterButton(
    vm: MainViewModel = viewModel()
) {
    val currentFilter = vm.gpsUpdatesManager.currentFilter.asStateFlow().collectAsState()
    var filtersPopup by vm.filtersPopup

    return IconButton(
        onClick = {
            filtersPopup = !filtersPopup
        }
    ) {
        iconGPSFilter(currentFilter.value)
    }
}