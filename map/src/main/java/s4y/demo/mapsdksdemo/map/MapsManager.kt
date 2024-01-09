package s4y.demo.mapsdksdemo.map

import s4y.demo.mapsdksdemo.map.dependencies.IMapFactory

class MapsManager(
    private val factories: Map<MapType, IMapFactory>
) {
    fun mapFactory(id: MapType): IMapFactory =
        factories[id] ?: throw IllegalArgumentException("No factory for map $id")

    val defaultMapFactory: IMapFactory = factories.values.first()
}