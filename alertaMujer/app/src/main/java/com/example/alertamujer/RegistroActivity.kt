package com.example.alertamujer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPass: TextInputEditText
    private lateinit var btnRegistrar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        inicializarVistas()

        btnRegistrar.setOnClickListener {
            ejecutarRegistro()
        }
    }

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.et_nombre)
        etEmail = findViewById(R.id.et_reg_email)
        etPass = findViewById(R.id.et_reg_password)
        btnRegistrar = findViewById(R.id.btn_registrar_usuario)
    }

    private fun ejecutarRegistro() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString().trim()

        if (validarCampos(nombre, email, pass)) {
            // AQUÍ LLAMAMOS A LA ESTRUCTURA DE LA BASE DE DATOS
            guardarUsuarioEnBaseDeDatos(nombre, email, pass)
        }
    }

    private fun validarCampos(nombre: String, email: String, pass: String): Boolean {
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El correo no es válido", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // --- ESTRUCTURA PARA FUTURA BASE DE DATOS ---
    private fun guardarUsuarioEnBaseDeDatos(nombre: String, email: String, pass: String) {
        /* TODO: Cuando estés listo, aquí conectarás con:
           - Firebase Auth (auth.createUserWithEmailAndPassword)
           - O una petición POST a tu API de MySQL/PostgreSQL
        */

        // Por ahora, simulamos éxito inmediato
        Toast.makeText(this, "Registro exitoso para $nombre", Toast.LENGTH_LONG).show()

        // Cerramos y volvemos al Login
        finish()
    }
}