package com.example.alertamujer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSave = findViewById<Button>(R.id.btn_save_account)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)

        val sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        switchTheme.isChecked = isDarkMode

        // El botón de regreso simplemente cierra la actividad.
        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            // Lógica para guardar el nombre de usuario y la contraseña
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            // Simplemente aplicamos el tema y guardamos la preferencia.
            // El sistema y nuestro código en onResume se encargarán del resto.
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            with(sharedPreferences.edit()) {
                putBoolean("dark_mode", isChecked)
                apply()
            }
        }
    }
}