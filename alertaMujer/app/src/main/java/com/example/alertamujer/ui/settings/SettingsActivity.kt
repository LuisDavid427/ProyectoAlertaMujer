package com.example.alertamujer.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.alertamujer.R
import com.example.alertamujer.presentation.settings.SettingsViewModel
import com.example.alertamujer.ui.auth.LoginActivity // 🟢 Paquete correcto
import com.example.alertamujer.util.configurarBotonAtras
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val tvUserEmail = findViewById<TextView>(R.id.tv_user_email)
        val btnLogout = findViewById<TextView>(R.id.btn_logout)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)

        configurarBotonAtras()

        // Observar datos de la sesión
        viewModel.nombreUsuario.observe(this) { nombre ->
            tvUserName.text = nombre
        }

        viewModel.emailUsuario.observe(this) { email ->
            tvUserEmail.text = email
        }

        viewModel.isDarkMode.observe(this) { isDark ->
            switchTheme.isChecked = isDark
        }

        // Evento de cierre de sesión
        viewModel.cerrarSesionEvento.observe(this) { cerrado ->
            if (cerrado) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }

        btnLogout.setOnClickListener {
            viewModel.cerrarSesion()
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            viewModel.updateTheme(isChecked)
        }
    }
}