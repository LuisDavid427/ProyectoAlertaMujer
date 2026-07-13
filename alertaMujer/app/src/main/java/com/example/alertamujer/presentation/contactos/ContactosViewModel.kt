package com.example.alertamujer.presentation.contactos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.ContactoEntity
import kotlinx.coroutines.launch

class ContactosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    // CORRECCIÓN: Usamos el método que devuelve LiveData
    val contactos: LiveData<List<ContactoEntity>> = db.contactoDao().obtenerTodosLosContactos()

    fun eliminarContacto(id: Int) {
        viewModelScope.launch {
            db.contactoDao().eliminarContacto(id)
        }
    }
}