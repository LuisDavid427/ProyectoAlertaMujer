package com.example.alertamujer.ui.historial

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alertamujer.R
import com.example.alertamujer.presentation.historial.HistorialViewModel
import com.example.alertamujer.util.configurarBotonAtras

class HistorialActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var adapter: HistorialAdapter

    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var layoutEstadoVacio: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerHistorial = findViewById(R.id.recycler_historial)
        layoutEstadoVacio = findViewById(R.id.layout_estado_vacio)

        adapter = HistorialAdapter()
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        recyclerHistorial.adapter = adapter

        configurarBotonAtras()
        observarViewModel()
    }

    private fun observarViewModel() {
        viewModel.historialAlertas.observe(this) { lista ->
            if (lista.isNullOrEmpty()) {
                recyclerHistorial.visibility = View.GONE
                layoutEstadoVacio.visibility = View.VISIBLE
            } else {
                layoutEstadoVacio.visibility = View.GONE
                recyclerHistorial.visibility = View.VISIBLE
                adapter.actualizarLista(lista)
            }
        }
    }
}