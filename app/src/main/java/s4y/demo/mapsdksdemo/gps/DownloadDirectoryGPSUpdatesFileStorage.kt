package s4y.demo.mapsdksdemo.gps

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesStorage
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesSerializer
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class DownloadDirectoryGPSUpdatesFileStorage(private val context: Context) :
    IGPSUpdatesStorage {
    override fun save(gpsUpdates: Array<GPSUpdate>, serializer: IGPSUpdatesSerializer): String {
        val serialized = serializer.serialize(gpsUpdates)

        val current = System.currentTimeMillis()
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val formatted = formatter.format(current)

        val fileName = "gps_$formatted.csv"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val fileUri = MediaStore.Downloads.getContentUri("external")
            val uri = resolver.insert(fileUri, contentValues)
            resolver.openOutputStream(uri!!)?.use { outputStream ->
                outputStream.bufferedWriter().use { it.write(serialized) }
            }
        } else {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            file.writeText(serialized)
        }
        return fileName
    }
}