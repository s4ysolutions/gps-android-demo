package s4y.demo.mapsdksdemo.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import s4y.demo.mapsdksdemo.Application
import s4y.demo.mapsdksdemo.appstate.GPSFilterPreference
import s4y.demo.mapsdksdemo.gps.GPSCurrentPositionManager
import s4y.demo.mapsdksdemo.gps.GPSUpdatesManager
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSCurrentPositionProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSFilterProvider
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesStore
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import s4y.demo.mapsdksdemo.gps.implementation.AndroidGPSCurrentPositionProvider
import s4y.demo.mapsdksdemo.gps.implementation.AndroidGPSUpdatesProvider
import s4y.demo.mapsdksdemo.gps.implementation.ArrayGPSUpdatesStore
import s4y.demo.mapsdksdemo.map.MapsManager
import s4y.demo.mapsdksdemo.mapsforge.MapsforgeMap
import s4y.demo.mapsdksdemo.mapsforge.MapsforgeMapFactory

class Di {
    companion object {

        private lateinit var application: Application
        val appContext: Context
            get() = application

        fun init(application: Application) {
            this.application = application
        }


        val mapsManager: MapsManager by lazy {
            MapsManager(
                mapOf(
                    MapsforgeMap.mapType to MapsforgeMapFactory(application)
                )
            )
        }

        private val gpsUpdateProvider: AndroidGPSUpdatesProvider by lazy {
            AndroidGPSUpdatesProvider()
        }

        private val gpsUpdatesStore: IGPSUpdatesStore by lazy {
            ArrayGPSUpdatesStore(1000)
        }


        val gpsUpdatesManager: GPSUpdatesManager by lazy {
            val gpsFilterProvider = object :
                IGPSFilterProvider {
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
            AndroidGPSCurrentPositionProvider()
        }
        val gpsCurrentPositionManager: GPSCurrentPositionManager by lazy {
            GPSCurrentPositionManager(gpsCurrentPositionProvider)
        }
    }
}