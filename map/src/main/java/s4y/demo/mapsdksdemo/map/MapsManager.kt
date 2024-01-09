package s4y.demo.mapsdksdemo.map

import kotlinx.coroutines.flow.MutableStateFlow
import s4y.demo.mapsdksdemo.map.dependencies.IMapFactory

class MapsManager(
    private val factories: List<IMapFactory>
): List<IMapFactory> by factories {
    private val state = MutableStateFlow(factories.first())
    val current get() = state.value
    val stateFlow get() = state
    fun switchTo(id: MapId) {
        state.value = get(id)
    }
    operator fun get(id: MapId): IMapFactory = factories.firstOrNull { it.mapId == id } ?: throw IllegalArgumentException("No factory for map $id")
}