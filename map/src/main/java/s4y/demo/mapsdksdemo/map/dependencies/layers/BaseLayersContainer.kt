package s4y.demo.mapsdksdemo.map.dependencies.layers

abstract class BaseLayersContainer<T:ILayer>: ILayersContainer<T>{
    private val layers = mutableListOf<T>()
    override fun addLayer(layer: T) {
        layers.add(layer)
    }
    override fun removeLayer(layer: T) {
        layers.remove(layer)
    }

    override val defaultLayer: T
        get() {
            if (layers.isEmpty()) {
                addLayer(createDefaultLayer())
            }
            return layers[0]
        }

    protected abstract fun createDefaultLayer(): T
}