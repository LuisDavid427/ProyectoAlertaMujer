package com.example.alertamujer.presentation.contactos

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.local.AppDatabase
import kotlinx.coroutines.launch
import com.example.alertamujer.data.local.entity.ContactoEntity
import com.example.alertamujer.data.local.dao.ContactoDao
import org.json.JSONArray
import org.json.JSONObject

class AddContactoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val _guardadoExitoso = MutableLiveData<Boolean>()
    val guardadoExitoso: LiveData<Boolean> get() = _guardadoExitoso

    fun guardarContacto(nombre: String, numero: String, email: String) {
        viewModelScope.launch {
            db.contactoDao().insertarContacto(ContactoEntity(nombre = nombre, numero = numero, email = email))
            _guardadoExitoso.postValue(true)
        }
    }
}