package com.jera.caracterisiticsv1.data.domain.model

import com.jera.caracterisiticsv1.data.database.entities.ModelEntity

data class CarModel(
    val id: Int = 0,
    val make_name: String,
    val model_name: String,
    val years: String,
    val probability: Double,
    val path: String,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val captureTimestamp: Long? = null,

    // ── CarSpecs fields ──
    // Body
    val specsBodyType: String? = null,
    val specsDoors: String? = null,
    val specsSeats: String? = null,
    // Engine
    val specsEngineType: String? = null,
    val specsDisplacementCm3: String? = null,
    val specsFuelType: String? = null,
    val specsCylinders: String? = null,
    val specsPowerHp: String? = null,
    val specsTorqueNm: String? = null,
    val specsFuelTankL: String? = null,
    // Performance
    val specsAcceleration0100: String? = null,
    val specsTopSpeedKmh: String? = null,
    val specsConsumptionMixed: String? = null,
    val specsCo2GKm: String? = null,
    // Dimensions
    val specsLengthMm: String? = null,
    val specsWidthMm: String? = null,
    val specsHeightMm: String? = null,
    val specsWheelbaseMm: String? = null,
    val specsCurbWeightKg: String? = null,
    // Transmission
    val specsGearbox: String? = null,
    val specsDriveWheels: String? = null
)

fun ModelEntity.toDomain() = CarModel(
    id = id,
    make_name = make_name,
    model_name = model_name,
    years = years,
    probability = probability,
    path = path,
    imageUrl = imageUrl,
    latitude = latitude,
    longitude = longitude,
    captureTimestamp = captureTimestamp,
    // Body
    specsBodyType = specsBodyType,
    specsDoors = specsDoors,
    specsSeats = specsSeats,
    // Engine
    specsEngineType = specsEngineType,
    specsDisplacementCm3 = specsDisplacementCm3,
    specsFuelType = specsFuelType,
    specsCylinders = specsCylinders,
    specsPowerHp = specsPowerHp,
    specsTorqueNm = specsTorqueNm,
    specsFuelTankL = specsFuelTankL,
    // Performance
    specsAcceleration0100 = specsAcceleration0100,
    specsTopSpeedKmh = specsTopSpeedKmh,
    specsConsumptionMixed = specsConsumptionMixed,
    specsCo2GKm = specsCo2GKm,
    // Dimensions
    specsLengthMm = specsLengthMm,
    specsWidthMm = specsWidthMm,
    specsHeightMm = specsHeightMm,
    specsWheelbaseMm = specsWheelbaseMm,
    specsCurbWeightKg = specsCurbWeightKg,
    // Transmission
    specsGearbox = specsGearbox,
    specsDriveWheels = specsDriveWheels
)

fun CarModel.toEntity() = ModelEntity(
    id = id,
    make_name = make_name,
    model_name = model_name,
    years = years,
    probability = probability,
    path = path,
    imageUrl = imageUrl,
    latitude = latitude,
    longitude = longitude,
    captureTimestamp = captureTimestamp,
    // Body
    specsBodyType = specsBodyType,
    specsDoors = specsDoors,
    specsSeats = specsSeats,
    // Engine
    specsEngineType = specsEngineType,
    specsDisplacementCm3 = specsDisplacementCm3,
    specsFuelType = specsFuelType,
    specsCylinders = specsCylinders,
    specsPowerHp = specsPowerHp,
    specsTorqueNm = specsTorqueNm,
    specsFuelTankL = specsFuelTankL,
    // Performance
    specsAcceleration0100 = specsAcceleration0100,
    specsTopSpeedKmh = specsTopSpeedKmh,
    specsConsumptionMixed = specsConsumptionMixed,
    specsCo2GKm = specsCo2GKm,
    // Dimensions
    specsLengthMm = specsLengthMm,
    specsWidthMm = specsWidthMm,
    specsHeightMm = specsHeightMm,
    specsWheelbaseMm = specsWheelbaseMm,
    specsCurbWeightKg = specsCurbWeightKg,
    // Transmission
    specsGearbox = specsGearbox,
    specsDriveWheels = specsDriveWheels
)
