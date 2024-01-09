package s4y.demo.mapsdksdemo.map.dependencies

import android.app.Activity
import s4y.demo.mapsdksdemo.map.MapId
import java.io.Closeable

interface IMapFactory: Closeable {
   val mapId: MapId
   val name: String
   fun newMap(activity: Activity, initialState: IMapState): IMap
}