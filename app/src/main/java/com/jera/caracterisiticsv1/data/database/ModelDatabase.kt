package com.jera.caracterisiticsv1.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jera.caracterisiticsv1.data.database.dao.ModelEntityDao
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity

@Database(entities = [ModelEntity::class], version = 3, exportSchema = false)
abstract class ModelDatabase : RoomDatabase() {

    abstract fun modelDetectedDao(): ModelEntityDao

    companion object {
        /**
         * Migración de versión 2 → 3:
         * Añade todas las columnas de CarSpecs API (nullable con DEFAULT NULL).
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Body
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_body_type TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_doors TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_seats TEXT DEFAULT NULL")
                // Engine
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_engine_type TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_displacement_cm3 TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_fuel_type TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_cylinders TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_power_hp TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_torque_nm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_fuel_tank_l TEXT DEFAULT NULL")
                // Performance
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_acceleration_0_100 TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_top_speed_kmh TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_consumption_mixed TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_co2_g_km TEXT DEFAULT NULL")
                // Dimensions
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_length_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_width_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_height_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_wheelbase_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_curb_weight_kg TEXT DEFAULT NULL")
                // Transmission
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_gearbox TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_drive_wheels TEXT DEFAULT NULL")
            }
        }
    }
}
