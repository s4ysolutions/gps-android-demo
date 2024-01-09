package s4y.demo.mapsdksdemo.composables.menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalman
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@Composable
fun AppMenu(vm: MainViewModel = viewModel()) {
    val selected = vm.gpsUpdatesManager.currentFilter.asStateFlow().collectAsState()
    vm.filters.forEach { filter ->
        DropdownMenuItem(
            text = {
                val disabledText = if (filter is GPSFilterKalman) {
                    " (Disabled)"
                } else {
                    ""
                }
                if (filter == selected.value) {
                    Text(text = "- " + filter.name + disabledText)
                } else {
                    Text(text = filter.name + disabledText)
                }
            },
            leadingIcon = {
                if (filter == selected.value) {
                    Icons.Filled.Check
                }
            },
            trailingIcon = {
                if (filter == selected.value) {
                    Icons.Filled.Check
                }
            },
            onClick = {
                vm.gpsUpdatesManager.currentFilter.set(filter)
            })
        /*
    DropdownMenuItem(onClick = {
        selectedIndex = index
        // expanded = false
    }) {
        val disabledText = if (s == disabledValue) {
            " (Disabled)"
        } else {
            ""
        }
        Text(text = s + disabledText)
        }
         */
    }
}
