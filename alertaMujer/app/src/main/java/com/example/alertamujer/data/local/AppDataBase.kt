package com.example.alertamujer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.alertamujer.data.local.dao.AlertaDao
import com.example.alertamujer.data.local.dao.ContactoDao
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.data.local.entity.ContactoEntity

// Definimos la base de datos y le decimos qué entidades contiene
@Database(entities = [AlertaEntity::class, ContactoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos el DAO para que el ViewModel lo pueda usar
    abstract fun alertaDao(): AlertaDao
    abstract fun contactoDao(): ContactoDao

    companion object {
        // Volatile asegura que el valor de INSTANCE siempre esté actualizado en todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devolvemos. Si no, la creamos de forma segura.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_alertas_mujer" // Nombre del archivo de la base de datos
                )
                    .fallbackToDestructiveMigration() // Útil en desarrollo: borra datos si cambias la estructura de la tabla
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}