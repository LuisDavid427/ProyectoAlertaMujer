package com.example.alertamujer.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegistroActivity : AppCompatActivity() {

    // Conectamos el ViewModel
    private val viewModel: RegistroViewModel by viewModels()

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPass: TextInputEditText
    private lateinit var btnRegistrar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        inicializarVistas()
        configurarListeners()
        observarViewModel()
    }

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.et_nombre)
        etEmail = findViewById(R.id.et_reg_email)
        etPass = findViewById(R.id.et_reg_password)
        btnRegistrar = findViewById(R.id.btn_registrar_usuario)
    }

    private fun configurarListeners() {
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()

            // Delegamos la lógica al ViewModel
            viewModel.intentarRegistro(nombre, email, pass)
        }
    }

    private fun observarViewModel() {
        // Escuchamos si hay errores de validación
        viewModel.mensajeError.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        // Escuchamos si el registro fue exitoso para cerrar la pantalla
        viewModel.registroExitoso.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}