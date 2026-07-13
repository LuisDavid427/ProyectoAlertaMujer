package com.example.alertamujer.ui.contactos.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.data.model.MensajeAlerta

class ChatAdapter(
    private var listaMensajes: MutableList<MensajeAlerta>,
    private val onMapaClick: (MensajeAlerta) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_burbuja_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(listaMensajes[position])
    }

    override fun getItemCount(): Int = listaMensajes.size

    fun actualizarMensajes(nuevaLista: List<MensajeAlerta>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = listaMensajes.size
            override fun getNewListSize() = nuevaLista.size
            override fun areItemsTheSame(o: Int, n: Int) = listaMensajes[o].id_alerta == nuevaLista[n].id_alerta
            override fun areContentsTheSame(o: Int, n: Int) = listaMensajes[o] == nuevaLista[n]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listaMensajes.clear()
        this.listaMensajes.addAll(nuevaLista)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_alerta)
        private val tvTexto: TextView = itemView.findViewById(R.id.tv_texto_alerta)
        private val btnMapa: Button = itemView.findViewById(R.id.btn_abrir_gps)

        fun bind(alerta: MensajeAlerta) {
            tvNombre.text = "${alerta.nombre_usuario} (En Peligro)"
            tvTexto.text = alerta.mensaje

            btnMapa.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:${alerta.latitud},${alerta.longitud}?q=${alerta.latitud},${alerta.longitud}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                itemView.context.startActivity(mapIntent)
            }
        }
    }
}