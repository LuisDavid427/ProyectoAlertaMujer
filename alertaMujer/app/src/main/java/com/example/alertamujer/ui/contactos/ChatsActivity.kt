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

        // Observamos la base de datos local de forma reactiva
        viewModel.listaAlertas.observe(this) { alertas ->
            adapter.actualizarMensajes(alertas.map { it.toMensajeAlerta() })
        }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(mutableListOf()) { alerta ->
            try {
                val lat = alerta.latitud
                val lng = alerta.longitud

                // Intent para la app de Google Maps
                val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(Alerta)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }

                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    // Si la app nativa falla, se abre vía navegador web como respaldo seguro
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                    )
                    startActivity(webIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "No se pudo abrir el mapa", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<RecyclerView>(R.id.rv_chat_alertas).apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = this@ChatsActivity.adapter
        }
    }
}