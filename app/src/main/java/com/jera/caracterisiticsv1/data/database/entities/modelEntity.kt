package com.jera.caracterisiticsv1.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected

@Entity(tableName = "modelsDetected_table")
data class ModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "make_name") val make_name: String,
    @ColumnInfo(name = "model_name") val model_name: String,
    @ColumnInfo(name = "years") val years: String,
    @ColumnInfo(name = "probability") val probability: Double,
    val path: String,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null,
    @ColumnInfo(name = "latitude") val latitude: Double? = null,
    @ColumnInfo(name = "longitude") val longitude: Double? = null,
    @ColumnInfo(name = "captureTimestamp") val captureTimestamp: Long? = null,

    // ── CarSpecs fields (nullable, se rellenan tras la llamada a CarSpecs API) ──

    // Body
    @ColumnInfo(name = "specs_body_type") val specsBodyType: String? = null,
    @ColumnInfo(name = "specs_doors") val specsDoors: String? = null,
    @ColumnInfo(name = "specs_seats") val specsSeats: String? = null,

    // Engine
    @ColumnInfo(name = "specs_engine_type") val specsEngineType: String? = null,
    @ColumnInfo(name = "specs_displacement_cm3") val specsDisplacementCm3: String? = null,
    @ColumnInfo(name = "specs_fuel_type") val specsFuelType: String? = null,
    @ColumnInfo(name = "specs_cylinders") val specsCylinders: String? = null,
    @ColumnInfo(name = "specs_power_hp") val specsPowerHp: String? = null,
    @ColumnInfo(name = "specs_torque_nm") val specsTorqueNm: String? = null,
    @ColumnInfo(name = "specs_fuel_tank_l") val specsFuelTankL: String? = null,

    // Performance
    @ColumnInfo(name = "specs_acceleration_0_100") val specsAcceleration0100: String? = null,
    @ColumnInfo(name = "specs_top_speed_kmh") val specsTopSpeedKmh: String? = null,
    @ColumnInfo(name = "specs_consumption_mixed") val specsConsumptionMixed: String? = null,
    @ColumnInfo(name = "specs_co2_g_km") val specsCo2GKm: String? = null,

    // Dimensions
    @ColumnInfo(name = "specs_length_mm") val specsLengthMm: String? = null,
    @ColumnInfo(name = "specs_width_mm") val specsWidthMm: String? = null,
    @ColumnInfo(name = "specs_height_mm") val specsHeightMm: String? = null,
    @ColumnInfo(name = "specs_wheelbase_mm") val specsWheelbaseMm: String? = null,
    @ColumnInfo(name = "specs_curb_weight_kg") val specsCurbWeightKg: String? = null,

    // Transmission
    @ColumnInfo(name = "specs_gearbox") val specsGearbox: String? = null,
    @ColumnInfo(name = "specs_drive_wheels") val specsDriveWheels: String? = null
)

fun ModelDetected.toDatabase() = ModelEntity(
    make_name = make_name,
    model_name = model_name,
    years = years,
    probability = probability,
    path = file.absolutePath
)

fun CarModel.toDatabase() = ModelEntity(
    make_name = make_name,
    model_name = model_name,
    years = years,
    probability = probability,
    path = path
)
