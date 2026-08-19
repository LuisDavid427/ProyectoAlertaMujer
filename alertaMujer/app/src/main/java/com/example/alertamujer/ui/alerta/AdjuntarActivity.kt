package com.example.alertamujer.ui.alerta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.alertamujer.R
import com.example.alertamujer.data.manager.SosManager
import com.example.alertamujer.presentation.alerta.AdjuntarViewModel
import com.example.alertamujer.util.SessionManager
import com.example.alertamujer.util.configurarBotonAtras
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream

class AdjuntarActivity : AppCompatActivity() {

    private val viewModel: AdjuntarViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    private var idAlerta: Int = -1
    private var tipoMediaActual: String = "FOTO"

    // Launcher para pedir permisos en tiempo de ejecución
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            lanzarCapturaDirecta()
        } else {
            Toast.makeText(this, "Se requiere el permiso para capturar la evidencia.", Toast.LENGTH_LONG).show()
        }
    }

    // Launcher exclusivo de Cámara para Foto (Abre la cámara y retorna el Bitmap)
    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            procesarYEnviarFoto(bitmap)
        }
    }

    // Launcher para Video y Audio
    private val mediaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                procesarYEnviarMedia(uri, tipoMediaActual)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjuntar)

        sessionManager = SessionManager(this)

        idAlerta = intent.getIntExtra("EXTRA_ID_ALERTA", -1)
        if (idAlerta == -1) {
            idAlerta = SosManager.getInstance(this).idAlertaActual.value ?: -1
        }

        configurarBotonAtras()
        setupUI()
        observarViewModel()
        procesarAutoLanzamiento(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarAutoLanzamiento(intent)
    }

    private fun procesarAutoLanzamiento(intent: Intent?) {
        val autoLaunch = intent?.getStringExtra("EXTRA_TIPO_AUTO_LAUNCH")
        if (!autoLaunch.isNullOrEmpty()) {
            when (autoLaunch) {
                "FOTO" -> verificarPermisoYCapturar("FOTO")
                "VIDEO" -> verificarPermisoYCapturar("VIDEO")
                "AUDIO" -> verificarPermisoYCapturar("AUDIO")
            }
        }
    }

    private fun setupUI() {
        findViewById<MaterialButton>(R.id.btn_camara).setOnClickListener { viewModel.alHacerClicEnCamara() }
        findViewById<MaterialButton>(R.id.btn_audio).setOnClickListener { viewModel.alHacerClicEnAudio() }
    }

    private fun observarViewModel() {
        viewModel.accionCaptura.observe(this) { tipo ->
            when (tipo) {
                AdjuntarViewModel.TipoCaptura.MOSTRAR_OPCIONES -> mostrarDialogoOpciones()
                AdjuntarViewModel.TipoCaptura.FOTO -> verificarPermisoYCapturar("FOTO")
                AdjuntarViewModel.TipoCaptura.VIDEO -> verificarPermisoYCapturar("VIDEO")
                AdjuntarViewModel.TipoCaptura.AUDIO -> verificarPermisoYCapturar("AUDIO")
            }
        }

        viewModel.estadoSubida.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            if (mensaje.contains("éxito", ignoreCase = true)) {
                finish()
            }
        }
    }

    private fun verificarPermisoYCapturar(tipo: String) {
        tipoMediaActual = tipo
        val permisoRequerido = if (tipo == "AUDIO") {
            Manifest.permission.RECORD_AUDIO
        } else {
            Manifest.permission.CAMERA
        }

        if (ContextCompat.checkSelfPermission(this, permisoRequerido) == PackageManager.PERMISSION_GRANTED) {
            lanzarCapturaDirecta()
        } else {
            requestPermissionLauncher.launch(permisoRequerido)
        }
    }

    private fun lanzarCapturaDirecta() {
        try {
            when (tipoMediaActual) {
                "FOTO" -> {
                    // Fuerza la apertura de la cámara nativa
                    tomarFotoLauncher.launch(null)
                }
                "VIDEO" -> {
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    mediaLauncher.launch(intent)
                }
                "AUDIO" -> {
                    val intent = Intent("android.provider.MediaStore.RECORD_SOUND")
                    mediaLauncher.launch(intent)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No se pudo abrir la cámara o grabadora. Verifica que tu emulador/celular tenga la cámara activada.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun procesarYEnviarFoto(bitmap: Bitmap) {
        val token = sessionManager.obtenerToken() ?: ""
        val tokenFormateado = "Bearer $token"

        if (validarIdAlerta()) {
            val file = guardarBitmapEnArchivo(bitmap)
            viewModel.enviarArchivoAlServidor(tokenFormateado, idAlerta, file, "FOTO")
        }
    }

    private fun procesarYEnviarMedia(uri: Uri, tipo: String) {
        val token = sessionManager.obtenerToken() ?: ""
        val tokenFormateado = "Bearer $token"

        if (validarIdAlerta()) {
            val file = uriToFile(uri, tipo)
            viewModel.enviarArchivoAlServidor(tokenFormateado, idAlerta, file, tipo)
        }
    }

    private fun validarIdAlerta(): Boolean {
        if (idAlerta == -1) {
            idAlerta = SosManager.getInstance(this).idAlertaActual.value ?: -1
        }
        if (idAlerta == -1) {
            Toast.makeText(this, "No hay una alerta activa para adjuntar evidencia.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun guardarBitmapEnArchivo(bitmap: Bitmap): File {
        val file = File(cacheDir, "evidencia_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
        }
        return file
    }

    private fun uriToFile(uri: Uri, tipo: String): File {
        val extension = if (tipo == "VIDEO") ".mp4" else ".m4a"
        val file = File(cacheDir, "evidencia_${System.currentTimeMillis()}$extension")

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun mostrarDialogoOpciones() {
        val opciones = arrayOf("Tomar Foto", "Grabar Video")
        AlertDialog.Builder(this)
            .setTitle("Seleccione evidencia")
            .setItems(opciones) { _, which -> viewModel.seleccionarOpcionCamara(which) }
            .show()
    }
}