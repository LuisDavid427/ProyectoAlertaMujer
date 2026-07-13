package com.example.alertamujer.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alertamujer.data.local.entity.ContactoEntity

@Dao
interface ContactoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarContacto(contacto: ContactoEntity)

    // Esta es la versión para la UI (Reactiva)
    @Query("SELECT * FROM tabla_contactos")
    fun obtenerTodosLosContactos(): LiveData<List<ContactoEntity>>

    // Esta es la versión para el SosViewModel (Síncrona)
    @Query("SELECT * FROM tabla_contactos")
    suspend fun obtenerTodosLosContactosSincrono(): List<ContactoEntity>

    @Query("DELETE FROM tabla_contactos WHERE id = :id")
    suspend fun eliminarContacto(id: Int)
}