package com.jera.caracterisiticsv1.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jera.caracterisiticsv1.data.database.dao.AchievementDao
import com.jera.caracterisiticsv1.data.database.dao.DailyMissionDao
import com.jera.caracterisiticsv1.data.database.dao.ModelEntityDao
import com.jera.caracterisiticsv1.data.database.dao.UserProfileDao
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import com.jera.caracterisiticsv1.data.database.entities.DailyMissionEntity
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import com.jera.caracterisiticsv1.data.database.entities.UserProfileEntity

@Database(
    entities = [ModelEntity::class, UserProfileEntity::class, AchievementEntity::class, DailyMissionEntity::class],
    version = 5,
    exportSchema = false
)
abstract class ModelDatabase : RoomDatabase() {

    abstract fun modelDetectedDao(): ModelEntityDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dailyMissionDao(): DailyMissionDao

    companion object {

        /** Migración 2 → 3: añade columnas CarSpecs */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_body_type TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_doors TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_seats TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_engine_type TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_displacement_cm3 TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_fuel_type TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_cylinders TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_power_hp TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_torque_nm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_fuel_tank_l TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_acceleration_0_100 TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_top_speed_kmh TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_consumption_mixed TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_co2_g_km TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_length_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_width_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_height_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_wheelbase_mm TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_curb_weight_kg TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_gearbox TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE modelsDetected_table ADD COLUMN specs_drive_wheels TEXT DEFAULT NULL")
            }
        }

        /** Migración 4 → 5: añade tabla de misiones diarias */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_mission_table (
                        mission_id TEXT NOT NULL PRIMARY KEY,
                        type TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        target_brand TEXT DEFAULT NULL,
                        target_value INTEGER NOT NULL DEFAULT 1,
                        current_progress INTEGER NOT NULL DEFAULT 0,
                        goal INTEGER NOT NULL DEFAULT 1,
                        xp_reward INTEGER NOT NULL DEFAULT 50,
                        date_key TEXT NOT NULL,
                        is_completed INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        /** Migración 3 → 4: añade tablas de perfil y logros */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_profile_table (
                        userId INTEGER NOT NULL PRIMARY KEY,
                        username TEXT NOT NULL DEFAULT 'Driver',
                        level INTEGER NOT NULL DEFAULT 1,
                        current_xp INTEGER NOT NULL DEFAULT 0,
                        total_xp INTEGER NOT NULL DEFAULT 0,
                        cars_collected INTEGER NOT NULL DEFAULT 0,
                        total_captures INTEGER NOT NULL DEFAULT 0,
                        created_date INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS achievement_table (
                        achievementId TEXT NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        icon TEXT NOT NULL,
                        xp_reward INTEGER NOT NULL,
                        is_unlocked INTEGER NOT NULL DEFAULT 0,
                        unlocked_date INTEGER,
                        progress INTEGER NOT NULL DEFAULT 0,
                        target INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
