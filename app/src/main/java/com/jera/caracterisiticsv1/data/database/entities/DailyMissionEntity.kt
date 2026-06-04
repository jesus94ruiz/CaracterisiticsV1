package com.jera.caracterisiticsv1.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una misión diaria persistida en Room.
 *
 * @param missionId     ID único de la misión (p.ej. "2024-06-01_CAPTURE_BRAND_NISSAN")
 * @param type          Tipo de misión (ver MissionType)
 * @param title         Título visible al usuario
 * @param description   Descripción detallada
 * @param targetBrand   Marca objetivo (solo para CAPTURE_BRAND)
 * @param targetValue   Valor numérico objetivo (cantidad, CV, año, etc.)
 * @param currentProgress Progreso actual (0..goal)
 * @param goal          Objetivo numérico completo
 * @param xpReward      XP que otorga al completarse
 * @param dateKey       Clave de fecha "YYYY-MM-DD" para reseteo diario
 * @param isCompleted   Si la misión ya fue completada hoy
 */
@Entity(tableName = "daily_mission_table")
data class DailyMissionEntity(
    @PrimaryKey
    @ColumnInfo(name = "mission_id")
    val missionId: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "target_brand")
    val targetBrand: String? = null,

    @ColumnInfo(name = "target_value")
    val targetValue: Int = 1,

    @ColumnInfo(name = "current_progress")
    val currentProgress: Int = 0,

    @ColumnInfo(name = "goal")
    val goal: Int = 1,

    @ColumnInfo(name = "xp_reward")
    val xpReward: Int = 50,

    @ColumnInfo(name = "date_key")
    val dateKey: String,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
