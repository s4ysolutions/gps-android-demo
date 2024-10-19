## MapsForge configuration

create directory `mapsforge-maps/src/main/res/raw` by right click on
`mapsforge-maps` -> `New` -> `Android Resource directory` -> `Resource type: raw`

Download a map from [Mapsforge](https://download.mapsforge.org/maps/v5/) and copy it to
`mapsforge-maps/src/main/res/raw`

Then change `mapsforge-maps/src/main/java/s4y/demo/mapsdksdemo/mapsforge/maps/MapsforgeMaps.kt`
according to the map file name.

## MapBox configuration

add your [MapBox access token](https://docs.mapbox.com/android/maps/guides/install/) to
`local.properties` file:
```
MAPBOX_DOWNLOADS_TOKEN=pk. ...
MAPBOX_ACCESS_TOKEN=sk. ...
```

`:app` module `build.gradle.kts`:
```kotlin
import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
     localProperties.load(FileInputStream(localPropertiesFile))
}
val mapboxAccessToken = localProperties.getProperty("MAPBOX_ACCESS_TOKEN")

android {
    // ...
    buildFeatures {
        // ...
        buildConfig = true
    }

    defaultConfig {
        // ...
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxAccessToken\"")
    }
}
```

## Adding new map provider

_On the example of Mapbox_

 1. Create an Android library module ":mapsbox-maps" with a dependency on `:map` module

```
implementation(project(":map"))
```

### Implement `Map` interface inhertiing from `BaseMap` 

Create stubs overriding properties and methods

 2. Implement id property which will identify the Mapbox map
     in the rest of the application


```kotlin
class MapboxMap: BaseMap()   {
    companion object {
    const val mapType = "mapbox"
    }
    
    override val currentGPSPositionLayer: ICurrentGPSPositionLayer
        get() = TODO("Not yet implemented")

    override val id: MapId
        get() = mapType
        
    override val trackLayers: ILayersContainer<ITrackLayer>
        get() = TODO("Not yet implemented")
        
    override val view: MapView = ...
    
    override val state: IMapStateMutable = object : IMapStateMutable {
        override var center: MapPosition
            get() = TODO("Not yet implemented")
            set(value) {}
        override var zoom: Int
            get() = TODO("Not yet implemented")
            set(value) {}
    }
    
    override fun close() {
    }
        
    override fun pause() {
    }

    override fun resume() {
    }

}
```

 3. Implement `MapView` property according to
https://docs.mapbox.com/android/maps/guides/install/#runtime-layout. This property 
whil be used as a factory for AndroidView composable in the s4y.demo.mapsdksdemo.composables.Map
composable responsible for rendering the map.

```kotlin
    override val view: MapView = MapView(activity).apply {
}
```

 4. Implement a factory for the MapboxMap instances
The purpose of the factory is to provide the information about the map before the very first
instance is created (i.e. a title for a menu item) and initialize the framework if needed.

```kotlin
// most probably a singleton
class MapboxMapFactory(mapBoxAccessToken: String) : IMapFactory {
    override val mapId: MapId = MapboxMap.mapType

    override val name: String = "Mapbox"
    
    init {
        // initialize the framework if needed
         MapboxOptions.accessToken = mapBoxAccessToken
    }

    override fun close() {
        // clean up the resources used by the framework if needed
    }

    override fun newMap(
        activity: Activity,
        initialState: IMapState
    ): IMap = MapboxMap(activity, initialState)
}
```
 5. Use :mapbox in :app
 5.1. Add dependency to `:mapbox` module
 5.2. Enhance DI class with the new factory

`s4y.demo.mapsdksdemo.di.DI`
```kotlin
        val mapsManager: MapsManager by lazy {
            MapsManager(
                listOf(
                    MapsforgeMapFactory(application),
                    VtmMapFactory(),
                    MapboxMapFactory(BuildConfig.mapboxAccessToken)), // <--- add this line
                )
            )
        }
```

At this point the App can be built and the "Mapbox" option should appear in the menu.

  6. Provide state to :app

  6.1. Coordinates helpers
The application uses `MapPosition` class to represent a position on the map. It is convinient to
add helpers converting between `MapPosition` and the framework specific types.

```kotlin
fun Point.toMapPosition(): MapPosition = MapPosition(latitude(), longitude())
fun MapPosition.toPoint(): Point = Point.fromLngLat(longitude, latitude)
```

  6.2. Implement state accessors
```kotlin
    override val state: IMapStateMutable = object : IMapStateMutable {
     override var center: MapPosition
          get() = view.mapboxMap.cameraState.center.toMapPosition()
          set(value) {
               view.mapboxMap.setCamera(
                    CameraOptions.Builder()
                         .center(com.mapbox.geojson.Point.fromLngLat(value.longitude, value.latitude))
                         .zoom(view.mapboxMap.cameraState.zoom)
                         .build()
               )
          }
     override var zoom: Int
          get() = view.mapboxMap.cameraState.zoom.toInt()
          set(value) {
               view.mapboxMap.setCamera(
                    CameraOptions.Builder()
                         .center(view.mapboxMap.cameraState.center)
                         .zoom(value.toDouble())
                         .build()
               )
          }
}
```

 7. Init map view state

And now we have all the tools to init map view state

```kotlin
init{
     state.center = initialState.center
     state.zoom = initialState.zoom
}
```

 8. Implement base (GPS) layer

Mapbox does not require any special layer to display the current GPS position, so
we can provide dummy implementation.

```kotlin
    override val trackLayers = object : BaseLayersContainer<ITrackLayer>() {
        override fun createDefaultLayer(): ITrackLayer {
            return object : ITrackLayer {
                override fun setPositions(positions: Array<MapPosition>) {
                }

                override fun addPosition(position: MapPosition) {
                }

                override var isVisible: Boolean
                    get() = false
                    set(value) {}
            }
        }
    }
```
In this point it is possible to switch to Mapbox map using menu item. Moreover it is expected to
reflect the center and zoom level of the existing maps.

 9. Handle map events

```kotlin
    private val onCameraChangeCancelable: Cancelable
    
    private val onCameraChangeCancelable: Cancelable
    init {
        ...

        onCameraChangeCancelable = view.mapboxMap.subscribeCameraChanged{
            notifyStateChangeListeners(state)
        }
    }

    override fun close() {
        onCameraChangeCancelable.cancel()
    }
```

Now the changes of zoom and map center are reflected in the application state.

 10. Implement the layer to show GPS track.
   Assuming that we should not share the style between the layers, we can provide a naive
   implementation using the default style and mapbox LineLayer:

 ```kotlin
class MapsforgeTrackLayer(private val mapView: MapView) : ITrackLayer {
    internal val layer = Polyline(
        AndroidGraphicFactory.INSTANCE.createPaint()
            .apply {
                color = AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE)
                strokeWidth = 8 * mapView.model.displayModel.scaleFactor
                setStyle(Style.STROKE)
            }, AndroidGraphicFactory.INSTANCE
    ).apply {
        isVisible = true
    }

    override fun setPositions(positions: Array<MapPosition>) {
        layer.setPoints(positions.map { it.latLong })
        mapView.postInvalidate()
        layer.requestRedraw()
    }

    override fun addPosition(position: MapPosition) {
        layer.addPoint(position.latLong)
        layer.requestRedraw()
    }

    override var isVisible: Boolean
        get() = layer.isVisible
        set(value) {
            layer.isVisible = value
        }
}
```
next use this clase as defautl base layer:

```kotlin
class MapboxMap(activity: Activity, initialState: IMapState) : BaseMap() {
    ...
     override val trackLayers = object : BaseLayersContainer<ITrackLayer>() {
          override fun createDefaultLayer(): ITrackLayer {
               return MapBoxTrackLayer(view)
          }
     }
}
```

That's all the application is smart enough to switch between the maps and keep the track visible.

11. Implement the layer to show GPS position

Currently app does not show the current GPS position but is good idea to prepare [the layer](mapbox/src/main/java/s4y/demo/mapsdksdemo/mapbox/layers/MapboxCurrentGPSPositionLayer.kt) to show
it, so the layer can be added to the map in the future.
