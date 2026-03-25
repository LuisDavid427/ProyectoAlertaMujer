package com.example.alertamujer

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.ImageButton
import org.json.JSONArray
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.alertamujer.utils.PermissionUtils


class MainActivity : AppCompatActivity() {

    private lateinit var btnUbicacion: MaterialButton
    private lateinit var btnContactos: MaterialButton
    private lateinit var btnUserProfile: ImageButton
    private lateinit var btnSosAdjuntar: MaterialButton

    private lateinit var btnSos: MaterialButton



    // Guardaremos el modo de tema con el que se creó esta actividad.
    private var currentUiMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Guarda el modo de tema actual al crear la vista.
        currentUiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // 1. Inicialización de todos los botones
        btnContactos = findViewById(R.id.btn_real_contactos)
        btnUserProfile = findViewById(R.id.btn_user_profile)
        btnUbicacion = findViewById(R.id.btn_ubicacion)
        btnSosAdjuntar = findViewById(R.id.btn_sos_adjuntar) // <-- Sin "val", igual que los otros
        btnSos = findViewById(R.id.btn_sos_circular)

        // 2. Listeners (Acciones de los botones)

        btnContactos.setOnClickListener {
            ContactosActivity.start(this)
        }

        btnUserProfile.setOnClickListener {
            SettingsActivity.start(this)
        }

        btnUbicacion.setOnClickListener {
            verificarPermisosUbicacion()
        }
        btnSos.setOnClickListener {
            SosActivity.start(this)
        }

        // El nuevo listener para la actividad de adjuntar
        btnSosAdjuntar.setOnClickListener {
            AdjuntarActivity.start(this)
        }
    }
    override fun onResume() {
        super.onResume()
        actualizarNumeroDeContactos()
        actualizarEstadoBotonUbicacion()

        // --- LA LÓGICA CLAVE ---
        // Comprueba si el tema guardado en las preferencias es diferente al que se muestra actualmente.
        val sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPreferences.getBoolean("dark_mode", false)
        val expectedUiMode = if (isDarkModeOn) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO

        // Si el tema actual de la actividad no es el que debería ser, recréala.
        if (currentUiMode != expectedUiMode) {
            recreate()
        }
    }

    private fun actualizarNumeroDeContactos() {
        val sharedPreferences = getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)
        val numeroDeContactos = contactosArray.length()

        btnContactos.text = "$numeroDeContactos\nContactos"
    }



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted) {
            // Caso exitoso: Tenemos ubicación precisa
            actualizarEstadoBotonUbicacion()
        } else {
            // El usuario no dio el permiso preciso.
            // Aquí es donde detectamos si el sistema está bloqueado (después de 2 intentos)
            val permiso = android.Manifest.permission.ACCESS_FINE_LOCATION
            val deberiaExplicar = ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)

            if (!deberiaExplicar) {
                // Si llegamos aquí sin permiso y deberiaExplicar es FALSE,
                // significa que el usuario marcó "No volver a preguntar" o ya fallaron los 2 intentos.
                PermissionUtils.obtenerDialogoPermisoManual(this).show()
            }

            actualizarEstadoBotonUbicacion()
        }
    }

    private fun verificarPermisosUbicacion() {
        val estado = PermissionUtils.obtenerEstadoUbicacion(this)

        when (estado) {
            "CONFIGURACION" -> {
                PermissionUtils.abrirAjustesSistema(this)
            }
            else -> {
                // No importa si es PEDIR_INICIAL o PEDIR_PRECISA,
                // lanzamos la petición y dejamos que el Launcher
                // arriba decida si mostrar el diálogo manual o no.
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
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