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

    @Query("select * from tabla_alertas order by timestamp desc")
    fun obtenerTodasLasAlertas(): LiveData<List<AlertaEntity>>
}