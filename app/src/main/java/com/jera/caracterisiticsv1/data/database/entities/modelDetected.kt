package com.jera.caracterisiticsv1.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity(tableName = "modelsDetected_table")
data class ModelDetected(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val make_name: String,
    val model_name: String,
    val years: String,
    val probability: Double,
    val path: String,
)

