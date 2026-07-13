package com.example.alertamujer.ui.contactos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.alertamujer.R
import com.example.alertamujer.ui.contactos.adapter.ChatAdapter
import com.example.alertamujer.presentation.contactos.ChatViewModel // Asegúrate de tener este ViewModel

class ChatsActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        setupRecyclerView()
        setupObservers()

        findViewById<SwipeRefreshLayout>(R.id.swipeRefreshChat).setOnRefreshListener {
            viewModel.cargarAlertas()
        }

        // Botón de atrás (asumiendo que tu layout btn_back funciona como un botón)
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(mutableListOf()) { alerta ->
            // Abrir Google Maps con las coordenadas
            val gmmIntentUri = Uri.parse("geo:${alerta.latitud},${alerta.longitud}?q=${alerta.latitud},${alerta.longitud}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<RecyclerView>(R.id.rv_chat_alertas).apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = this@ChatsActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.listaAlertas.observe(this) { alertas ->
            findViewById<SwipeRefreshLayout>(R.id.swipeRefreshChat).isRefreshing = false
            adapter.actualizarMensajes(alertas)
        }

        viewModel.mensajeError.observe(this) { error ->
            findViewById<SwipeRefreshLayout>(R.id.swipeRefreshChat).isRefreshing = false
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}