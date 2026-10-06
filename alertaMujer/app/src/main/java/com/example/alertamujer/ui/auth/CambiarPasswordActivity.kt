package com.example.alertamujer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.auth.CambiarPasswordViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CambiarPasswordActivity : AppCompatActivity() {

    private val viewModel: CambiarPasswordViewModel by viewModels()

    private lateinit var etNuevaPass: TextInputEditText
    private lateinit var etConfirmarPass: TextInputEditText
    private lateinit var btnGuardar: MaterialButton

    private var email: String = ""
    private var codigo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_password)

        email = intent.getStringExtra("EXTRA_EMAIL") ?: ""
        codigo = intent.getStringExtra("EXTRA_CODIGO") ?: ""

        inicializarVistas()
        configurarListeners()
        observarViewModel()
    }

    private fun inicializarVistas() {
        etNuevaPass = findViewById(R.id.et_nueva_password)
        etConfirmarPass = findViewById(R.id.et_confirmar_password)
        btnGuardar = findViewById(R.id.btn_guardar_password)

        findViewById<android.view.View>(R.id.btn_back)?.setOnClickListener { finish() }
    }

    private fun configurarListeners() {
        btnGuardar.setOnClickListener {
            val pass = etNuevaPass.text.toString().trim()
            val confirmar = etConfirmarPass.text.toString().trim()
            viewModel.actualizarPassword(email, codigo, pass, confirmar)
        }
    }

    private fun observarViewModel() {
        viewModel.mensajeError.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        viewModel.cambioExitoso.observe(this) { exitoso ->
            if (exitoso) {
                Toast.makeText(this, "Contraseña actualizada con éxito", Toast.LENGTH_LONG).show()
                // Regresa al Login y limpia la pila para que no se pueda volver atrás
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
    }
}