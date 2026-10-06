package com.example.alertamujer.presentation.alerta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.util.SessionManager

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val sessionManager = SessionManager(application)
    private val idUsuarioActual = sessionManager.obtenerIdUsuario()

    // Conexión reactiva directa a Room
    val historialAlertas: LiveData<List<AlertaEntity>> = db.alertaDao().obtenerTodasMisAlertas(idUsuarioActual)
}