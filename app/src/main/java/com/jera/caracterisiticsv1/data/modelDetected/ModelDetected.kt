package com.jera.caracterisiticsv1.data.modelDetected
import android.os.Parcelable
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ModelDetected (
    val make_name: String,
    val model_name: String,
    val years: String,
    val probability: Double,
    val searchedImages: MutableList<String>,
    val file: File,
): Parcelable

fun ModelDetected.toDatabase() = ModelEntity(make_name = make_name, model_name = model_name, years = years, probability = probability, path = file.absolutePath)