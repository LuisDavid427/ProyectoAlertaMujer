package com.example.alertamujer.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

data class GpsVisualConfig(
    val texto: String,
    val color: Int
)

object PermissionUtils {
    const val UBICACION_PRECISA = "PRECISA"
    const val UBICACION_APROXIMADA = "APROXIMADA"
    const val SIN_PERMISO = "NO"

    fun tienePermisoUbicacion(context: Context): String {
        val fineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return when {
            fineLocation == PackageManager.PERMISSION_GRANTED ->
                UBICACION_PRECISA

            coarseLocation == PackageManager.PERMISSION_GRANTED ->
                UBICACION_APROXIMADA

            else ->
                SIN_PERMISO
        }
    }

    fun obtenerEstadoUbicacion(context: Context): String {
        val estado = tienePermisoUbicacion(context)
        return when (estado) {
            UBICACION_PRECISA -> "CONFIGURACION"
            UBICACION_APROXIMADA -> "PEDIR_PRECISA"
            else -> "PEDIR_INICIAL"
        }
    }

    fun obtenerVisualGps(context: Context): GpsVisualConfig {
        return when (tienePermisoUbicacion(context)) {
            UBICACION_PRECISA -> GpsVisualConfig(
                "GPS: ALTA PRECISIÓN",
                android.R.color.holo_green_dark
            )

            UBICACION_APROXIMADA -> GpsVisualConfig(
                "GPS: APROXIMADO",
                android.R.color.holo_orange_dark
            )

            else -> GpsVisualConfig(
                "GPS: OFF",
                android.R.color.holo_red_dark
            )
        }
    }

    // 🔔 Valida si las notificaciones están permitidas a nivel de sistema operativo (Android 8+)
    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun abrirAjustesSistema(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // 🔔 Abre directamente la configuración de notificaciones de la app
    fun abrirAjustesNotificaciones(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    }
}