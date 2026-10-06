package com.example.alertamujer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.auth.RecuperarPasswordViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RecuperarPasswordActivity : AppCompatActivity() {

    private val viewModel: RecuperarPasswordViewModel by viewModels()

    private lateinit var etEmail: TextInputEditText
    private lateinit var btnEnviar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        inicializarVistas()
        configurarListeners()
        observarViewModel()
    }

    private fun inicializarVistas() {
        etEmail = findViewById(R.id.et_email_recuperacion)
        btnEnviar = findViewById(R.id.btn_enviar_recuperacion)

        // Botón de retroceso configurado con tu include
        findViewById<android.view.View>(R.id.btn_back)?.setOnClickListener { finish() }
    }

    private fun configurarListeners() {
        btnEnviar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            viewModel.solicitarCodigo(email)
        }
    }

    private fun observarViewModel() {
        viewModel.mensajeError.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        viewModel.navegarAVerificar.observe(this) { email ->
            Toast.makeText(this, "Código enviado al correo", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, VerificarCodigoActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", email)
            }
            startActivity(intent)
        }
    }
}