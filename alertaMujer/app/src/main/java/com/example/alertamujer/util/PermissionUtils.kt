package com.example.alertamujer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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

    fun obtenerVisualGps(context: Context): GpsVisualConfig{
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
                android.R.color.holo_red_dark)
        }
    }
    fun abrirAjustesSistema(context: Context) {
        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            val uri = android.net.Uri.fromParts("package", context.packageName, null)
            data = uri
            // Si llamamos esto desde fuera de una actividad, necesitamos este flag
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    fun obtenerDialogoPermisoManual(context: Context): androidx.appcompat.app.AlertDialog.Builder {
        return androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Permiso denegado")
            .setMessage("Para que la Alerta de Pánico funcione, necesitas activar el GPS manualmente en los ajustes de la aplicación.")
            .setPositiveButton("IR A AJUSTES") { _, _ ->
                abrirAjustesSistema(context)
            }
            .setNegativeButton("CANCELAR", null)
    }
}
