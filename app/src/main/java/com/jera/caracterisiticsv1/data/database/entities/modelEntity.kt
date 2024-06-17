package com.jera.caracterisiticsv1.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected

@Entity(tableName = "modelsDetected_table")
data class ModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name ="make_name") val make_name: String,
    @ColumnInfo(name ="model_name") val model_name: String,
    @ColumnInfo(name ="years") val years: String,
    @ColumnInfo(name ="probability") val probability: Double,
    val path: String,
)

fun ModelDetected.toDatabase() = ModelEntity(make_name = make_name, model_name = model_name, years = years, probability = probability, path = file.absolutePath)
fun CarModel.toDatabase() = ModelEntity(make_name = make_name, model_name = model_name, years = years, probability = probability, path = path)

