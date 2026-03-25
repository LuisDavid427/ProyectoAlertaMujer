package com.example.alertamujer
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent


class SosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ... tu código de la actividad ...
        }
    companion object {
        /**
         * Encapsula la creación del Intent.
         * Si en el futuro requiere parámetros, se piden aquí en la firma del método.
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, SosActivity::class.java)
        }

        // Opcionalmente, puedes encapsular también el arranque (más robusto)
        fun start(context: Context) {
            val intent = newIntent(context)
            context.startActivity(intent)
        }
    }
}
