package com.example.alertamujer.ui.contactos

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

import com.example.alertamujer.data.local.entity.ContactoEntity
import com.example.alertamujer.presentation.contactos.ContactosViewModel
import com.example.alertamujer.util.abrirActividad
import com.example.alertamujer.util.configurarBotonAtras
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

        // Observamos directamente el LiveData de Room
        viewModel.contactos.observe(this) { lista ->
            dibujarContactos(lista)
        }
    }

    private fun dibujarContactos(listaContactos: List<ContactoEntity>) {
        listaContactosLayout.removeAllViews()

        if (listaContactos.isEmpty()) {
            tvNoContacts.visibility = View.VISIBLE
        } else {
            tvNoContacts.visibility = View.GONE

            for (contacto in listaContactos) {
                val contactView = LayoutInflater.from(this)
                    .inflate(R.layout.item_contacto, listaContactosLayout, false)

                contactView.findViewById<TextView>(R.id.tv_nombre_contacto).text = contacto.nombre
                contactView.findViewById<TextView>(R.id.tv_numero_contacto).text = contacto.numero

                // Eliminar usando el ID real de la base de datos
                contactView.findViewById<ImageButton>(R.id.btn_eliminar_contacto).setOnClickListener {
                    mostrarDialogoDeConfirmacion(contacto.id)
                }

                listaContactosLayout.addView(contactView)
            }
        }
    }

    private fun mostrarDialogoDeConfirmacion(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Eliminar este contacto?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarContacto(id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}