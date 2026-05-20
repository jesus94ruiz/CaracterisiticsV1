package com.jera.caracterisiticsv1.repository

import com.jera.caracterisiticsv1.data.database.dao.AchievementDao
import com.jera.caracterisiticsv1.data.database.dao.UserProfileDao
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import com.jera.caracterisiticsv1.data.database.entities.UserProfileEntity
import com.jera.caracterisiticsv1.utilities.XpManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class LevelUpResult(
    val newLevel: Int,
    val xpGained: Int,
    val leveledUp: Boolean,
    val newAchievements: List<AchievementEntity>
)

@Singleton
class UserRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val achievementDao: AchievementDao
) {

    // ── Canales de eventos (SharedFlow) ──────────────────────────────────────
    private val _xpGainedChannel = MutableSharedFlow<Int>(extraBufferCapacity = 8)
    val xpGainedChannel: SharedFlow<Int> = _xpGainedChannel.asSharedFlow()

    private val _levelUpChannel = MutableSharedFlow<Int>(extraBufferCapacity = 4)
    val levelUpChannel: SharedFlow<Int> = _levelUpChannel.asSharedFlow()

    private val _achievementChannel = MutableSharedFlow<List<AchievementEntity>>(extraBufferCapacity = 4)
    val achievementChannel: SharedFlow<List<AchievementEntity>> = _achievementChannel.asSharedFlow()


    // ── Profile ───────────────────────────────────────────────────────────────
    fun getUserProfileFlow(): Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    suspend fun ensureProfileExists() {
        if (userProfileDao.getUserProfileOnce() == null) {
            userProfileDao.insertOrUpdateProfile(UserProfileEntity())
            achievementDao.insertAchievements(defaultAchievements())
        }
    }

    // ── XP & Nivel ───────────────────────────────────────────────────────────
    /**
     * Añade XP al perfil tras una captura.
     * Devuelve [LevelUpResult] con el nuevo nivel, XP ganado y logros desbloqueados.
     */
    suspend fun addXpForCapture(
        probability: Double,
        isFirstCapture: Boolean,
        brandName: String,
        carsOfSameBrand: Int
    ): LevelUpResult {
        val profile = userProfileDao.getUserProfileOnce()
            ?: UserProfileEntity().also { userProfileDao.insertOrUpdateProfile(it) }

        val xpGained = XpManager.xpForCapture(probability, isFirstCapture)
        val oldTotalXp = profile.totalXp
        val newTotalXp = oldTotalXp + xpGained

        val oldLevel = XpManager.calculateLevel(oldTotalXp).first
        val (newLevel, currentXpInLevel, _) = XpManager.calculateLevel(newTotalXp)
        val newTotalCaptures = profile.totalCaptures + 1
        val newCarsCollected = if (isFirstCapture) profile.carsCollected + 1 else profile.carsCollected

        userProfileDao.updateStats(
            currentXp = currentXpInLevel,
            totalXp = newTotalXp,
            level = newLevel,
            carsCollected = newCarsCollected,
            totalCaptures = newTotalCaptures
        )

        val newAchievements = checkAchievements(
            totalCaptures = newTotalCaptures,
            carsCollected = newCarsCollected,
            carsOfSameBrand = carsOfSameBrand,
            probability = probability
        )

        val result = LevelUpResult(
            newLevel = newLevel,
            xpGained = xpGained,
            leveledUp = newLevel > oldLevel,
            newAchievements = newAchievements
        )

        // Emitir eventos para la UI
        _xpGainedChannel.tryEmit(xpGained)
        if (result.leveledUp) _levelUpChannel.tryEmit(newLevel)
        if (newAchievements.isNotEmpty()) _achievementChannel.tryEmit(newAchievements)

        return result
    }

    suspend fun addXpDirect(xp: Int) {
        val profile = userProfileDao.getUserProfileOnce() ?: return
        val newTotalXp = profile.totalXp + xp
        val (newLevel, currentXpInLevel, _) = XpManager.calculateLevel(newTotalXp)
        userProfileDao.updateStats(
            currentXp = currentXpInLevel,
            totalXp = newTotalXp,
            level = newLevel,
            carsCollected = profile.carsCollected,
            totalCaptures = profile.totalCaptures
        )
    }

    // ── Logros ────────────────────────────────────────────────────────────────
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>> =
        achievementDao.getAllAchievements()

    private suspend fun checkAchievements(
        totalCaptures: Int,
        carsCollected: Int,
        carsOfSameBrand: Int,
        probability: Double
    ): List<AchievementEntity> {
        val unlocked = mutableListOf<AchievementEntity>()

        suspend fun tryUnlock(id: String) {
            val ach = achievementDao.getAchievementById(id) ?: return
            if (!ach.isUnlocked) {
                achievementDao.unlockAchievement(id, System.currentTimeMillis())
                unlocked.add(ach.copy(isUnlocked = true))
                addXpDirect(ach.xpReward)
            }
        }

        // Primera captura
        if (totalCaptures == 1) tryUnlock("first_capture")

        // Coleccionista
        if (carsCollected >= 10) tryUnlock("collector_10")
        if (carsCollected >= 50) tryUnlock("collector_50")
        if (carsCollected >= 100) tryUnlock("collector_100")

        // Especialista de marca
        if (carsOfSameBrand >= 10) tryUnlock("brand_specialist")

        // Velocista (coche épico → probabilidad alta)
        if (probability >= 0.85) tryUnlock("speedster")

        // Progreso capturas
        achievementDao.updateProgress("collector_10", minOf(carsCollected, 10))
        achievementDao.updateProgress("collector_50", minOf(carsCollected, 50))
        achievementDao.updateProgress("collector_100", minOf(carsCollected, 100))

        return unlocked
    }

    // ── Achievements por defecto ──────────────────────────────────────────────
    private fun defaultAchievements(): List<AchievementEntity> = listOf(
        AchievementEntity(
            achievementId = "first_capture",
            title = "Primera Captura",
            description = "Fotografía tu primer coche",
            icon = "🏁",
            xpReward = 50,
            target = 1
        ),
        AchievementEntity(
            achievementId = "collector_10",
            title = "Coleccionista",
            description = "Recopila 10 modelos distintos",
            icon = "🚗",
            xpReward = 100,
            target = 10
        ),
        AchievementEntity(
            achievementId = "collector_50",
            title = "Gran Coleccionista",
            description = "Recopila 50 modelos distintos",
            icon = "🚙",
            xpReward = 300,
            target = 50
        ),
        AchievementEntity(
            achievementId = "collector_100",
            title = "Maestro del Garaje",
            description = "Recopila 100 modelos distintos",
            icon = "🏆",
            xpReward = 1000,
            target = 100
        ),
        AchievementEntity(
            achievementId = "brand_specialist",
            title = "Especialista de Marca",
            description = "Captura 10 coches de la misma marca",
            icon = "⭐",
            xpReward = 200,
            target = 10
        ),
        AchievementEntity(
            achievementId = "speedster",
            title = "Velocista",
            description = "Captura un coche deportivo de alta gama",
            icon = "⚡",
            xpReward = 75,
            target = 1
        )
    )
}
