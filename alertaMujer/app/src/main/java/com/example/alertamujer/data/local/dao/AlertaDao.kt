package com.example.alertamujer.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alertamujer.data.local.entity.AlertaEntity

@Dao
interface AlertaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAlerta(alerta: AlertaEntity)

    @Query("SELECT * FROM tabla_alertas WHERE id_alerta = :idAlerta LIMIT 1")
    suspend fun obtenerAlertaPorIdAlerta(idAlerta: Int): AlertaEntity?

    @Query("SELECT * FROM tabla_alertas WHERE id_usuario = :idUsuarioActual ORDER BY timestamp DESC")
    fun obtenerTodasMisAlertas(idUsuarioActual: Int): LiveData<List<AlertaEntity>>

    @Query("SELECT * FROM tabla_alertas WHERE id_usuario != :idUsuarioActual ORDER BY timestamp DESC")
    fun obtenerAlertasDeOtrosUsuarios(idUsuarioActual: Int): LiveData<List<AlertaEntity>>
}