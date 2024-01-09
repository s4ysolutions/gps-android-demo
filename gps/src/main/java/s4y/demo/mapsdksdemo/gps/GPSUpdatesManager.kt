package s4y.demo.mapsdksdemo.gps

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSFilterProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesStore
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import java.io.Closeable

class GPSUpdatesManager(
    private val updatesProvider: IGPSUpdatesProvider,
    private val store: IGPSUpdatesStore,
    private val filterProvider: IGPSFilterProvider,
    getUpdatesScope: CoroutineScope,
) : Closeable {
    // listen to bulk updates from store, filter them and redirect to _all
    private val _all = MutableStateFlow<Array<GPSUpdate>>(emptyArray())
    private val updateAllJob = store
        .updates
        .map { filterProvider.filter.apply(it) }
        .onEach { _all.value = it }
        .launchIn(getUpdatesScope)

    // listen to updates from store, filter them and redirect to _last
    private val _last = MutableSharedFlow<GPSUpdate>(1, 0,BufferOverflow.DROP_LATEST)
    private val updateLastJob = store
        .lastUpdate
        .map { filterProvider.filter.apply(it) }
        .filterNotNull()
        .onEach { _last.tryEmit(it) }
        .launchIn(getUpdatesScope)

    // listen to updates from gpsProvider and save them to store (no filtering)
    private val updateStoreJob: Job = updatesProvider.updates.asSharedFlow()
        .onEach { store.add(it) }
        .launchIn(getUpdatesScope)

    // listen to filter changes and update _all in order UI to be updated
    private val filterSwitchJob = filterProvider.asStateFlow()
        .onEach {
            _all.value = filterProvider.filter.apply(store.updates.value)
        }
        .launchIn(getUpdatesScope)

    override fun close() {
        updateStoreJob.cancel()
        updateAllJob.cancel()
        updateLastJob.cancel()
        filterSwitchJob.cancel()
    }

    // expose store bulk updates
    interface IAll {
        fun asStateFlow(): StateFlow<Array<GPSUpdate>>
        suspend fun snapshot(): Array<GPSUpdate>
    }

    val all = object : IAll {
        override fun asStateFlow(): StateFlow<Array<GPSUpdate>> = _all
        override suspend fun snapshot(): Array<GPSUpdate> = store.updates.first()
    }

    // expose store last update
    interface ILast {
        val status: IGPSProvider.IStatus
        fun asSharedFlow(): SharedFlow<GPSUpdate>
    }

    val last = object : ILast {
        override val status: IGPSProvider.IStatus = updatesProvider.status
        override fun asSharedFlow(): SharedFlow<GPSUpdate> = _last
    }

    // listen to gpsFilter changes
    interface IFilter {
        fun set(filter: GPSFilter)
        fun asStateFlow(): StateFlow<GPSFilter>
    }

    val filter = object : IFilter {
        override fun set(filter: GPSFilter) {
            filterProvider.filter = filter
        }
        override fun asStateFlow() = filterProvider.asStateFlow()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startRecording(context: Context, looper: Looper? = null) {
        store.clear()
        continueRecording(context, looper)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun continueRecording(context: Context, looper: Looper? = null) {
        updatesProvider.startUpdates(context, looper)
    }

    fun stopRecording() = updatesProvider.stopUpdates()
}