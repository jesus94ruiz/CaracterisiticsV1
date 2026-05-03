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
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("generation_id") val generationId: String? = null,
    @SerializedName("body_type") val bodyType: String? = null,
    @SerializedName("number_of_doors") val numberOfDoors: String? = null,
    @SerializedName("number_of_seats") val numberOfSeats: String? = null,
    // Engine
    @SerializedName("engine_displacement_cm3") val engineDisplacementCm3: String? = null,
    @SerializedName("power_hp") val powerHp: String? = null,
    @SerializedName("power_rpm") val powerRpm: String? = null,
    @SerializedName("torque_nm") val torqueNm: String? = null,
    @SerializedName("torque_rpm") val torqueRpm: String? = null,
    @SerializedName("engine_type") val engineType: String? = null,
    @SerializedName("number_of_cylinders") val numberOfCylinders: String? = null,
    @SerializedName("fuel_type") val fuelType: String? = null,
    @SerializedName("fuel_tank_capacity_l") val fuelTankCapacityL: String? = null,
    // Performance
    @SerializedName("acceleration_0_100_kmh_s") val acceleration0100KmhS: String? = null,
    @SerializedName("maximum_speed_kmh") val maximumSpeedKmh: String? = null,
    @SerializedName("mixed_fuel_consumption_per_100km_l") val fuelConsumptionMixed: String? = null,
    @SerializedName("co2_emissions_g_km") val co2EmissionsGKm: String? = null,
    // Dimensions
    @SerializedName("length_mm") val lengthMm: String? = null,
    @SerializedName("width_mm") val widthMm: String? = null,
    @SerializedName("height_mm") val heightMm: String? = null,
    @SerializedName("wheelbase_mm") val wheelbaseMm: String? = null,
    @SerializedName("curb_weight_kg") val curbWeightKg: String? = null,
    @SerializedName("gross_weight_kg") val grossWeightKg: String? = null,
    // Transmission
    @SerializedName("gearbox") val gearbox: String? = null,
    @SerializedName("number_of_gears") val numberOfGears: String? = null,
    @SerializedName("drive_wheels") val driveWheels: String? = null
)
