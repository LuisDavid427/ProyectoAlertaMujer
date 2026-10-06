package com.example.alertamujer.ui.contactos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.contactos.AddContactoViewModel
import com.example.alertamujer.util.configurarBotonAtras

class AddContactoActivity : AppCompatActivity() {

    private val viewModel: AddContactoViewModel by viewModels()
    private var idContactoActual: Int = 0 // 0 por defecto si es nuevo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacto)

        val tvTitulo = findViewById<TextView>(R.id.tv_añadirContacto_title)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar_contacto)
        val etNombre = findViewById<EditText>(R.id.et_nombre_contacto)
        val etNumero = findViewById<EditText>(R.id.et_numero_contacto)
        val etEmail = findViewById<EditText>(R.id.et_email_contacto)

        configurarBotonAtras()

        // 🔍 RECUPERAR DATOS SI VIENE DE LA EDICIÓN (LÁPIZ)
        if (intent.hasExtra("EXTRA_ID_CONTACTO")) {
            idContactoActual = intent.getIntExtra("EXTRA_ID_CONTACTO", 0)
            val nombreAnterior = intent.getStringExtra("EXTRA_NOMBRE_CONTACTO") ?: ""
            val numeroAnterior = intent.getStringExtra("EXTRA_NUMERO_CONTACTO") ?: ""
            val emailAnterior = intent.getStringExtra("EXTRA_EMAIL_CONTACTO") ?: "" // 👈 Recibimos el correo

            // Precargamos los datos en las casillas
            etNombre.setText(nombreAnterior)
            etNumero.setText(numeroAnterior)
            etEmail.setText(emailAnterior) // 👈 Rellenamos el correo electrónico

            tvTitulo.text = "Editar Contacto"
            btnGuardar.text = "Actualizar Contacto"
        }

        observarViewModel()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val numero = etNumero.text.toString()
            val email = etEmail.text.toString()

            if (nombre.isNotBlank() && numero.isNotBlank() && email.isNotBlank()) {
                // Enviamos los datos junto con el id para que Room se encargue de actualizar o crear
                viewModel.guardarContacto(nombre, numero, email, idContactoActual)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observarViewModel() {
        viewModel.guardadoExitoso.observe(this) { exito ->
            if (exito) {
                val mensaje = if (idContactoActual != 0) "Contacto actualizado" else "Contacto guardado"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}