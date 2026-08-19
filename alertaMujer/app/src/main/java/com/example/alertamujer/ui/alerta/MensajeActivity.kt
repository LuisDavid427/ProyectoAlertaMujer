package com.example.alertamujer.ui.settings

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

        // ✅ 1. Cargar el mensaje actual usando el ViewModel (almacenamiento cifrado)
        val mensajeGuardado = viewModel.obtenerMensajeActual()
        etMensaje.setText(mensajeGuardado)

        // ✅ 2. Guardar el nuevo mensaje escrito a través del ViewModel
        btnGuardar.setOnClickListener {
            val nuevoMensaje = etMensaje.text.toString().trim()
            viewModel.guardarMensajeLocal(nuevoMensaje)
        }
    }

    private fun observarViewModel() {
        viewModel.estadoMensaje.observe(this) { mensajeEstado ->
            Toast.makeText(this, mensajeEstado, Toast.LENGTH_SHORT).show()
            // Si se guardó con éxito, cerramos la pantalla
            if (mensajeEstado == "Mensaje guardado en tu celular") {
                finish()
            }
        }
    }
}