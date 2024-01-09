package s4y.demo.mapsdksdemo.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.Application
import s4y.demo.mapsdksdemo.appstate.GPSFilterPreference
import s4y.demo.mapsdksdemo.appstate.LastMapStatePreference
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSCurrentPositionProvider
import s4y.demo.mapsdksdemo.gps.filters.dependencies.IGPSFilterProvider
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesStorage
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesStore
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import s4y.demo.mapsdksdemo.gps.filters.GPSFilters
import s4y.demo.mapsdksdemo.gps.DownloadDirectoryGPSUpdatesFileStorage
import s4y.demo.mapsdksdemo.gps.GPSCurrentPositionManager
import s4y.demo.mapsdksdemo.gps.GPSUpdatesManager
import s4y.demo.mapsdksdemo.gps.implementation.FusedGPSCurrentPositionProvider
import s4y.demo.mapsdksdemo.gps.implementation.FusedGPSUpdatesProvider
import s4y.demo.mapsdksdemo.gps.store.ArrayGPSUpdatesStore
import s4y.demo.mapsdksdemo.map.MapsManager
import s4y.demo.mapsdksdemo.mapsforgevtm.MapsforgeMapFactory
import s4y.demo.mapsdksdemo.mapsforgevtm.VtmMapFactory
import s4y.demo.mapsdksdemo.gps.stats.GPSStats

class Di {
    companion object {
        private lateinit var application: Application
        private val appContext: Context
            get() = application

        fun init(application: Application) {
            this.application = application
        }

        val gpsFilters by lazy {
            GPSFilters.instance
        }

        val mapsManager: MapsManager by lazy {
            MapsManager(
                listOf(
                    MapsforgeMapFactory(application),
                    VtmMapFactory()
                )
            )
        }

        val gpsUpdatesManager: GPSUpdatesManager by lazy {
            val gpsUpdateProvider: IGPSUpdatesProvider = FusedGPSUpdatesProvider(appContext)

            val gpsUpdatesFileStorage: IGPSUpdatesStorage =
                DownloadDirectoryGPSUpdatesFileStorage(appContext)

            val gpsUpdatesStore: IGPSUpdatesStore = ArrayGPSUpdatesStore(500, gpsUpdatesFileStorage)

            val gpsFilterProvider = object : IGPSFilterProvider {
                private val preference = GPSFilterPreference(application)
                override var filter: GPSFilter by preference
                override fun asStateFlow(): StateFlow<GPSFilter> = preference.asStateFlow()
            }

            GPSUpdatesManager(
                gpsUpdateProvider,
                gpsUpdatesStore,
                gpsFilterProvider,
                CoroutineScope(Dispatchers.IO + SupervisorJob())
            )
        }

        private val gpsCurrentPositionProvider: IGPSCurrentPositionProvider by lazy {
            FusedGPSCurrentPositionProvider(appContext)
        }

        val gpsCurrentPositionManager: GPSCurrentPositionManager by lazy {
            GPSCurrentPositionManager(gpsCurrentPositionProvider)
        }

        val lastMapStatePreference by lazy {
            LastMapStatePreference(appContext)
        }

        val gpsStats = GPSStats()
    }
}