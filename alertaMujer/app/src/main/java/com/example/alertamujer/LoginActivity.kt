package com.example.alertamujer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

// Agregamos los paréntesis () después de AppCompatActivity
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvIrARegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inicializarVistas()
        configurarListeners()
    }

    private fun inicializarVistas() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvIrARegistro = findViewById(R.id.btn_ir_a_registro)
    }

    private fun configurarListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Navega a la MainActivity (donde está tu botón SOS)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        tvIrARegistro.setOnClickListener {
            // Esto abrirá la pantalla de registro cuando la tengamos lista
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
    private fun ejecutarLogin() {
        val email = etEmail.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (email.isNotEmpty() && pass.isNotEmpty()) {

            // --- ESTRUCTURA PARA FUTURA VALIDACIÓN ---
            /* TODO: Aquí consultarás tu base de datos:
               if (usuarioExisteEnBD(email, pass)) { ... }
            */

            // Por ahora, simulamos que los datos son correctos
            Toast.makeText(this, "Bienvenida de nuevo", Toast.LENGTH_SHORT).show()

            // 1. Creamos el Intent para ir al Main
            val intent = Intent(this, MainActivity::class.java)

            // 2. Iniciamos la actividad
            startActivity(intent)

            // 3. ¡IMPORTANTE! Usamos finish()
            finish()

        } else {
            Toast.makeText(this, "Por favor, ingresa tus credenciales", Toast.LENGTH_SHORT).show()
        }
    }
}