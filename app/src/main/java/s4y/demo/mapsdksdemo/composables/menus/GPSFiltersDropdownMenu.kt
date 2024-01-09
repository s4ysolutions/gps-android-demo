package s4y.demo.mapsdksdemo.composables.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.composables.iconbuttons.ToggleFilterButton
import s4y.demo.mapsdksdemo.composables.icons.iconGPSFilter
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@Composable
fun GPSFiltersDropdownMenu(
    vm: MainViewModel = viewModel()

) {
    var expanded by vm.filtersPopup

    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        ToggleFilterButton()
        // menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            vm.filters.map { filter ->
                DropdownMenuItem(
                    text = {
                        Text(filter.name)
                    },
                    onClick = {
                        vm.gpsUpdatesManager.currentFilter.set(filter)
                        expanded = false
                    },
                    leadingIcon = {
                        iconGPSFilter(filter)
                    }
                )
            }
        }
    }
}