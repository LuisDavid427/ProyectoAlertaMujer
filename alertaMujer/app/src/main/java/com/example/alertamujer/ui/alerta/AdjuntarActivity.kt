package com.example.alertamujer.ui.alerta

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
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

    private val mediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data

            // LECTURA SEGURA: Se obtiene el token desde la bóveda encriptada
            val token = sessionManager.obtenerToken() ?: ""
            val tokenFormateado = "Bearer $token"

            if (tipoMediaActual == "FOTO") {
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    val file = guardarBitmapEnArchivo(it)
                    viewModel.enviarArchivoAlServidor(tokenFormateado, idAlerta, file, "FOTO")
                }
            } else {
                val uri = data?.data
                uri?.let {
                    val file = uriToFile(it, tipoMediaActual)
                    viewModel.enviarArchivoAlServidor(tokenFormateado, idAlerta, file, tipoMediaActual)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjuntar)

        sessionManager = SessionManager(this)

        idAlerta = intent.getIntExtra("EXTRA_ID_ALERTA", -1)

        configurarBotonAtras()
        setupUI()
        observarViewModel()
    }

    private fun setupUI() {
        findViewById<MaterialButton>(R.id.btn_camara).setOnClickListener { viewModel.alHacerClicEnCamara() }
        findViewById<MaterialButton>(R.id.btn_audio).setOnClickListener { viewModel.alHacerClicEnAudio() }
    }

    private fun observarViewModel() {
        viewModel.accionCaptura.observe(this) { tipo ->
            when (tipo) {
                AdjuntarViewModel.TipoCaptura.MOSTRAR_OPCIONES -> mostrarDialogoOpciones()
                AdjuntarViewModel.TipoCaptura.FOTO -> {
                    tipoMediaActual = "FOTO"
                    lanzarIntent(MediaStore.ACTION_IMAGE_CAPTURE)
                }
                AdjuntarViewModel.TipoCaptura.VIDEO -> {
                    tipoMediaActual = "VIDEO"
                    lanzarIntent(MediaStore.ACTION_VIDEO_CAPTURE)
                }
                AdjuntarViewModel.TipoCaptura.AUDIO -> {
                    tipoMediaActual = "AUDIO"
                    lanzarIntent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
                }
            }
        }

        viewModel.estadoSubida.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarBitmapEnArchivo(bitmap: Bitmap): File {
        val file = File(cacheDir, "evidencia_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
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

    private fun lanzarIntent(accion: String) {
        try {
            val intent = Intent(accion)
            mediaLauncher.launch(intent)
        } catch (e: Exception) {
            // Manejo de la restricción de visibilidad de paquetes en Android 11+
            Toast.makeText(this, "Tu dispositivo no tiene una app para esta acción", Toast.LENGTH_SHORT).show()
        }
    }
}