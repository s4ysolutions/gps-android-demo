package s4y.demo.mapsdksdemo.map.dependencies

import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseMap: IMap {
    private val stateChangeListeners = CopyOnWriteArrayList<(IMapState) -> Unit>()
    override fun addStateChangeListener(listener: (IMapState) -> Unit) {
        stateChangeListeners.add(listener)
    }

    override fun removeStateChangeListener(listener: (IMapState) -> Unit) {
        stateChangeListeners.remove(listener)
    }

    protected fun notifyStateChangeListeners(state: IMapState) {
        val s = state.clone()
        stateChangeListeners.forEach { it(s) }
    }
}