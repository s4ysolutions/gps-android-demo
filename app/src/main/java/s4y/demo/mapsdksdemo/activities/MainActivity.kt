package s4y.demo.mapsdksdemo.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import s4y.demo.mapsdksdemo.composables.Map
import s4y.demo.mapsdksdemo.composables.Root
import s4y.demo.mapsdksdemo.composables.iconbuttons.RecordingIconButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.CenterCurrentPositionIconButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.SaveAsFileButton
import s4y.demo.mapsdksdemo.composables.iconbuttons.ShowStatsButton
import s4y.demo.mapsdksdemo.composables.menus.SwitchMapDropdownMenu
import s4y.demo.mapsdksdemo.composables.menus.GPSFiltersDropdownMenu
import s4y.demo.mapsdksdemo.di.Di
import s4y.demo.mapsdksdemo.gps.foregroundservice.GPSUpdatesForegroundService
import s4y.demo.mapsdksdemo.ui.theme.MapSDKsDemoTheme

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

    override fun onPause() {
        super.onPause()
        GPSUpdatesForegroundService.updatesManager = Di.gpsUpdatesManager
        GPSUpdatesForegroundService.start(this)
    }

    override fun onResume() {
        super.onResume()
        GPSUpdatesForegroundService.stop(this)
    }
}

