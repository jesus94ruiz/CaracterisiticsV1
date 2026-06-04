package com.jera.caracterisiticsv1.data.database.dao

import androidx.room.*
import com.jera.caracterisiticsv1.data.database.entities.DailyMissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMissionDao {

    @Query("SELECT * FROM daily_mission_table WHERE date_key = :dateKey")
    fun getMissionsForDate(dateKey: String): Flow<List<DailyMissionEntity>>

    @Query("SELECT * FROM daily_mission_table WHERE date_key = :dateKey")
    suspend fun getMissionsForDateSync(dateKey: String): List<DailyMissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<DailyMissionEntity>)

    @Update
    suspend fun updateMission(mission: DailyMissionEntity)

    @Query("DELETE FROM daily_mission_table WHERE date_key != :dateKey")
    suspend fun deleteOldMissions(dateKey: String)
}
