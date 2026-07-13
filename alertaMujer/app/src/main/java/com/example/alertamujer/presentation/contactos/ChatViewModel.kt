package com.example.alertamujer.presentation.contactos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.AlertaEntity

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    // El RecyclerView observará esta lista. Cuando Room cambia, la UI se actualiza sola.
    val listaAlertas: LiveData<List<AlertaEntity>> = db.alertaDao().obtenerTodasLasAlertas()

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError


}