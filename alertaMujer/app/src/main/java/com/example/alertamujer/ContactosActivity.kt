package com.example.alertamujer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ContactosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        // 1. Buscamos la barra morada por su ID
        val toolbar = findViewById<Toolbar>(R.id.toolbar_contactos)

        // 2. En lugar de configurarla como la barra principal del sistema,
        // simplemente le decimos que escuche el click en la flecha.
        toolbar.setNavigationOnClickListener {
            // Esto cierra la pantalla actual y te regresa al panel
            finish()
        }
    }
}