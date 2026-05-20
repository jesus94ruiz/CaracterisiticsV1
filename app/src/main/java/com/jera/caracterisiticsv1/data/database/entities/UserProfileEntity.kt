package com.jera.caracterisiticsv1.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile_table")
data class UserProfileEntity(
    @PrimaryKey val userId: Int = 1,
    @ColumnInfo(name = "username") val username: String = "Driver",
    @ColumnInfo(name = "level") val level: Int = 1,
    @ColumnInfo(name = "current_xp") val currentXp: Int = 0,
    @ColumnInfo(name = "total_xp") val totalXp: Int = 0,
    @ColumnInfo(name = "cars_collected") val carsCollected: Int = 0,
    @ColumnInfo(name = "total_captures") val totalCaptures: Int = 0,
    @ColumnInfo(name = "created_date") val createdDate: Long = System.currentTimeMillis()
)
