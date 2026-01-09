package com.example.alertamujer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import org.json.JSONArray

class ContactosActivity : AppCompatActivity() {

    private lateinit var listaContactosLayout: LinearLayout
    private lateinit var tvNoContacts: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_contactos)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        listaContactosLayout = findViewById(R.id.lista_contactos)
        tvNoContacts = findViewById(R.id.tv_no_contacts)

        val btnAgregarContacto = findViewById<MaterialButton>(R.id.btn_agregar_contacto)
        btnAgregarContacto.setOnClickListener {
            val intent = Intent(this, AddContactoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarContactos()
    }

    private fun cargarContactos() {
        listaContactosLayout.removeAllViews()

        val sharedPreferences = getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        if (contactosArray.length() == 0) {
            tvNoContacts.visibility = View.VISIBLE
        } else {
            tvNoContacts.visibility = View.GONE
            for (i in 0 until contactosArray.length()) {
                val contacto = contactosArray.getJSONObject(i)
                val nombre = contacto.getString("nombre")
                val numero = contacto.getString("numero")

                val contactView = LayoutInflater.from(this).inflate(R.layout.item_contacto, listaContactosLayout, false)

                val tvNombre = contactView.findViewById<TextView>(R.id.tv_nombre_contacto)
                val tvNumero = contactView.findViewById<TextView>(R.id.tv_numero_contacto)
                val btnEditar = contactView.findViewById<ImageButton>(R.id.btn_editar_contacto)
                val btnEliminar = contactView.findViewById<ImageButton>(R.id.btn_eliminar_contacto)

                tvNombre.text = nombre
                tvNumero.text = numero

                btnEditar.setOnClickListener {
                    val intent = Intent(this, AddContactoActivity::class.java)
                    intent.putExtra("CONTACTO_INDEX", i)
                    intent.putExtra("CONTACTO_NOMBRE", nombre)
                    intent.putExtra("CONTACTO_NUMERO", numero)
                    startActivity(intent)
                }

                btnEliminar.setOnClickListener {
                    mostrarDialogoDeConfirmacion(i)
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
                eliminarContacto(index)
                cargarContactos()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarContacto(index: Int) {
        val sharedPreferences = getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val contactosJson = sharedPreferences.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)

        contactosArray.remove(index)

        editor.putString("contactos", contactosArray.toString())
        editor.apply()
    }
}