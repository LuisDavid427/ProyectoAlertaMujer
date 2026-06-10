package com.example.alertamujer.ui.contactos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.alertamujer.R
import com.example.alertamujer.presentation.chat.ChatViewModel
import com.example.alertamujer.util.configurarBotonAtras

class ChatsActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()

    // private lateinit var adapter: ChatAdapter // ¡Lo armaremos en el siguiente paso!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        configurarBotonAtras()
        setupRecyclerView()
        observarViewModel()

        viewModel.cargarChat()

        // Recargar el chat al deslizar hacia abajo
        findViewById<SwipeRefreshLayout>(R.id.swipeRefreshChat).setOnRefreshListener {
            viewModel.cargarChat()
        }
    }

    private fun setupRecyclerView() {
        val rvChat = findViewById<RecyclerView>(R.id.rv_chat_alertas)
        rvChat.layoutManager = LinearLayoutManager(this)

        // Aquí conectaremos el adaptador más adelante
        // adapter = ChatAdapter(emptyList())
        // rvChat.adapter = adapter
    }

    private fun observarViewModel() {
        viewModel.alertas.observe(this) { listaMensajes ->
            // adapter.actualizarLista(listaMensajes)
            findViewById<SwipeRefreshLayout>(R.id.swipeRefreshChat).isRefreshing = false
        }

        viewModel.error.observe(this) { msj ->
            Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
            findViewById<SwipeRefreshLayout>(R.id.swipeRefreshChat).isRefreshing = false
        }
    }
}