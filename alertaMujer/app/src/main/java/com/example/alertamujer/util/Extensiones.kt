package com.example.alertamujer.util
import com.example.alertamujer.R
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Context
import android.content.Intent

fun AppCompatActivity.configurarBotonAtras(){
    val btnBack = findViewById<ImageButton>(R.id.btn_back)

    btnBack?.setOnClickListener{
        finish()
    }
}
// Esta función abre cualquier activity, con o sin datos extra
inline fun <reified T : Activity> Context.abrirActividad(extras: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.extras() // Aplica los datos extra si los enviaste
    startActivity(intent)
}