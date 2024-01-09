package s4y.demo.mapsdksdemo.gps.foregroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import s4y.demo.mapsdksdemo.gps.GPSUpdatesManager
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesProvider

/**
 * The service is managed by the [GPSUpdatesManager] and the Activity.
 *
 * It is launched when the [GPSUpdatesManager] is started and the Activity is not visible or
 * when it receives a start command from the Activity and the [GPSUpdatesManager] is started.
 */
class GPSUpdatesForegroundService : LifecycleService() {
    override fun onCreate() {
        super.onCreate()
        updatesManager?.let { updatesManager ->
            lifecycleScope.launch {
                updatesManager.status.asStateFlow().collect {
                    if (IGPSUpdatesProvider.Status.IDLE == it) {
                        stopSelf()
                    }
                }
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val inactive = updatesManager?.status?.isIdle ?: true
        if (inactive)
            stopSelf()
        else
            enterForeground()
        return START_STICKY
    }

    private fun enterForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        val notification = buildNotification()
        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildNotification(): Notification {

        // createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                notificationChannelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .also { builder ->
                notificationBuilder?.invoke(builder)
                    ?: defaultNotificationBuilder(builder)
            }
        return builder.build()
    }

    private fun defaultNotificationBuilder(builder: NotificationCompat.Builder): NotificationCompat.Builder {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(this.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        /*
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, this::class.java).setAction(ACTION_STOP_SERVICE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
         */
        return builder
            .setContentTitle("S4Y GPS Demo")
            .setContentIntent(pendingIntent)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            //   .addAction(android.R.drawable.ic_media_pause, "Stop", stopIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    }

    companion object {
        private var NOTIFICATION_ID = 1
        private var NOTIFICATION_CHANNEL_ID = "S4YGPSDemo"
        private var NOTIFICATION_CHANNEL_NAME = "S4Y GPS Demo"

        var updatesManager: GPSUpdatesManager? = null
        var notificationChannelName: String = "S4Y GPS Demo"
        var notificationBuilder: ((builder: NotificationCompat.Builder) -> NotificationCompat.Builder)? =
            null

        fun start(context: Context) {
            val intent = Intent(context, GPSUpdatesForegroundService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, GPSUpdatesForegroundService::class.java)
            context.stopService(intent)
        }
    }
}