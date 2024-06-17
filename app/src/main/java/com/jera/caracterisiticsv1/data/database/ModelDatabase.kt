package com.jera.caracterisiticsv1.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jera.caracterisiticsv1.data.database.dao.ModelEntityDao
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity

@Database(entities = [ModelEntity::class], version = 1, exportSchema = false)
abstract class ModelDatabase : RoomDatabase() {

    abstract fun modelDetectedDao(): ModelEntityDao
}