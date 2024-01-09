package s4y.demo.mapsdksdemo.map.dependencies.layers

interface ILayersContainer<T : ILayer> {
    fun addLayer(layer: T)
    fun removeLayer(layer: T)
    val defaultLayer: T
}