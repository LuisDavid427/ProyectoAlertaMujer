package com.example.alertamujer.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.auth.RegistroViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class RegistroActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModels()

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPass: TextInputEditText
    private lateinit var etFechaNacimiento: TextInputEditText
    private lateinit var btnRegistrar: MaterialButton

    // Variable interna para guardar la fecha en el formato exacto de Spring Boot
    private var fechaFormatoSpring: String = ""

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
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento)
        btnRegistrar = findViewById(R.id.btn_registrar_usuario)

        // Bloqueamos el teclado para obligar al usuario a usar el calendario
        etFechaNacimiento.isFocusable = false
        etFechaNacimiento.isClickable = true
    }

    private fun configurarListeners() {
        // Mostrar calendario al tocar el campo de fecha
        etFechaNacimiento.setOnClickListener {
            mostrarCalendario()
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()

            // Validamos que el usuario haya seleccionado una fecha en el calendario
            if (fechaFormatoSpring.isEmpty()) {
                Toast.makeText(this, "Por favor selecciona tu fecha de nacimiento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pasamos la variable formateada para Spring Boot, NO el texto visual
            viewModel.intentarRegistro(nombre, email, pass, fechaFormatoSpring)
        }
    }

    private fun mostrarCalendario() {
        val calendario = Calendar.getInstance()
        val anioActual = calendario.get(Calendar.YEAR)
        val mesActual = calendario.get(Calendar.MONTH)
        val diaActual = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, anio, mes, dia ->
            // 1. Formato visual amigable para el usuario en la app (Ej: 24/05/2000)
            val fechaVisual = String.format("%02d/%02d/%04d", dia, mes + 1, anio)
            etFechaNacimiento.setText(fechaVisual)

            // 2. Formato ISO 8601 exigido por el LocalDateTime de Spring Boot (Ej: 2000-05-24T00:00:00)
            fechaFormatoSpring = String.format("%04d-%02d-%02dT00:00:00", anio, mes + 1, dia)

        }, anioActual, mesActual, diaActual)

        // Opcional: Limitar para que no puedan seleccionar fechas del futuro
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun observarViewModel() {
        viewModel.mensajeError.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        viewModel.registroExitoso.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}