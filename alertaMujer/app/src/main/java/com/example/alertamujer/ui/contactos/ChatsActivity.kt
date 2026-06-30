package com.example.alertamujer.ui.contactos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.presentation.contactos.ChatsViewModel
import com.example.alertamujer.ui.contactos.adapter.ChatAdapter

/**
 * Pantalla de visualización de alertas en tiempo real.
 * Sigue el patrón Observer para mantener la sincronización con el ViewModel.
 */
class ChatsActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatsViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var rvChatAlertas: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        rvChatAlertas = findViewById(R.id.rv_chat_alertas)
        // stackFromEnd permite que los mensajes se apilen hacia arriba (estilo WhatsApp)
        rvChatAlertas.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        // Inicializamos con lista vacía
        chatAdapter = ChatAdapter(mutableListOf()) { alerta ->
            // Aquí dispararemos la lógica de Google Maps en la siguiente fase
            Toast.makeText(this, "Redirigiendo a GPS...", Toast.LENGTH_SHORT).show()
        }
        rvChatAlertas.adapter = chatAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ChatsViewModel::class.java]

        // Observar cambios en el ViewModel
        viewModel.mensajes.observe(this) { listaActualizada ->
            chatAdapter.actualizarMensajes(listaActualizada)
            // Auto-scroll al último mensaje al recibir uno nuevo
            rvChatAlertas.scrollToPosition(listaActualizada.size - 1)
        }
    }
}