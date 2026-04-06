package com.example.alertamujer.contactos

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONObject

class AddContactoViewModel(application: Application) : AndroidViewModel(application) {

    // Usamos este LiveData como una "señal" para avisar que se guardó con éxito
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
            // Modo Edición
            contactosArray.put(index, nuevoContacto)
        } else {
            // Modo Nuevo Contacto
            contactosArray.put(nuevoContacto)
        }

        editor.putString("contactos", contactosArray.toString())
        editor.apply()

        // Disparamos la señal de éxito para que la Actividad reaccione
        _guardadoExitoso.value = true
    }
}