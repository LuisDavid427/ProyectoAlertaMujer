package com.example.alertamujer.contactos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.utils.configurarBotonAtras

class AddContactoActivity : AppCompatActivity() {

    private val viewModel: AddContactoViewModel by viewModels()

    private var contactoIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacto)

        val tvTitulo = findViewById<TextView>(R.id.tv_añadirContacto_title)
        val btnGuardarContacto = findViewById<Button>(R.id.btn_guardar_contacto)
        val etNombreContacto = findViewById<EditText>(R.id.et_nombre_contacto)
        val etNumeroContacto = findViewById<EditText>(R.id.et_numero_contacto)

        configurarBotonAtras()

        if (intent.hasExtra("CONTACTO_INDEX")) {
            contactoIndex = intent.getIntExtra("CONTACTO_INDEX", -1)
            val nombre = intent.getStringExtra("CONTACTO_NOMBRE")
            val numero = intent.getStringExtra("CONTACTO_NUMERO")

            tvTitulo.text = "Editar Contacto"
            etNombreContacto.setText(nombre)
            etNumeroContacto.setText(numero)
            btnGuardarContacto.text = "Guardar Cambios"
        }

        observarViewModel()

        btnGuardarContacto.setOnClickListener {
            val nombre = etNombreContacto.text.toString()
            val numero = etNumeroContacto.text.toString()

            if (nombre.isNotBlank() && numero.isNotBlank()) {
                viewModel.guardarContacto(nombre, numero, contactoIndex)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
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

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AddContactoActivity::class.java)
        }

        fun start(context: Context) {
            val intent = newIntent(context)
            context.startActivity(intent)
        }
    }
}