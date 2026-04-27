package com.example.alertamujer.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.alertamujer.R
import com.example.alertamujer.presentation.settings.SettingsViewModel
import com.example.alertamujer.utils.configurarBotonAtras
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSave = findViewById<Button>(R.id.btn_save_account)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)

        viewModel.isDarkMode.observe(this) { isDark ->
            switchTheme.isChecked = isDark
        }

        configurarBotonAtras()

        btnSave.setOnClickListener {
            val user = etUsername.text.toString()
            val pass = etPassword.text.toString()

            // Le pasamos la tarea sucia al ViewModel
            viewModel.saveCredentials(user, pass)
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            // La Actividad SOLO hace cosas visuales (cambiar los colores de Android)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Le avisa al ViewModel para que él guarde el dato en memoria
            viewModel.updateTheme(isChecked)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }

        fun start(context: Context) {
            val intent = newIntent(context)
            context.startActivity(intent)
        }
    }
}