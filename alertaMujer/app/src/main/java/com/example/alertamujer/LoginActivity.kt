package com.example.alertamujer

import android.content.Context // Importante para SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvIrARegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 1. EL GUARDIÁN DE SESIÓN ---
        val prefs = getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
        val userIsLogged = prefs.getBoolean("isLoggedIn", false)

        if (userIsLogged) {
            irAMain()
            return // Detiene el resto del código para no inflar el diseño innecesariamente
        }

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
            ejecutarLogin()
        }

        tvIrARegistro.setOnClickListener {
            // Asegúrate que el nombre de la clase sea exacto al que creaste (ActivityRegistro)
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun ejecutarLogin() {
        val email = etEmail.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (email.isNotEmpty() && pass.isNotEmpty()) {

            // --- ESTRUCTURA PARA FUTURA VALIDACIÓN CON BD ---
            /* TODO: Aquí validarás contra MySQL o Firebase
               if (validarConBD(email, pass)) { ... }
            */

            // --- 2. GUARDAR LA SESIÓN ---
            val prefs = getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean("isLoggedIn", true)
            // editor.putString("userEmail", email) // Puedes guardar el correo si quieres
            editor.apply()

            Toast.makeText(this, "Bienvenida de nuevo", Toast.LENGTH_SHORT).show()
            irAMain()

        } else {
            Toast.makeText(this, "Por favor, ingresa tus credenciales", Toast.LENGTH_SHORT).show()
        }
    }

    // Función auxiliar para no repetir código de navegación
    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Destruye el Login para que no regrese al presionar "atrás"
    }
}