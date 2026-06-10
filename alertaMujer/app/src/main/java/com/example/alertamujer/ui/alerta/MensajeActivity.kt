package com.example.alertamujer.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.settings.MensajeViewModel
import com.example.alertamujer.util.configurarBotonAtras
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MensajeActivity : AppCompatActivity() {

    private val viewModel: MensajeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje)

        setupUI()
        observarViewModel()
        configurarBotonAtras()
    }

    private fun setupUI() {
        val etMensaje = findViewById<TextInputEditText>(R.id.et_mensaje_personalizado)
        val btnGuardar = findViewById<MaterialButton>(R.id.btn_guardar_mensaje)

        // 1. Cargamos el mensaje que ya estaba guardado para mostrarlo en pantalla
        val prefs = getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
        val mensajeGuardado = prefs.getString("mensaje_sos", "¡Auxilio! Alerta SOS iniciada")
        etMensaje.setText(mensajeGuardado)

        // 2. Evento para guardar el nuevo mensaje escrito
        btnGuardar.setOnClickListener {
            val nuevoMensaje = etMensaje.text.toString()
            viewModel.guardarMensajeLocal(nuevoMensaje)
        }
    }

    private fun observarViewModel() {
        viewModel.estadoMensaje.observe(this) { mensajeEstado ->
            Toast.makeText(this, mensajeEstado, Toast.LENGTH_SHORT).show()
            // Si se guardó con éxito, podemos cerrar la pantalla
            if (mensajeEstado == "Mensaje guardado en tu celular") {
                finish()
            }
        }
    }
}