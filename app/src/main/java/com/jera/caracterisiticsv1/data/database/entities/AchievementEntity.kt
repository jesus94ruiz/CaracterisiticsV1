package com.jera.caracterisiticsv1.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_table")
data class AchievementEntity(

    @PrimaryKey
    @ColumnInfo(name = "achievementId")
    val achievementId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    /** Emoji o clave de icono */
    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "xp_reward")
    val xpReward: Int,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false,

    /** Timestamp en milisegundos; null si aún no se ha desbloqueado */
    @ColumnInfo(name = "unlocked_date")
    val unlockedDate: Long? = null,

    /** Progreso actual hacia el objetivo */
    @ColumnInfo(name = "progress")
    val progress: Int = 0,

    /** Objetivo necesario para desbloquear */
    @ColumnInfo(name = "target")
    val target: Int = 1
)
