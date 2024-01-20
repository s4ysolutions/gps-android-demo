package s4y.demo.mapsdksdemo.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.composables.Map
import s4y.demo.mapsdksdemo.composables.iconbuttons.ToggleKalmanButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.RecordingIconButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.CenterCurrentPositionIconButton
import s4y.demo.mapsdksdemo.lib.cast.toMapPosition
import s4y.demo.mapsdksdemo.ui.theme.MapSDKsDemoTheme
import s4y.demo.mapsdksdemo.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapSDKsDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Root()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Root(vm: MainViewModel = viewModel()) {
    // val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val accessFineLocationState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val accessCoarseLocationState =
        rememberPermissionState(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    val isUpdatingActive by vm.gpsUpdatesManager.last.status.asStateFlow().collectAsState()
    val isRequestingCurrentPosition by vm.gpsCurrentPositionManager.status.asStateFlow()
        .collectAsState()

    val context = LocalContext.current

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
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Top app bar")
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
                CenterCurrentPositionIconButton(safeGps)
                RecordingIconButton(isRecording = isUpdatingActive, safeGps = safeGps)
                ToggleKalmanButton(safeGps = safeGps)
                /*
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Bottom app bar",
            )
             */
                /*
            IconButton(
                onClick = {
                    if (!isRequestingCurrentPosition) safeGps {
                        vm.updateCenter(context)
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
             */
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
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Map(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
    /*
    }
     */
}