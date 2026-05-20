package com.jera.caracterisiticsv1.data.database.dao

import androidx.room.*
import com.jera.caracterisiticsv1.data.database.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile_table WHERE userId = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile_table WHERE userId = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("""
        UPDATE user_profile_table 
        SET current_xp = :currentXp, total_xp = :totalXp, level = :level,
            cars_collected = :carsCollected, total_captures = :totalCaptures
        WHERE userId = 1
    """)
    suspend fun updateStats(
        currentXp: Int,
        totalXp: Int,
        level: Int,
        carsCollected: Int,
        totalCaptures: Int
    )
}
