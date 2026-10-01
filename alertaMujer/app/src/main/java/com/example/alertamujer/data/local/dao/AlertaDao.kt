package com.example.alertamujer.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alertamujer.data.local.entity.AlertaEntity

@Dao
interface AlertaDao {
    // 🟢 Usamos @Insert normal para activar el autoincremento en id_local = 0
    @Insert
    suspend fun insertarAlerta(alerta: AlertaEntity)

    @Query("SELECT * FROM tabla_alertas ORDER BY timestamp DESC")
    fun obtenerTodasLasAlertas(): LiveData<List<AlertaEntity>>
}