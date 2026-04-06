package com.example.alertamujer.contactos

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray

// 1. EL MODELO: Una forma limpia de representar un contacto en Kotlin
data class Contacto(val nombre: String, val numero: String, val index: Int)

class ContactosViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)

    // 2. EL LIVEDATA: La lista lista para ser consumida por la Vista
    private val _contactos = MutableLiveData<List<Contacto>>()
    val contactos: LiveData<List<Contacto>> get() = _contactos

    // El cerebro se encarga de traducir el JSON a una lista limpia de Kotlin
    fun cargarContactos() {
        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        val listaTemporal = mutableListOf<Contacto>()
        for (i in 0 until contactosArray.length()) {
            val obj = contactosArray.getJSONObject(i)
            listaTemporal.add(Contacto(obj.getString("nombre"), obj.getString("numero"), i))
        }

        // Avisamos a la Vista que ya tenemos los datos procesados
        _contactos.value = listaTemporal
    }

    fun eliminarContacto(index: Int) {
        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        // Eliminamos del JSON
        contactosArray.remove(index)

        // Guardamos los cambios en la memoria
        sharedPreferences.edit().putString("contactos", contactosArray.toString()).apply()

        // Recargamos la lista para que la Actividad se actualice sola
        cargarContactos()
    }
}