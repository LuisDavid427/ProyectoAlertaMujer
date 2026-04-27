package com.example.alertamujer.presentation.contactos

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONObject

class AddContactoViewModel(application: Application) : AndroidViewModel(application) {

    private val _guardadoExitoso = MutableLiveData<Boolean>()
    val guardadoExitoso: LiveData<Boolean> get() = _guardadoExitoso

    fun guardarContacto(nombre: String, numero: String, index: Int) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        val nuevoContacto = JSONObject()
        nuevoContacto.put("nombre", nombre)
        nuevoContacto.put("numero", numero)

        if (index != -1) {
            contactosArray.put(index, nuevoContacto)
        } else {
            contactosArray.put(nuevoContacto)
        }

        editor.putString("contactos", contactosArray.toString())
        editor.apply()

        _guardadoExitoso.value = true
    }
}