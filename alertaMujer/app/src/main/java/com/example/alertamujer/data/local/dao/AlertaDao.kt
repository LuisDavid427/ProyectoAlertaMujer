package com.example.alertamujer.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alertamujer.data.local.entity.AlertaEntity

@Dao
interface AlertaDao {
    @Insert
    suspend fun insertarAlerta(alerta: AlertaEntity)


    @Query("SELECT * FROM tabla_alertas WHERE id_usuario = :idUsuarioActual ORDER BY timestamp DESC")
    fun obtenerTodasMisAlertas(idUsuarioActual: Int): LiveData<List<AlertaEntity>>

    // CHAT: Excluye las alertas cuyo id pertenezca al usuario de la sesión actual
    @Query("SELECT * FROM tabla_alertas WHERE id_usuario != :idUsuarioActual ORDER BY timestamp DESC")
    fun obtenerAlertasDeOtrosUsuarios(idUsuarioActual: Int): LiveData<List<AlertaEntity>>
}