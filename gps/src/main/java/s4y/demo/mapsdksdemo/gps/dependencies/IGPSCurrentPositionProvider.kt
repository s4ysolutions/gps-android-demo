package s4y.demo.mapsdksdemo.gps.dependencies

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.tasks.CancellationToken
import kotlinx.coroutines.flow.Flow
import s4y.demo.mapsdksdemo.gps.GPSUpdate

interface IGPSCurrentPositionProvider: IGPSProvider {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun request(
        context: Context,
        cancellationToken: CancellationToken? = null
    ): Flow<GPSUpdate>
}