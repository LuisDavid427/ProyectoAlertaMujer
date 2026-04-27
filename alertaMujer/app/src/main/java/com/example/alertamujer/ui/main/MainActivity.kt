package com.example.alertamujer.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alertamujer.R
import com.example.alertamujer.ui.alerta.AdjuntarActivity
import com.example.alertamujer.presentation.alerta.SosViewModel
import com.example.alertamujer.ui.contactos.ContactosActivity
import com.example.alertamujer.presentation.main.MainViewModel
import com.example.alertamujer.ui.settings.SettingsActivity
import com.example.alertamujer.utils.PermissionUtils
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val sosViewModel: SosViewModel by viewModels()

    private lateinit var btnUbicacion: MaterialButton
    private lateinit var btnContactos: MaterialButton
    private lateinit var btnUserProfile: ImageButton
    private lateinit var btnSosAdjuntar: MaterialButton
    private lateinit var btnSos: MaterialButton

    private var currentUiMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        btnContactos = findViewById(R.id.btn_real_contactos)
        btnUserProfile = findViewById(R.id.btn_user_profile)
        btnUbicacion = findViewById(R.id.btn_ubicacion)
        btnSosAdjuntar = findViewById(R.id.btn_sos_adjuntar)
        btnSos = findViewById(R.id.btn_sos_circular)

        btnSosAdjuntar.visibility = View.GONE

        observarViewModel()
        observarEstadoSOS()

        btnContactos.setOnClickListener { ContactosActivity.start(this) }
        btnUserProfile.setOnClickListener { SettingsActivity.start(this) }
        btnUbicacion.setOnClickListener { verificarPermisosUbicacion() }
        btnSosAdjuntar.setOnClickListener {
            // 1. Obtenemos el ID de la alerta que guardó el SosViewModel
            val idActual = sosViewModel.idAlertaActual.value

            if (idActual != null) {
                // 2. Pasamos el contexto explícito y el ID
                AdjuntarActivity.start(this@MainActivity, idActual)
            } else {
                // Seguridad por si el botón aparece pero no hay ID (no debería pasar)
                Toast.makeText(this, "Error: No se encontró el ID de la alerta", Toast.LENGTH_SHORT).show()
            }
        }

        btnSos.setOnClickListener {
            when (sosViewModel.estadoAlerta.value) {
                is SosViewModel.EstadoAlerta.Activa -> {
                    sosViewModel.desactivarAlerta()
                }
                is SosViewModel.EstadoAlerta.Enviando -> {
                }
                else -> {
                    dispararAlerta()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarEstadoBotonUbicacion()
        viewModel.cargarDatosGenerales()
    }

    private fun observarViewModel() {
        viewModel.numeroContactos.observe(this) { cantidad ->
            btnContactos.text = "$cantidad\nContactos"
        }

        viewModel.isDarkModeOn.observe(this) { isDark ->
            val expectedUiMode = if (isDark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            if (currentUiMode != expectedUiMode) {
                recreate()
            }
        }
    }

    private fun observarEstadoSOS() {
        sosViewModel.estadoAlerta.observe(this) { estado ->
            when (estado) {
                is SosViewModel.EstadoAlerta.Inactiva -> {
                    btnSos.text = "S.O.S"
                    btnSos.isEnabled = true
                    btnSos.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                    btnSosAdjuntar.visibility = View.GONE
                }
                is SosViewModel.EstadoAlerta.Enviando -> {
                    btnSos.text = "Enviando..."
                    btnSos.isEnabled = false
                    btnSos.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    btnSosAdjuntar.visibility = View.GONE
                }
                is SosViewModel.EstadoAlerta.Activa -> {
                    btnSos.text = "Alerta Activa\n(Presiona para cancelar)"
                    btnSos.isEnabled = true
                    btnSos.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_red))
                    btnSosAdjuntar.visibility = View.VISIBLE // AHORA SÍ APARECE
                }
                is SosViewModel.EstadoAlerta.Error -> {
                    Toast.makeText(this, estado.mensaje, Toast.LENGTH_LONG).show()
                    sosViewModel.desactivarAlerta()
                }
            }
        }
    }


    private fun dispararAlerta() {
        val permisoAceptado = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoAceptado) {
            sosViewModel.obtenerUbicacionYEnviar()
        } else {
            Toast.makeText(this, "Debes conceder permisos de ubicación primero", Toast.LENGTH_LONG).show()
            verificarPermisosUbicacion()
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (fineGranted) {
            actualizarEstadoBotonUbicacion()
        } else {
            val permiso = Manifest.permission.ACCESS_FINE_LOCATION
            val deberiaExplicar = ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)

            if (!deberiaExplicar) {
                PermissionUtils.obtenerDialogoPermisoManual(this).show()
            }
            actualizarEstadoBotonUbicacion()
        }
    }

    private fun verificarPermisosUbicacion() {
        val estado = PermissionUtils.obtenerEstadoUbicacion(this)

        when (estado) {
            "CONFIGURACION" -> PermissionUtils.abrirAjustesSistema(this)
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun actualizarEstadoBotonUbicacion() {
        val config = PermissionUtils.obtenerVisualGps(this)
        btnUbicacion.text = config.texto
        btnUbicacion.setTextColor(ContextCompat.getColor(this, config.color))
    }
}