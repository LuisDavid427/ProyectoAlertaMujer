package com.example.alertamujer.utils
import com.example.alertamujer.R
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.configurarBotonAtras(){
    val btnBack = findViewById<ImageButton>(R.id.btn_back)

    btnBack?.setOnClickListener{
        finish()
    }
}