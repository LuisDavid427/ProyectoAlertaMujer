package com.example.alertamujer.presentation.contactos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.local.AppDatabase
import kotlinx.coroutines.launch
import com.example.alertamujer.data.local.entity.ContactoEntity

class AddContactoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val _guardadoExitoso = MutableLiveData<Boolean>()
    val guardadoExitoso: LiveData<Boolean> get() = _guardadoExitoso

    // Acepta el id opcional. Si no se pasa, por defecto es 0 (nuevo registro)
    fun guardarContacto(nombre: String, numero: String, email: String, id: Int = 0) {
        viewModelScope.launch {
            val contacto = ContactoEntity(
                id = id, // Si viene con ID del lápiz, Room lo actualizará. Si es 0, creará uno nuevo.
                nombre = nombre,
                numero = numero,
                email = email
            )

            // Usamos insertarContacto, que gracias al INSERT OR REPLACE actúa como crear o actualizar
            db.contactoDao().insertarContacto(contacto)
            _guardadoExitoso.postValue(true)
        }
    }
}