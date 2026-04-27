package com.example.alertamujer.presentation.contactos

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray

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

        _contactos.value = listaTemporal
    }

    fun eliminarContacto(index: Int) {
        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        contactosArray.remove(index)

        sharedPreferences.edit().putString("contactos", contactosArray.toString()).apply()

        cargarContactos()
    }
}