package com.example.alertamujer.contactos

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
import com.example.alertamujer.utils.configurarBotonAtras
import com.google.android.material.button.MaterialButton

class ContactosActivity : AppCompatActivity() {

    // ¡Conectamos el ViewModel!
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

        btnAgregarContacto.setOnClickListener {
            AddContactoActivity.start(this)
        }

        // Suscribimos la vista para que escuche al ViewModel
        observarViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Solo damos la orden, el ViewModel hace el trabajo pesado
        viewModel.cargarContactos()
    }

    private fun observarViewModel() {
        // Cada vez que el ViewModel actualiza la lista, se dispara esta función
        viewModel.contactos.observe(this) { lista ->
            dibujarContactos(lista)
        }
    }

    private fun dibujarContactos(listaContactos: List<Contacto>) {
        // 1. Limpiamos la pantalla
        listaContactosLayout.removeAllViews()

        // 2. Dibujamos según el estado de la lista
        if (listaContactos.isEmpty()) {
            tvNoContacts.visibility = View.VISIBLE
        } else {
            tvNoContacts.visibility = View.GONE

            for (contacto in listaContactos) {
                val contactView = LayoutInflater.from(this).inflate(R.layout.item_contacto, listaContactosLayout, false)

                val tvNombre = contactView.findViewById<TextView>(R.id.tv_nombre_contacto)
                val tvNumero = contactView.findViewById<TextView>(R.id.tv_numero_contacto)
                val btnEditar = contactView.findViewById<ImageButton>(R.id.btn_editar_contacto)
                val btnEliminar = contactView.findViewById<ImageButton>(R.id.btn_eliminar_contacto)

                // Usamos el modelo limpio de Kotlin
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
                // Le pasamos el problema al ViewModel
                viewModel.eliminarContacto(index)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ContactosActivity::class.java)
        }

        fun start(context: Context) {
            val intent = newIntent(context)
            context.startActivity(intent)
        }
    }
}