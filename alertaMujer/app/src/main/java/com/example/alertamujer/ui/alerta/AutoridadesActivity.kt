package com.example.alertamujer.ui.alerta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.alerta.AutoridadesViewModel
import com.google.android.material.textfield.TextInputEditText


class AutoridadesActivity : AppCompatActivity() {

    private val viewModel: AutoridadesViewModel by viewModels()

    private lateinit var etPolicia: TextInputEditText
    private lateinit var etBomberos: TextInputEditText
    private lateinit var etAmbulancia: TextInputEditText

    private lateinit var btnLlamarPolicia: Button
    private lateinit var btnLlamarBomberos: Button
    private lateinit var btnLlamarAmbulancia: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autoridades)

        etPolicia = findViewById(R.id.et_policia)
        etBomberos = findViewById(R.id.et_bomberos)
        etAmbulancia = findViewById(R.id.et_ambulancia)

        btnLlamarPolicia = findViewById(R.id.btn_llamar_policia)
        btnLlamarBomberos = findViewById(R.id.btn_llamar_bomberos)
        btnLlamarAmbulancia = findViewById(R.id.btn_llamar_ambulancia)

        // Botón de retroceso
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Cargar números usando el ViewModel
        etPolicia.setText(viewModel.obtenerNumeroPolicia())
        etBomberos.setText(viewModel.obtenerNumeroBomberos())
        etAmbulancia.setText(viewModel.obtenerNumeroAmbulancia())

        // Botón Llamar Policía
        btnLlamarPolicia.setOnClickListener {
            guardarYMarcar(etPolicia.text.toString())
        }

        // Botón Llamar Bomberos
        btnLlamarBomberos.setOnClickListener {
            guardarYMarcar(etBomberos.text.toString())
        }

        // Botón Llamar Ambulancia
        btnLlamarAmbulancia.setOnClickListener {
            guardarYMarcar(etAmbulancia.text.toString())
        }
    }

    private fun guardarYMarcar(numero: String) {
        // Guardar todos los campos a través del ViewModel
        viewModel.guardarNumeros(
            etPolicia.text.toString(),
            etBomberos.text.toString(),
            etAmbulancia.text.toString()
        )

        Toast.makeText(this, "¡Número guardado con éxito!", Toast.LENGTH_SHORT).show()
        realizarLlamada(numero)
    }

    private fun realizarLlamada(numero: String) {
        if (numero.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$numero")
            }
            startActivity(intent)
        }
    }
}