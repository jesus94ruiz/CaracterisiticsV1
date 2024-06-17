package com.jera.caracterisiticsv1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity

@Dao
interface ModelEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modelDetected: ModelEntity)

    @Query("SELECT * FROM modelsDetected_table ORDER BY make_name DESC")
    suspend fun getAllModels(): List<ModelEntity>

    @Query("SELECT * FROM modelsDetected_table WHERE id = :id")
    suspend fun getModelById(id: Int): ModelEntity?
}