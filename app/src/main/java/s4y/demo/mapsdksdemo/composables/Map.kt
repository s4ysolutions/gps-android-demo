package s4y.demo.mapsdksdemo.composables

import android.app.Activity
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import s4y.demo.mapsdksdemo.lib.cast.toMapPosition
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

@Composable
fun Map(modifier: Modifier, vm: MainViewModel = viewModel()) {
    var activityInitialized by remember {
        mutableStateOf(false)
    }

    val activity = LocalContext.current as Activity
    val mapState = vm.mapStateFlow.collectAsState()
    //val mapId = vm.mapsManager.stateFlow.collectAsState()

    // trigger map creation when activity is initialized
    LaunchedEffect(activity) {
        activityInitialized = true
        vm.setupMap(activity)
    }

    LaunchedEffect(activityInitialized) {
        if (activityInitialized) {
            // TODO: this should obey onPause/onResume lifecycle
            // add last position to map
            vm.gpsUpdatesManager.last.asSharedFlow().onEach {
                mapState
                    .value
                    ?.trackLayers
                    ?.defaultLayer
                    ?.addPosition(it.toMapPosition())
            }.launchIn(this)
            // track filter changes and initial update
            vm.gpsUpdatesManager.all.asStateFlow().onEach { updates ->
                mapState.value?.let { map ->
                    val mapPosition = Array(updates.size) { updates[it].toMapPosition() }
                    map.trackLayers.defaultLayer.setPositions(mapPosition)
                }
            }.launchIn(this)
            // recreate map when mapId changes (usually by user action)
            vm.mapsManager.stateFlow.onEach {
                if (activityInitialized && mapState.value?.id != it.mapId) {
                    vm.setupMap(activity)
                }
            }.launchIn(this)
        }
    }

    Box {
        key(mapState.value) {
            AndroidView(
                factory = { context ->
                    mapState.value?.view ?: View(context)
                },
                modifier = modifier
            )
        }
        Row(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.BottomEnd)
        ) {
            OutlinedButton(modifier =
            Modifier
                .padding(8.dp)
                .width(64.dp)
                .height(64.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Black.copy(alpha = 0.4f)
                ),
                onClick = {
                    mapState.value?.let {
                        it.state.zoom = it.state.zoom + 1
                    }
                }) {
                Text(
                    "+",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(8.dp)
                    .width(64.dp)
                    .height(64.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Black.copy(alpha = 0.4f)
                ),
                onClick = {
                    mapState.value?.let {
                        it.state.zoom = it.state.zoom - 1
                    }
                }) {
                Text(
                    "-",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}