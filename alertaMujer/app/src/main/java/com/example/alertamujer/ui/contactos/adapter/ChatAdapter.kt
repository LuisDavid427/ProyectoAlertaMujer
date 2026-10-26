package com.example.alertamujer.ui.contactos.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.BuildConfig
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
            val mensajeLower = alerta.mensaje?.lowercase() ?: ""
            val esAsalvo = mensajeLower.contains("finalizada") || mensajeLower.contains("a salvo")

            if (esAsalvo) {
                tvNombre.text = "${alerta.nombre_usuario} (Alerta Finalizada)"
                tvNombre.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                tvNombre.text = "${alerta.nombre_usuario} (En Peligro)"
                tvNombre.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }

            tvTexto.text = alerta.mensaje

            // 1. Abrir mapa GPS (botón)
            btnMapa.setOnClickListener {
                onMapaClick(alerta)
            }

            // 2. Manejo de evidencias con descarga mediante OkHttp y apertura automática
            if (!alerta.url_evidencia.isNullOrBlank()) {
                tvTexto.paintFlags = tvTexto.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                tvTexto.setTextColor(itemView.context.getColor(android.R.color.holo_blue_dark))

                var estaDescargando = false

                tvTexto.setOnClickListener {
                    if (estaDescargando) return@setOnClickListener
                    estaDescargando = true

                    try {
                        val context = itemView.context
                        val urlRaw = alerta.url_evidencia.trim()

                        val baseUrlLimpia = BuildConfig.BASE_URL.removeSuffix("/")
                        val urlFinal = if (urlRaw.startsWith("/")) {
                            "$baseUrlLimpia$urlRaw"
                        } else {
                            urlRaw
                        }

                        Log.d("DEBUG_EVIDENCIA", "Iniciando descarga y apertura: $urlFinal")
                        Toast.makeText(context, "Descargando y abriendo archivo...", Toast.LENGTH_SHORT).show()

                        val client = okhttp3.OkHttpClient()
                        val request = okhttp3.Request.Builder().url(urlFinal).get().build()

                        client.newCall(request).enqueue(object : okhttp3.Callback {
                            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                                e.printStackTrace()
                                estaDescargando = false
                                android.os.Handler(context.mainLooper).post {
                                    Toast.makeText(context, "Error de red al descargar", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                                if (!response.isSuccessful) {
                                    estaDescargando = false
                                    android.os.Handler(context.mainLooper).post {
                                        Toast.makeText(context, "Error del servidor: ${response.code}", Toast.LENGTH_SHORT).show()
                                    }
                                    return
                                }

                                try {
                                    val inputStream = response.body?.byteStream()
                                    val nombreArchivo = "AlertaMujer_${System.currentTimeMillis()}_${urlFinal.substringAfterLast("/")}"

                                    val directorioDescargas = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                                    if (!directorioDescargas.exists()) {
                                        directorioDescargas.mkdirs()
                                    }

                                    val archivoDestino = java.io.File(directorioDescargas, nombreArchivo)
                                    val outputStream = java.io.FileOutputStream(archivoDestino)

                                    inputStream?.copyTo(outputStream)
                                    outputStream.close()
                                    inputStream?.close()

                                    estaDescargando = false

                                    // Preparamos el Intent para abrir el archivo descargado automáticamente
                                    android.os.Handler(context.mainLooper).post {
                                        Toast.makeText(context, "¡Descarga completa! Abriendo...", Toast.LENGTH_SHORT).show()

                                        try {
                                            val fileUri = androidx.core.content.FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.fileprovider",
                                                archivoDestino
                                            )

                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(fileUri, context.contentResolver.getType(fileUri) ?: "image/*")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Guardado en Descargas, pero no se pudo abrir automáticamente", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    estaDescargando = false
                                    android.os.Handler(context.mainLooper).post {
                                        Toast.makeText(context, "Error guardando archivo", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        })

                    } catch (e: Exception) {
                        e.printStackTrace()
                        estaDescargando = false
                        Toast.makeText(itemView.context, "Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                tvTexto.paintFlags = tvTexto.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()
                tvTexto.setTextColor(itemView.context.getColor(android.R.color.black))
                tvTexto.setOnClickListener(null)
            }
        }
    }
}