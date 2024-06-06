package s4y.demo.mapsdksdemo.gps

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import s4y.demo.mapsdksdemo.gps.filters.dependencies.IGPSFilterProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesStore
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import java.io.Closeable

class GPSUpdatesManager(
    private val updatesProvider: IGPSUpdatesProvider,
    private val store: IGPSUpdatesStore,
    private val filterProvider: IGPSFilterProvider,
    getUpdatesScope: CoroutineScope,
) : Closeable {
    // listen to bulk updates from store, filter them and redirect to _all
    private val _all = MutableStateFlow(filterProvider.filter.apply(store.snapshot))

    // listen to updates from store, filter them and redirect to _last
    private val _last = MutableSharedFlow<GPSUpdate>(1, 0, BufferOverflow.DROP_LATEST)
    private val updateLastJob = store
        .lastUpdate
        .mapNotNull {
            filterProvider.filter.apply(it)
        }
        .onEach { _last.tryEmit(it) }
        .launchIn(getUpdatesScope)

    // listen to updates from gpsProvider and save them to store (no filtering)
    private val updateStoreJob: Job = updatesProvider.updates.asSharedFlow()
        .onEach { store.add(it) }
        .launchIn(getUpdatesScope)

    // listen to filter changes and update _all in order UI to be updated
    private val filterSwitchJob = filterProvider.asStateFlow()
        .onEach {
            it.reset()
            val updates = store.snapshot
            if (updates.isEmpty()) return@onEach
            // NOTE: it should be updates.subRange(1, updates.size),
            // but i skip it for sake of performance
            _all.value = it.apply(updates)
        }
        .launchIn(getUpdatesScope)

    override fun close() {
        updateStoreJob.cancel()
        updateLastJob.cancel()
        filterSwitchJob.cancel()
    }

    // expose store bulk updates
    interface IAll {
        fun asStateFlow(): StateFlow<Array<GPSUpdate>>
        val snapshot: Array<GPSUpdate>
    }

    val all = object : IAll {
        override fun asStateFlow(): StateFlow<Array<GPSUpdate>> = _all
        override val snapshot get() = store.snapshot
    }

    // expose store last update
    interface ILast {
        val status: IGPSUpdatesProvider.IStatus
        fun asSharedFlow(): SharedFlow<GPSUpdate>
    }

    val last = object : ILast {
        override val status: IGPSUpdatesProvider.IStatus = updatesProvider.status
        override fun asSharedFlow(): SharedFlow<GPSUpdate> = _last
    }

    // listen to gpsFilter changes
    interface IFilter {
        fun set(filter: GPSFilter)
        fun get(): GPSFilter
        fun asStateFlow(): StateFlow<GPSFilter>
    }

    val currentFilter = object : IFilter {
        override fun set(filter: GPSFilter) {
            filterProvider.filter = filter
        }

        override fun asStateFlow() = filterProvider.asStateFlow()
        override fun get() = filterProvider.filter
    }

    fun startRecording() {
        store.clear()
        _all.value = store.snapshot
        continueRecording()
    }

    fun continueRecording() {
        updatesProvider.startUpdates()
    }

    fun stopRecording() = updatesProvider.stopUpdates()

    fun saveAsFile() = store.saveAsFile()

    val status = updatesProvider.status
}