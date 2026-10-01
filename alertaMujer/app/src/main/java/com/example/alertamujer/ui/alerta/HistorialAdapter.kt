package com.example.alertamujer.ui.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.data.local.entity.AlertaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorialAdapter(
    private var listaAlertas: List<AlertaEntity> = emptyList()
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txt_titulo_alerta) // 🟢
        val txtEstado: TextView = itemView.findViewById(R.id.txt_estado_alerta)
        val txtFecha: TextView = itemView.findViewById(R.id.txt_fecha_alerta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_alerta, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val alerta = listaAlertas[position]

        val sdf = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault())
        val fechaFormateada = sdf.format(Date(alerta.timestamp))

        // 🟢 Asignamos el título con el nombre y el ID
        holder.txtTitulo.text = "${alerta.nombre_usuario} • Alerta #${alerta.id_alerta}"
        holder.txtEstado.text = "✓ Alerta enviada con éxito"
        holder.txtFecha.text = fechaFormateada
    }

    override fun getItemCount(): Int = listaAlertas.size

    fun actualizarLista(nuevaLista: List<AlertaEntity>) {
        listaAlertas = nuevaLista
        notifyDataSetChanged()
    }
}