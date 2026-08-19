package com.example.alertamujer.ui.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.data.local.entity.AlertaEntity

class HistorialAdapter(
    private var listaAlertas: List<AlertaEntity> = emptyList()
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFecha: TextView = itemView.findViewById(R.id.txt_fecha_alerta)
        val txtEstado: TextView = itemView.findViewById(R.id.txt_estado_alerta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_alerta, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val alerta = listaAlertas[position]

        // Asigna los valores reales de tu entidad
        holder.txtFecha.text = alerta.timestamp.toString()
        holder.txtEstado.text = "Registrada" // Cámbialo si tu entidad maneja un estado de alerta
    }

    override fun getItemCount(): Int = listaAlertas.size

    fun actualizarLista(nuevaLista: List<AlertaEntity>) {
        listaAlertas = nuevaLista
        notifyDataSetChanged()
    }
}