package com.jera.caracterisiticsv1.data.domain.model

import com.jera.caracterisiticsv1.data.database.entities.ModelEntity


data class CarModel(
    val make_name: String,
    val model_name: String,
    val years: String,
    val probability: Double,
    val path: String,
)

fun ModelEntity.toDomain() = CarModel(make_name, model_name, years, probability, path)