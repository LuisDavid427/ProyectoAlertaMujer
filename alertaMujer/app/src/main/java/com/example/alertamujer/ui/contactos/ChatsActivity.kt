package com.example.alertamujer.ui.contactos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.ui.contactos.adapter.ChatAdapter
import com.example.alertamujer.presentation.contactos.ChatViewModel
import com.example.alertamujer.data.local.entity.toMensajeAlerta

class ChatsActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_alertas)

        setupRecyclerView()

        // Observamos la base de datos local
        viewModel.listaAlertas.observe(this) { alertas ->
            // Mapeamos de Entity a lo que espera tu adaptador
            adapter.actualizarMensajes(alertas.map { it.toMensajeAlerta() })
        }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(mutableListOf()) { alerta ->
            val gmmIntentUri = Uri.parse("geo:${alerta.latitud},${alerta.longitud}?q=${alerta.latitud},${alerta.longitud}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "Google Maps no instalado", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<RecyclerView>(R.id.rv_chat_alertas).apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity).apply {
                stackFromEnd = true // Esto hace que los mensajes nuevos aparezcan abajo como en WhatsApp
            }
            adapter = this@ChatsActivity.adapter
        }
    }
}