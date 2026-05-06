package com.example.alertamujer.ui.contactos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alertamujer.R
import com.example.alertamujer.presentation.contactos.Contacto
import com.example.alertamujer.presentation.contactos.ContactosViewModel
import com.example.alertamujer.utils.abrirActividad
import com.example.alertamujer.utils.configurarBotonAtras
import com.google.android.material.button.MaterialButton



class ContactosActivity : AppCompatActivity() {

    private val viewModel: ContactosViewModel by viewModels()

    private lateinit var listaContactosLayout: LinearLayout
    private lateinit var tvNoContacts: TextView
    private lateinit var btnAgregarContacto: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        configurarBotonAtras()

        listaContactosLayout = findViewById(R.id.lista_contactos)
        tvNoContacts = findViewById(R.id.tv_no_contacts)
        btnAgregarContacto = findViewById(R.id.btn_agregar_contacto)

        btnAgregarContacto.setOnClickListener { abrirActividad<AddContactoActivity>() }

        observarViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarContactos()
    }

    private fun observarViewModel() {
        viewModel.contactos.observe(this) { lista ->
            dibujarContactos(lista)
        }
    }

    private fun dibujarContactos(listaContactos: List<Contacto>) {
        listaContactosLayout.removeAllViews()

        if (listaContactos.isEmpty()) {
            tvNoContacts.visibility = View.VISIBLE
        } else {
            tvNoContacts.visibility = View.GONE

            for (contacto in listaContactos) {
                val contactView = LayoutInflater.from(this)
                    .inflate(R.layout.item_contacto, listaContactosLayout, false)

                val tvNombre = contactView.findViewById<TextView>(R.id.tv_nombre_contacto)
                val tvNumero = contactView.findViewById<TextView>(R.id.tv_numero_contacto)
                val btnEditar = contactView.findViewById<ImageButton>(R.id.btn_editar_contacto)
                val btnEliminar = contactView.findViewById<ImageButton>(R.id.btn_eliminar_contacto)

                tvNombre.text = contacto.nombre
                tvNumero.text = contacto.numero

                btnEditar.setOnClickListener {
                    val intent = Intent(this, AddContactoActivity::class.java)
                    intent.putExtra("CONTACTO_INDEX", contacto.index)
                    intent.putExtra("CONTACTO_NOMBRE", contacto.nombre)
                    intent.putExtra("CONTACTO_NUMERO", contacto.numero)
                    startActivity(intent)
                }

                btnEliminar.setOnClickListener {
                    mostrarDialogoDeConfirmacion(contacto.index)
                }

                listaContactosLayout.addView(contactView)
            }
        }
    }

    private fun mostrarDialogoDeConfirmacion(index: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar este contacto?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarContacto(index)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}