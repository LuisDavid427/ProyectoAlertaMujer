package com.example.alertamujer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Activa el diseño de borde a borde
        enableEdgeToEdge()
        // 2. Conecta con tu archivo XML donde pusiste el botón SOS
        setContentView(R.layout.activity_main)
    }
}