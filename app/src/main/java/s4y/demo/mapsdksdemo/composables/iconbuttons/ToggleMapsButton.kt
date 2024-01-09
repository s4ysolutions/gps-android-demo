package s4y.demo.mapsdksdemo.composables.iconbuttons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import s4y.demo.mapsdksdemo.R
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@Composable
fun ToggleMapsButton(vm: MainViewModel = viewModel()) {
    var mapsPopup by vm.mapsPopup
    return IconButton(onClick = {
        mapsPopup = !mapsPopup
    }) {
        Icon(
            painter = painterResource(id = R.drawable.map_switch),
            contentDescription = "Localized description"
        )
    }
}