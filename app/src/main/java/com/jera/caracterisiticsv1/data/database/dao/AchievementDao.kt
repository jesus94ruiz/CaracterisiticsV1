package com.jera.caracterisiticsv1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievement_table ORDER BY is_unlocked DESC, achievementId ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievement_table WHERE achievementId = :id")
    suspend fun getAchievementById(id: String): AchievementEntity?

    @Query(
        "UPDATE achievement_table SET is_unlocked = 1, unlocked_date = :timestamp WHERE achievementId = :id"
    )
    suspend fun unlockAchievement(id: String, timestamp: Long)

    @Query(
        "UPDATE achievement_table SET progress = :progress WHERE achievementId = :id"
    )
    suspend fun updateProgress(id: String, progress: Int)
}
