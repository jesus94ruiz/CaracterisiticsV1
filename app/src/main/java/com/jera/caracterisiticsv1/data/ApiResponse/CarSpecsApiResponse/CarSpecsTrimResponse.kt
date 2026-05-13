package com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse

import com.google.gson.annotations.SerializedName

// Lista de trims de una generación
data class CarSpecsTrimBasic(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("generation_id") val generationId: String? = null
)

// Detalle completo de un trim con sus specs
data class CarSpecsTrimDetail(

    @SerializedName("id")
    val id: Int,

    @SerializedName("make")
    val make: String? = null,

    @SerializedName("model")
    val model: String? = null,

    @SerializedName("generation")
    val generation: String? = null,

    @SerializedName("trim")
    val trim: String? = null,

    @SerializedName("bodyType")
    val bodyType: String? = null,

    @SerializedName("numberOfSeats")
    val numberOfSeats: String? = null,

    @SerializedName("lengthMm")
    val lengthMm: String? = null,

    @SerializedName("widthMm")
    val widthMm: String? = null,

    @SerializedName("heightMm")
    val heightMm: String? = null,

    @SerializedName("wheelbaseMm")
    val wheelbaseMm: String? = null,

    @SerializedName("capacityCm3")
    val engineDisplacementCm3: String? = null,

    @SerializedName("engineHp")
    val powerHp: String? = null,

    @SerializedName("engineHpRpm")
    val powerRpm: String? = null,

    @SerializedName("maximumTorqueNM")
    val torqueNm: String? = null,

    @SerializedName("turnoverOfMaximumTorqueRpm")
    val torqueRpm: String? = null,

    @SerializedName("engineType")
    val engineType: String? = null,

    @SerializedName("numberOfCylinders")
    val numberOfCylinders: String? = null,

    @SerializedName("fuelTankCapacityL")
    val fuelTankCapacityL: String? = null,

    @SerializedName("acceleration0To100KmPerHS")
    val acceleration0100KmhS: String? = null,

    @SerializedName("maxSpeedKmPerH")
    val maximumSpeedKmh: String? = null,

    @SerializedName("mixedFuelConsumptionPer100KmL")
    val fuelConsumptionMixed: String? = null,

    @SerializedName("curbWeightKg")
    val curbWeightKg: String? = null,

    @SerializedName("numberOfGears")
    val numberOfGears: String? = null,

    @SerializedName("driveWheels")
    val driveWheels: String? = null,

    @SerializedName("transmission")
    val gearbox: String? = null
)
