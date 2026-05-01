package com.jera.caracterisiticsv1.data.domain.model

import com.jera.caracterisiticsv1.data.database.entities.ModelEntity


data class CarModel(
    val id: Int = 0,
    val make_name: String,
    val model_name: String,
    val years: String,
    val probability: Double,
    val path: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val captureTimestamp: Long? = null
)

fun ModelEntity.toDomain() = CarModel(
    id = id,
    make_name = make_name,
    model_name = model_name,
    years = years,
    probability = probability,
    path = path,
    latitude = latitude,
    longitude = longitude,
    captureTimestamp = captureTimestamp
)

fun CarModel.toEntity() = ModelEntity(
    id = id,
    make_name = make_name,
    model_name = model_name,
    years = years,
    probability = probability,
    path = path,
    latitude = latitude,
    longitude = longitude,
    captureTimestamp = captureTimestamp
)
