package com.example.alertamujer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AdjuntarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjuntar)
        // 1. Buscamos la Toolbar por su ID
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_adjuntar)

        // 2. Le asignamos la acción de "clic" a la flechita
        toolbar.setNavigationOnClickListener {
            // Esta función cierra la actividad actual y te regresa a la anterior (MainActivity)
            finish()
        }
    }
}