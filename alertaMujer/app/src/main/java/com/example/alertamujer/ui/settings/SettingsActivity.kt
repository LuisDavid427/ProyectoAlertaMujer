package com.example.alertamujer.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.alertamujer.R
import com.example.alertamujer.presentation.settings.SettingsViewModel
import com.example.alertamujer.service.BubbleService
import com.example.alertamujer.ui.auth.LoginActivity
import com.example.alertamujer.util.configurarBotonAtras
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var switchBubble: SwitchMaterial // 🟢 Declaramos el switch de la burbuja

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val tvUserEmail = findViewById<TextView>(R.id.tv_user_email)
        val btnLogout = findViewById<TextView>(R.id.btn_logout)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)

        // 🛑 ASEGÚRATE de agregar este ID (switch_bubble) en tu archivo activity_settings.xml
        switchBubble = findViewById(R.id.switch_bubble)

        configurarBotonAtras()

        // Observar datos de la sesión
        viewModel.nombreUsuario.observe(this) { tvUserName.text = it }
        viewModel.emailUsuario.observe(this) { tvUserEmail.text = it }
        viewModel.isDarkMode.observe(this) { switchTheme.isChecked = it }

        // 🟢 Observar estado guardado de la burbuja
        viewModel.isBubbleActive.observe(this) { isActive: Boolean ->
            switchBubble.isChecked = isActive
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

        btnLogout.setOnClickListener { viewModel.cerrarSesion() }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            viewModel.updateTheme(isChecked)
        }

        // 🟢 Lógica del switch de la burbuja flotante
        switchBubble.setOnCheckedChangeListener { _, isChecked ->
            manejarCambioBurbuja(isChecked)
        }
    }

    private fun manejarCambioBurbuja(activar: Boolean) {
        if (activar) {
            // Android exige verificar el permiso SYSTEM_ALERT_WINDOW (Mostrar sobre otras apps)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(
                    this,
                    "Concede el permiso para mostrar la burbuja sobre otras apps",
                    Toast.LENGTH_LONG // 🟢 Corrección aquí
                ).show()

                // Desmarcamos el switch momentáneamente hasta que otorgue el permiso
                switchBubble.isChecked = false
                viewModel.updateBubbleState(false)

                // Redirigir a los ajustes del sistema para el permiso especial
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                // Iniciar servicio de la burbuja
                val serviceIntent = Intent(this, BubbleService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                viewModel.updateBubbleState(true)
                Toast.makeText(this, "🟢 Burbuja flotante activada", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Detener servicio de la burbuja
            val serviceIntent = Intent(this, BubbleService::class.java)
            stopService(serviceIntent)
            viewModel.updateBubbleState(false)
            Toast.makeText(this, "🔴 Burbuja flotante desactivada", Toast.LENGTH_SHORT).show()
        }
    }

    // Opcional: Si regresa de los ajustes del sistema tras otorgar el permiso, puedes reevaluar aquí si deseas.
}