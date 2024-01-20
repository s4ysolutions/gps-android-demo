package s4y.demo.mapsdksdemo.composables

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.lib.cast.toMapPosition
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@Composable
fun Map(modifier: Modifier = Modifier, vm: MainViewModel = viewModel()) {
    val updates by vm.gpsUpdatesManager.all.asStateFlow().collectAsState()
    fun updateMap() {
        val mapPosition = Array(updates.size) { updates[it].toMapPosition() }
        vm.map.trackLayers.defaultLayer.setPositions(mapPosition)
    }

    var mapInitialized by remember {
        mutableStateOf(false)
    }
    val activity = LocalContext.current as Activity
    LaunchedEffect(activity) {
        vm.initMap(activity)
        mapInitialized = true
    }

    LaunchedEffect(mapInitialized) {
        if (mapInitialized) {
            // add last position to map
            vm.gpsUpdatesManager.last.asSharedFlow().onEach {
                vm.map.trackLayers.defaultLayer.addPosition(it.toMapPosition())
            }.launchIn(this)
            updateMap()
            val mapPosition = Array(updates.size) { updates[it].toMapPosition() }
            vm.map.trackLayers.defaultLayer.setPositions(mapPosition)
        }
    }

    LaunchedEffect(updates) {
        if (mapInitialized) {
            updateMap()
        }
    }

    if (mapInitialized) {
        AndroidView(
            factory = {
                vm.map.view
            },
            modifier = modifier
        )
    }
}