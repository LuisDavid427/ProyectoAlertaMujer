package com.example.alertamujer.ui.contactos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.data.model.MensajeAlerta

class ChatAdapter(
    private var listaMensajes: MutableList<MensajeAlerta>,
    private val onMapaClick: (MensajeAlerta) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_alerta, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(listaMensajes[position])
    }

    override fun getItemCount(): Int = listaMensajes.size



    /**
     * Esta es la función que te faltaba.
     * Permite que el Activity reemplace la lista vieja por la nueva
     * y notifique al RecyclerView que debe redibujarse.
     */
    fun actualizarMensajes(nuevaLista: List<MensajeAlerta>) {
        this.listaMensajes.clear()
        this.listaMensajes.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_alerta)
        private val tvTexto: TextView = itemView.findViewById(R.id.tv_texto_alerta)
        private val btnMapa: Button = itemView.findViewById(R.id.btn_abrir_gps)

        fun bind(alerta: MensajeAlerta) {
            tvNombre.text = "${alerta.nombreVictima} (En Peligro)"
            tvTexto.text = alerta.mensaje

            btnMapa.setOnClickListener {
                onMapaClick(alerta)
            }
        }
    }
}