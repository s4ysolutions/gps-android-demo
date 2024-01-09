package s4y.demo.mapsdksdemo.composables

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.composables.iconbuttons.CenterCurrentPositionIconButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.RecordingIconButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.SaveAsFileButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.ShowStatsButton
import s4y.demo.mapsdksdemo.composables.menus.GPSFiltersDropdownMenu
import s4y.demo.mapsdksdemo.composables.menus.SwitchMapDropdownMenu
import s4y.demo.mapsdksdemo.di.Di

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Root() {
    val accessFineLocationState =
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val accessCoarseLocationState =
        rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val safeGps = { body: () -> Unit ->
        if (accessFineLocationState.status.isGranted) {
            body()
        } else if (!accessFineLocationState.status.shouldShowRationale) {
            accessFineLocationState.launchPermissionRequest()
        } else if (accessCoarseLocationState.status.isGranted) {
            body()
        } else if (accessCoarseLocationState.status.shouldShowRationale) {
            accessCoarseLocationState.launchPermissionRequest()
        } else {
            Toast.makeText(context, "No permission", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { AppMenu()}
        },
    ) {
     */
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Map SDKs Demo")
                },
                actions = {
                    IconButton(
                        onClick = {/*
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
*/
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                SwitchMapDropdownMenu()
                CenterCurrentPositionIconButton(safeGps = safeGps)
                RecordingIconButton(safeGps = safeGps)
                // ToggleFilterButton(safeGps = safeGps)
                GPSFiltersDropdownMenu()
                SaveAsFileButton()
                ShowStatsButton {
                    showBottomSheet = true
                }
            }
        },
        /*
    floatingActionButton = {
        FloatingActionButton(onClick = { presses++ }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
     */
    ) { innerPadding ->
        // Column
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            //verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Map(
                modifier = Modifier.fillMaxSize()
            )
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    Column(modifier = Modifier
                        .padding(bottom = 32.dp)
                        .fillMaxWidth()) {
                        val updates = Di.gpsUpdatesManager.all.snapshot
                        val stats = Di.gpsStats.velocityStats(updates)
                        Text("Velocity stats")
                        Text(" Gps: µ=${f(stats.meanGps)} σ=${f(stats.sDeviationGps)}")
                        Text(" Eval: µ=${f(stats.meanEval)} σ=${f(stats.sDeviationEval)}")
                        Text(" Gps.prev. - Eval: µ=${f(stats.meanGpsEvalPrev)} σ=${f(stats.sDeviationGpsEvalPrev)}")
                        Text(" Gps.curr. - Eval: µ=${f(stats.meanEvalGpsCurrent)} σ=${f(stats.sDeviationEvalGpsCurrent)}")
                        Text(" Gps.avg.  - Eval: µ=${f(stats.meanEvalGpsAvg)} σ=${f(stats.sDeviationEvalGpsAvg)}")
                        Text("Max acceleration(eval): ${f(stats.maxAccelerationEval)}")
                        Text("Max acceleration: ${f(stats.maxAcceleration)}")
                        Text("Min dt: ${f(stats.minDt)}")
                        Text("Max dt: ${f(stats.maxDt)}")
                        Button(
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            }) {
                            Text("Hide bottom sheet")
                        }
                    }
                }
            }
        }
    }
}

private fun f(f: Float): String {
    return "%.5f".format(f)
}

private fun f(f: Double): String {
    return "%.5f".format(f)
}
