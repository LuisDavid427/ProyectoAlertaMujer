package com.example.alertamujer

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdjuntarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjuntar)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_adjuntar)
        val btnCamara = findViewById<MaterialButton>(R.id.btn_camara)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Configuración del evento de clic para el botón de evidencia
        btnCamara.setOnClickListener {
            mostrarOpcionesCaptura()
        }
    }

    private fun mostrarOpcionesCaptura() {
        val opciones = arrayOf("Tomar Foto", "Grabar Video")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione el tipo de evidencia")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> abrirCamara(MediaStore.ACTION_IMAGE_CAPTURE, 101)
                1 -> abrirCamara(MediaStore.ACTION_VIDEO_CAPTURE, 102)
            }
        }
        builder.show()
    }

    private fun abrirCamara(accion: String, requestCode: Int) {
        val intent = Intent(accion)
        // Validación de disponibilidad de la aplicación de cámara en el dispositivo
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestCode)
        }
    }
}