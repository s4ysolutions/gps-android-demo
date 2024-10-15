package s4y.demo.mapsdksdemo.mapsforge.maps

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class MapsforgeMaps {
    fun defaultMapFile(context: Context): File {
        // copy resource serbia.map to local storage
        val file = File(context.filesDir, "serbia.map")

        if (!file.exists()) {
            // https://download.mapsforge.org/maps/v5/europe/
            val input = context.resources.openRawResource(R.raw.serbia)
            val output = FileOutputStream(file)
            input.copyTo(output)
            input.close()
            output.close()
        }
        return file
    }
}