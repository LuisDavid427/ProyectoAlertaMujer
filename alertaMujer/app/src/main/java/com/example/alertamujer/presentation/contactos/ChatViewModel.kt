package com.example.alertamujer.presentation.contactos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.util.SessionManager

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val sessionManager = SessionManager(application)

    // Consulta únicamente las alertas ajenas filtrando por el ID de la sesión actual
    val listaAlertas: LiveData<List<AlertaEntity>> = run {
        val idUsuarioActual = sessionManager.obtenerIdUsuario()
        db.alertaDao().obtenerAlertasDeOtrosUsuarios(idUsuarioActual)
    }
}