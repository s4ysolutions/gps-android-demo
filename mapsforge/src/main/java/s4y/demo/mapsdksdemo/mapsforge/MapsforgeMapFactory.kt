package s4y.demo.mapsdksdemo.mapsforge

import android.app.Activity
import android.app.Application
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import s4y.demo.mapsdksdemo.map.dependencies.IMap
import s4y.demo.mapsdksdemo.map.dependencies.IMapFactory
import s4y.demo.mapsdksdemo.map.dependencies.IMapState

// TODO: cache lifecycle should be bigger than activity lifecycle
class MapsforgeMapFactory(application: Application) : IMapFactory {
    init {
        AndroidGraphicFactory.createInstance(application)
    }

    override fun newMap(activity: Activity, initialState: IMapState): IMap =
        MapsforgeMap(activity, initialState)

    override fun close() {
        AndroidGraphicFactory.clearResourceMemoryCache()
    }
}