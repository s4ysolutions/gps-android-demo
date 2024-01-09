package s4y.demo.mapsdksdemo.map.dependencies

import android.app.Activity
import java.io.Closeable

interface IMapFactory: Closeable {
   fun newMap(activity: Activity, initialState: IMapState): IMap
}