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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacto)

        val tvTitulo = findViewById<TextView>(R.id.tv_añadirContacto_title)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar_contacto)
        val etNombre = findViewById<EditText>(R.id.et_nombre_contacto)
        val etNumero = findViewById<EditText>(R.id.et_numero_contacto)
        val etEmail = findViewById<EditText>(R.id.et_email_contacto)

        configurarBotonAtras()

        // Nota: Al usar Room, la edición se simplifica. Si quieres editar,
        // pasa el ID del contacto en el intent.

        observarViewModel()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val numero = etNumero.text.toString()
            val email = etEmail.text.toString()

            if (nombre.isNotBlank() && numero.isNotBlank() && email.isNotBlank()) {
                viewModel.guardarContacto(nombre, numero, email)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observarViewModel() {
        viewModel.guardadoExitoso.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}