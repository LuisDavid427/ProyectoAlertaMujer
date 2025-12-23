package com.example.alertamujer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() { // Cambiado a AppCompatActivity para ser iguales
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // QUITAMOS enableEdgeToEdge() por ahora
        setContentView(R.layout.activity_main)

        val btnContactos = findViewById<MaterialButton>(R.id.btn_real_contactos)

        btnContactos.setOnClickListener {
            val intent = Intent(this, ContactosActivity::class.java)
            startActivity(intent)
        }
    }
}