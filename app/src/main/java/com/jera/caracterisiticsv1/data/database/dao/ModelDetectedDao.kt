package com.jera.caracterisiticsv1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity

@Dao
interface ModelEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modelDetected: ModelEntity): Long

    @Query("SELECT * FROM modelsDetected_table ORDER BY make_name DESC")
    suspend fun getAllModels(): List<ModelEntity>

    @Query("SELECT * FROM modelsDetected_table WHERE id = :id")
    suspend fun getModelById(id: Int): ModelEntity?

    @Query(
        """UPDATE modelsDetected_table SET
        specs_body_type         = :bodyType,
        specs_seats             = :seats,
        specs_engine_type       = :engineType,
        specs_displacement_cm3  = :displacementCm3,
        specs_cylinders         = :cylinders,
        specs_power_hp          = :powerHp,
        specs_torque_nm         = :torqueNm,
        specs_fuel_tank_l       = :fuelTankL,
        specs_acceleration_0_100 = :acceleration0100,
        specs_top_speed_kmh     = :topSpeedKmh,
        specs_consumption_mixed = :consumptionMixed,
        specs_length_mm         = :lengthMm,
        specs_width_mm          = :widthMm,
        specs_height_mm         = :heightMm,
        specs_wheelbase_mm      = :wheelbaseMm,
        specs_curb_weight_kg    = :curbWeightKg,
        specs_gearbox           = :gearbox,
        specs_drive_wheels      = :driveWheels
        WHERE id = :id"""
    )
    suspend fun updateSpecs(
        id: Int,
        bodyType: String?,
        seats: String?,
        engineType: String?,
        displacementCm3: String?,
        cylinders: String?,
        powerHp: String?,
        torqueNm: String?,
        fuelTankL: String?,
        acceleration0100: String?,
        topSpeedKmh: String?,
        consumptionMixed: String?,
        lengthMm: String?,
        widthMm: String?,
        heightMm: String?,
        wheelbaseMm: String?,
        curbWeightKg: String?,
        gearbox: String?,
        driveWheels: String?
    )
}
