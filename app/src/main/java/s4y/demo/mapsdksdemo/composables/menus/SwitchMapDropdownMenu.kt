package s4y.demo.mapsdksdemo.composables.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.composables.iconbuttons.ToggleMapsButton
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel


@Composable
fun SwitchMapDropdownMenu(
    vm: MainViewModel = viewModel()
) {
    var expanded by vm.mapsPopup

    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        ToggleMapsButton()
        // menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            vm.mapsManager.map { factory ->
                DropdownMenuItem(
                    onClick = {
                        vm.mapsManager.switchTo(factory.mapId)
                        expanded = false
                    },
                    text = {
                        Text(factory.name)
                    },
                    leadingIcon = {
                        if (factory.mapId == vm.mapsManager.current.mapId)
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Localized description"
                            )
                    })
            }
        }
    }
}