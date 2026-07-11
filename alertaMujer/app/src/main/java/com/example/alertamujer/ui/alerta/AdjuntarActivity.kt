package com.example.alertamujer.ui.alerta

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels // Asegúrate de tener este import
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.alerta.AdjuntarViewModel
import com.example.alertamujer.util.configurarBotonAtras
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream

class AdjuntarActivity : AppCompatActivity() {

    // 1. Ahora que es un ViewModel estándar, esta línea es suficiente
    private val viewModel: AdjuntarViewModel by viewModels()
    private var idAlerta: Int = -1

    private val tomarFotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val file = guardarBitmapEnArchivo(it)
                // 2. Aquí pasamos 'this' como el Context que pide tu función
                viewModel.enviarArchivoAlServidor(this, idAlerta, file, false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjuntar)

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
                AdjuntarViewModel.TipoCaptura.FOTO -> lanzarCamara(MediaStore.ACTION_IMAGE_CAPTURE)
                AdjuntarViewModel.TipoCaptura.VIDEO -> lanzarCamara(MediaStore.ACTION_VIDEO_CAPTURE)
                AdjuntarViewModel.TipoCaptura.AUDIO -> lanzarAudio()
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

    private fun mostrarDialogoOpciones() {
        val opciones = arrayOf("Tomar Foto", "Grabar Video")
        AlertDialog.Builder(this)
            .setTitle("Seleccione evidencia")
            .setItems(opciones) { _, which -> viewModel.seleccionarOpcionCamara(which) }
            .show()
    }

    private fun lanzarCamara(accion: String) {
        val intent = Intent(accion)
        // Nota: Si intentas lanzar video, asegúrate de tener los permisos de cámara y almacenamiento
        if (intent.resolveActivity(packageManager) != null) {
            tomarFotoLauncher.launch(intent)
        }
    }

    private fun lanzarAudio() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        startActivity(intent)
    }
}