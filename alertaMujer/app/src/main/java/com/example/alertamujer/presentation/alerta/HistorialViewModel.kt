package com.example.alertamujer.presentation.historial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.AlertaEntity

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    // Conexión reactiva directa a Room
    val historialAlertas: LiveData<List<AlertaEntity>> = db.alertaDao().obtenerTodasLasAlertas()
}