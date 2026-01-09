package com.example.alertamujer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import org.json.JSONArray
import org.json.JSONObject

class AddContactoActivity : AppCompatActivity() {

    private var contactoIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacto)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_contacto)
        val btnGuardarContacto = findViewById<Button>(R.id.btn_guardar_contacto)
        val etNombreContacto = findViewById<EditText>(R.id.et_nombre_contacto)
        val etNumeroContacto = findViewById<EditText>(R.id.et_numero_contacto)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (intent.hasExtra("CONTACTO_INDEX")) {
            contactoIndex = intent.getIntExtra("CONTACTO_INDEX", -1)
            val nombre = intent.getStringExtra("CONTACTO_NOMBRE")
            val numero = intent.getStringExtra("CONTACTO_NUMERO")

            toolbar.title = "Editar Contacto"
            etNombreContacto.setText(nombre)
            etNumeroContacto.setText(numero)
            btnGuardarContacto.text = "Guardar Cambios"
        }

        btnGuardarContacto.setOnClickListener {
            val nombre = etNombreContacto.text.toString()
            val numero = etNumeroContacto.text.toString()

            if (nombre.isNotBlank() && numero.isNotBlank()) {
                guardarContacto(nombre, numero)
                finish()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarContacto(nombre: String, numero: String) {
        val sharedPreferences = getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        val nuevoContacto = JSONObject()
        nuevoContacto.put("nombre", nombre)
        nuevoContacto.put("numero", numero)

        if (contactoIndex != -1) {
            contactosArray.put(contactoIndex, nuevoContacto)
        } else {
            contactosArray.put(nuevoContacto)
        }

        editor.putString("contactos", contactosArray.toString())
        editor.apply()
    }
}