package com.example.alertamujer.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alertamujer.R
import com.example.alertamujer.data.manager.SosManager
import com.example.alertamujer.presentation.alerta.SosViewModel
import com.example.alertamujer.presentation.main.MainViewModel
import com.example.alertamujer.ui.alerta.AdjuntarActivity
import com.example.alertamujer.ui.contactos.ChatsActivity
import com.example.alertamujer.ui.contactos.ContactosActivity
import com.example.alertamujer.ui.settings.MensajeActivity
import com.example.alertamujer.ui.settings.SettingsActivity
import com.example.alertamujer.ui.historial.HistorialActivity
import com.example.alertamujer.util.PermissionUtils
import com.example.alertamujer.util.abrirActividad
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val sosViewModel: SosViewModel by viewModels()

    private lateinit var btnUbicacion: MaterialButton
    private lateinit var btnContactos: MaterialButton
    private lateinit var btnUserProfile: ImageButton
    private lateinit var btnSosAdjuntar: MaterialButton
    private lateinit var btnSos: MaterialButton
    private lateinit var btnMensaje: MaterialButton
    private lateinit var btnChats: ImageButton
    private lateinit var btnHistorial: MaterialButton


    private var currentUiMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // Enlaces de Vistas
        btnContactos = findViewById(R.id.btn_real_contactos)

        // Observamos el número de contactos que viene de Room
        viewModel.numeroContactos.observe(this) { count ->
            btnContactos.text = "$count\nContactos"
        }
        btnUserProfile = findViewById(R.id.btn_user_profile)
        btnUbicacion = findViewById(R.id.btn_ubicacion)
        btnSosAdjuntar = findViewById(R.id.btn_sos_adjuntar)
        btnSos = findViewById(R.id.btn_sos_circular)
        btnChats = findViewById(R.id.btn_chats)
        btnMensaje = findViewById(R.id.btn_mensaje)
        btnHistorial = findViewById(R.id.btn_historial)

        btnSosAdjuntar.visibility = View.GONE

        observarViewModel()
        observarEstadoSOS()

        // Listeners
        btnUbicacion.setOnClickListener { verificarPermisosUbicacion() }
        btnMensaje.setOnClickListener { abrirActividad<MensajeActivity>() }
        btnContactos.setOnClickListener { abrirActividad<ContactosActivity>() }
        btnUserProfile.setOnClickListener { abrirActividad<SettingsActivity>() }
        btnChats.setOnClickListener { abrirActividad<ChatsActivity>() }
        btnHistorial.setOnClickListener { abrirActividad<HistorialActivity>() }


        btnSosAdjuntar.setOnClickListener {
            // Leemos directamente del Singleton para no depender del observador de LiveData
            val idActual = SosManager.getInstance(this).idAlertaActual.value

            if (idActual != null && idActual != -1) {
                abrirActividad<AdjuntarActivity> { putExtra("EXTRA_ID_ALERTA", idActual) }
            } else {
                // Fallback: Abre AdjuntarActivity directamente, la cual volverá a consultar a SosManager
                abrirActividad<AdjuntarActivity>()
            }
        }

        btnSos.setOnClickListener {
            when (sosViewModel.estadoAlerta.value) {
                is SosManager.EstadoAlerta.Activa -> {
                    sosViewModel.desactivarAlertaEnServidor()
                }
                else -> {
                    dispararAlerta()
                }
            }
        }
    }

    private fun observarViewModel() {
        viewModel.numeroContactos.observe(this) { btnContactos.text = "$it\nContactos" }
        viewModel.isDarkModeOn.observe(this) { isDark ->
            val expectedUiMode = if (isDark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            if (currentUiMode != expectedUiMode) recreate()
        }
    }

    private fun observarEstadoSOS() {
        sosViewModel.estadoAlerta.observe(this) { estado ->
            when (estado) {
                is SosManager.EstadoAlerta.Inactiva -> {
                    btnSos.isEnabled = true
                    btnSos.text = "ENVIAR ALERTA SOS"
                    btnSosAdjuntar.visibility = View.GONE // Oculto mientras no hay alerta
                }

                is SosManager.EstadoAlerta.Procesando -> {
                    btnSos.isEnabled = false
                    btnSos.text = "Espera, enviando alerta..."
                    btnSosAdjuntar.visibility = View.GONE
                }

                is SosManager.EstadoAlerta.Activa -> {
                    btnSos.isEnabled = true
                    btnSos.text = "CANCELAR ALERTA"
                    btnSosAdjuntar.visibility = View.VISIBLE // 👈 AQUÍ SE HACE VISIBLE EN LA PANTALLA PRINCIPAL
                    Toast.makeText(this, "¡Alerta enviada con éxito!", Toast.LENGTH_SHORT).show()
                }

                is SosManager.EstadoAlerta.Error -> {
                    btnSos.isEnabled = true
                    btnSos.text = "ENVIAR ALERTA SOS"
                    btnSosAdjuntar.visibility = View.GONE
                    Toast.makeText(this, estado.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun dispararAlerta() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            sosViewModel.procesarAlertaInicial()
        } else {
            verificarPermisosUbicacion()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        actualizarEstadoBotonUbicacion()
    }

    private fun verificarPermisosUbicacion() {
        if (PermissionUtils.obtenerEstadoUbicacion(this) == "CONFIGURACION") {
            PermissionUtils.abrirAjustesSistema(this)
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun actualizarEstadoBotonUbicacion() {
        val config = PermissionUtils.obtenerVisualGps(this)
        btnUbicacion.text = config.texto
        btnUbicacion.setTextColor(ContextCompat.getColor(this, config.color))
    }

    override fun onResume() {
        super.onResume()
        actualizarEstadoBotonUbicacion()
        viewModel.cargarDatosGenerales()
    }
}