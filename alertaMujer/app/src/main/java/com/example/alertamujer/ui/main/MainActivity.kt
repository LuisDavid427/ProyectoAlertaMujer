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
import com.example.alertamujer.presentation.alerta.SosViewModel
import com.example.alertamujer.presentation.main.MainViewModel
import com.example.alertamujer.ui.alerta.AdjuntarActivity
import com.example.alertamujer.ui.contactos.ChatsActivity
import com.example.alertamujer.ui.contactos.ContactosActivity
import com.example.alertamujer.ui.settings.MensajeActivity
import com.example.alertamujer.ui.settings.SettingsActivity
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

        btnSosAdjuntar.visibility = View.GONE

        observarViewModel()
        observarEstadoSOS()

        // Listeners
        btnUbicacion.setOnClickListener { verificarPermisosUbicacion() }
        btnMensaje.setOnClickListener { abrirActividad<MensajeActivity>() }
        btnContactos.setOnClickListener { abrirActividad<ContactosActivity>() }
        btnUserProfile.setOnClickListener { abrirActividad<SettingsActivity>() }
        btnChats.setOnClickListener { abrirActividad<ChatsActivity>() }

        btnSosAdjuntar.setOnClickListener {
            val idActual = sosViewModel.idAlertaActual.value
            if (idActual != null) {
                abrirActividad<AdjuntarActivity> { putExtra("EXTRA_ID_ALERTA", idActual) }
            } else {
                Toast.makeText(this, "Esperando ID de alerta...", Toast.LENGTH_SHORT).show()
            }
        }

        btnSos.setOnClickListener {
            when (sosViewModel.estadoAlerta.value) {
                is SosViewModel.EstadoAlerta.Activa -> {
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
                is SosViewModel.EstadoAlerta.Inactiva -> {
                    btnSos.text = "S.O.S"
                    btnSos.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                    btnSosAdjuntar.visibility = View.GONE
                }
                is SosViewModel.EstadoAlerta.Activa -> {
                    btnSos.text = "Alerta Activa\n(Presionar para cancelar)"
                    btnSos.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_red))
                    btnSosAdjuntar.visibility = View.VISIBLE
                }
                is SosViewModel.EstadoAlerta.Error -> {
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