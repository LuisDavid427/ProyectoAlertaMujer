package com.example.alertamujer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.auth.VerificarCodigoViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class VerificarCodigoActivity : AppCompatActivity() {

    private val viewModel: VerificarCodigoViewModel by viewModels()

    private lateinit var etCodigo: TextInputEditText
    private lateinit var btnVerificar: MaterialButton
    private var emailUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificar_codigo)

        emailUsuario = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        inicializarVistas()
        configurarListeners()
        observarViewModel()
    }

    private fun inicializarVistas() {
        etCodigo = findViewById(R.id.et_codigo)
        btnVerificar = findViewById(R.id.btn_verificar_codigo)

        findViewById<android.view.View>(R.id.btn_back)?.setOnClickListener { finish() }
    }

    private fun configurarListeners() {
        btnVerificar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()
            viewModel.verificarCodigo(emailUsuario, codigo)
        }
    }

    private fun observarViewModel() {
        viewModel.mensajeError.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        viewModel.navegarACambiarPass.observe(this) { datos ->
            val intent = Intent(this, CambiarPasswordActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", datos.first)
                putExtra("EXTRA_CODIGO", datos.second)
            }
            startActivity(intent)
        }
    }
}