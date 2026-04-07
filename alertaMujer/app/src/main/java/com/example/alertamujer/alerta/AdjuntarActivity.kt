package com.example.alertamujer.alerta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.utils.configurarBotonAtras
import com.google.android.material.button.MaterialButton

class AdjuntarActivity : AppCompatActivity() {

    private val viewModel: AdjuntarViewModel by viewModels()

    private val tomarFotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Foto capturada con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    private val grabarVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Video grabado con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    private val grabarAudioLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Audio grabado con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjuntar)

        configurarBotonAtras()

        val btnCamara = findViewById<MaterialButton>(R.id.btn_camara)
        val btnAudio = findViewById<MaterialButton>(R.id.btn_audio)

        btnCamara.setOnClickListener { viewModel.alHacerClicEnCamara() }
        btnAudio.setOnClickListener { viewModel.alHacerClicEnAudio() }

        observarViewModel()
    }

    private fun observarViewModel() {
        viewModel.accionCaptura.observe(this) { tipo ->
            when (tipo) {
                AdjuntarViewModel.TipoCaptura.MOSTRAR_OPCIONES -> mostrarDialogoOpciones()
                AdjuntarViewModel.TipoCaptura.FOTO -> lanzarCamara(MediaStore.ACTION_IMAGE_CAPTURE)
                AdjuntarViewModel.TipoCaptura.VIDEO -> lanzarCamara(MediaStore.ACTION_VIDEO_CAPTURE)
                AdjuntarViewModel.TipoCaptura.AUDIO -> lanzarAudio()
                else -> {}
            }
        }
    }

    private fun mostrarDialogoOpciones() {
        val opciones = arrayOf("Tomar Foto", "Grabar Video")
        AlertDialog.Builder(this)
            .setTitle("Seleccione el tipo de evidencia")
            .setItems(opciones) { _, which ->
                viewModel.seleccionarOpcionCamara(which)
            }
            .show()
    }

    private fun lanzarCamara(accion: String) {
        val intent = Intent(accion)
        if (intent.resolveActivity(packageManager) != null) {
            if (accion == MediaStore.ACTION_IMAGE_CAPTURE) {
                tomarFotoLauncher.launch(intent)
            } else {
                grabarVideoLauncher.launch(intent)
            }
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun lanzarAudio() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        grabarAudioLauncher.launch(intent)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AdjuntarActivity::class.java)
            context.startActivity(intent)
        }
    }
}