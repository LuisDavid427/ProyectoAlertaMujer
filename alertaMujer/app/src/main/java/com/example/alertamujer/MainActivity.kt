package com.example.alertamujer

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.ImageButton
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var btnContactos: MaterialButton
    private lateinit var btnUserProfile: ImageButton

    // Guardaremos el modo de tema con el que se creó esta actividad.
    private var currentUiMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Guarda el modo de tema actual al crear la vista.
        currentUiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        btnContactos = findViewById(R.id.btn_real_contactos)
        btnUserProfile = findViewById(R.id.btn_user_profile)

        btnContactos.setOnClickListener {
            val intent = Intent(this, ContactosActivity::class.java)
            startActivity(intent)
        }

        btnUserProfile.setOnClickListener {
            // Ya no necesitamos esperar un resultado. Solo abrimos la configuración.
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarNumeroDeContactos()

        // --- LA LÓGICA CLAVE ---
        // Comprueba si el tema guardado en las preferencias es diferente al que se muestra actualmente.
        val sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPreferences.getBoolean("dark_mode", false)
        val expectedUiMode = if (isDarkModeOn) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO

        // Si el tema actual de la actividad no es el que debería ser, recréala.
        if (currentUiMode != expectedUiMode) {
            recreate()
        }
    }

    private fun actualizarNumeroDeContactos() {
        val sharedPreferences = getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)
        val numeroDeContactos = contactosArray.length()

        btnContactos.text = "$numeroDeContactos\nContactos"
    }
}