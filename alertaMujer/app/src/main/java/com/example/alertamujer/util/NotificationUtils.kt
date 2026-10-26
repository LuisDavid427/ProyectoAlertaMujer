package com.example.alertamujer.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import com.example.alertamujer.R

class NotificationUtils(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(channelId: String, channelName: String, descriptionText: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra una notificación push configurable permitiendo definir la Activity de destino al hacer clic.
     */
    fun showNotification(
        channelId: String,
        title: String,
        content: String,
        notificationId: Int,
        colorHex: String? = null,
        targetActivityClass: Class<*> = com.example.alertamujer.ui.contactos.ChatsActivity::class.java
    ) {
        val intent = Intent(context, targetActivityClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alert)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (colorHex != null) {
            val formattedTitle = "<font color='$colorHex'>$title</font>"
            notificationBuilder.setContentTitle(Html.fromHtml(formattedTitle, Html.FROM_HTML_MODE_LEGACY))
        } else {
            notificationBuilder.setContentTitle(title)
        }

        notificationBuilder.setContentText(content)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}