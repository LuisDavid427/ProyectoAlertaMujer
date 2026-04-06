package com.example.alertamujer.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    // ¡Conectamos el ViewModel!
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvIrARegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inicializarVistas()
        configurarListeners()
        observarViewModel()
    }

    private fun inicializarVistas() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvIrARegistro = findViewById(R.id.btn_ir_a_registro)
    }

    private fun configurarListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            // Le pasamos el trabajo pesado al ViewModel
            viewModel.intentarLogin(email, pass)
        }

        tvIrARegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observarViewModel() {
        // 1. Escuchamos si hay errores (ej. campos vacíos o, en el futuro, contraseña incorrecta)
        viewModel.mensajeError.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        // 2. Escuchamos si nos dan luz verde para entrar a la app
        viewModel.navegarAMain.observe(this) { navegar ->
            if (navegar) {
                irAMain()
            }
        }
    }

    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Destruye el Login para que no regrese al presionar "atrás"
    }
}