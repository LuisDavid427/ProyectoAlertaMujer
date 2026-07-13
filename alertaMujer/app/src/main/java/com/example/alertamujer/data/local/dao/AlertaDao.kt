package com.example.alertamujer.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alertamujer.data.local.entity.AlertaEntity

@Dao
interface AlertaDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertarAlerta(alerta: AlertaEntity)

    @Query("SELECT * FROM tabla_alertas ORDER BY timestamp DESC")
    fun obtenerTodasLasAlertas(): LiveData<List<AlertaEntity>>
}