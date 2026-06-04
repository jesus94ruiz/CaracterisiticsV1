package com.jera.caracterisiticsv1.repository

import com.jera.caracterisiticsv1.data.database.dao.DailyMissionDao
import com.jera.caracterisiticsv1.data.database.entities.DailyMissionEntity
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// ── Tipos de misión ──────────────────────────────────────────────────────────
enum class MissionType {
    CAPTURE_ANY,        // Captura X coches hoy
    CAPTURE_BRAND,      // Captura X coches de marca Y
    CAPTURE_HP,         // Captura un coche con más de X CV
    CAPTURE_UNIQUE_BRANDS, // Captura coches de X marcas distintas
    CAPTURE_YEAR_OLD,   // Captura un coche fabricado antes de año X
    CAPTURE_YEAR_NEW,   // Captura un coche fabricado después de año X
    CAPTURE_FAST,       // Captura un coche que acelere 0-100 en menos de X seg
    CAPTURE_HEAVY,      // Captura un coche que pese más de X kg
    CAPTURE_ELECTRIC,   // Captura un coche eléctrico o híbrido
    CAPTURE_TORQUE,     // Captura un coche con más de X Nm de par
}

// ── Definición de plantilla de misión (pool) ─────────────────────────────────
private data class MissionTemplate(
    val type: MissionType,
    val title: String,
    val descriptionFn: (Int, String?) -> String,
    val targetValue: Int,
    val goal: Int,
    val xpReward: Int,
    val targetBrand: String? = null
)

@Singleton
class MissionRepository @Inject constructor(
    private val dailyMissionDao: DailyMissionDao
) {

    // ── Misiones recién completadas (pendientes de mostrar en CaptureRewardScreen) ──
    private val _pendingCompletedMissions = MutableStateFlow<List<DailyMissionEntity>>(emptyList())
    val pendingCompletedMissions: StateFlow<List<DailyMissionEntity>> = _pendingCompletedMissions

    fun setPendingCompleted(missions: List<DailyMissionEntity>) {
        _pendingCompletedMissions.value = missions
    }

    fun clearPendingCompleted() {
        _pendingCompletedMissions.value = emptyList()
    }

    // ── Misiones con progreso parcial (avanzaron pero no se completaron) ──────
    private val _pendingProgressedMissions = MutableStateFlow<List<DailyMissionEntity>>(emptyList())
    val pendingProgressedMissions: StateFlow<List<DailyMissionEntity>> = _pendingProgressedMissions

    fun setPendingProgressed(missions: List<DailyMissionEntity>) {
        _pendingProgressedMissions.value = missions
    }

    fun clearPendingProgressed() {
        _pendingProgressedMissions.value = emptyList()
    }


    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Marcas populares para misiones de marca
        private val POPULAR_BRANDS = listOf(
            "Nissan", "Toyota", "BMW", "Volkswagen", "Ford",
            "Mercedes-Benz", "Audi", "Honda", "Hyundai", "Renault",
            "Seat", "Peugeot", "Kia", "Mazda", "Opel",
            "Chevrolet", "Ferrari", "Porsche", "Lamborghini", "Tesla"
        )

        // Pool de plantillas de misión
        private fun buildPool(): List<MissionTemplate> = buildList {

            // ── CAPTURE_ANY ─────────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_ANY,
                title = "Coleccionista del día",
                descriptionFn = { v, _ -> "Captura $v coches hoy" },
                targetValue = 3, goal = 3, xpReward = 60
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_ANY,
                title = "Caza coches",
                descriptionFn = { v, _ -> "Captura $v coches hoy" },
                targetValue = 5, goal = 5, xpReward = 100
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_ANY,
                title = "Capturador imparable",
                descriptionFn = { v, _ -> "Captura $v coches en un solo día" },
                targetValue = 8, goal = 8, xpReward = 150
            ))

            // ── CAPTURE_BRAND ───────────────────────────────────────────────
            for (brand in POPULAR_BRANDS) {
                add(MissionTemplate(
                    type = MissionType.CAPTURE_BRAND,
                    title = "Fan de $brand",
                    descriptionFn = { v, b -> "Captura $v $b${if (v > 1) "s" else ""} hoy" },
                    targetValue = 2, goal = 2, xpReward = 80,
                    targetBrand = brand
                ))
                add(MissionTemplate(
                    type = MissionType.CAPTURE_BRAND,
                    title = "Especialista $brand",
                    descriptionFn = { v, b -> "Captura $v $b${if (v > 1) "s" else ""} hoy" },
                    targetValue = 3, goal = 3, xpReward = 120,
                    targetBrand = brand
                ))
            }

            // ── CAPTURE_HP ──────────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_HP,
                title = "Potencia pura",
                descriptionFn = { v, _ -> "Captura un coche con más de $v CV" },
                targetValue = 150, goal = 1, xpReward = 80
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_HP,
                title = "Buscador de bestias",
                descriptionFn = { v, _ -> "Captura un coche con más de $v CV" },
                targetValue = 300, goal = 1, xpReward = 150
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_HP,
                title = "Supercar hunter",
                descriptionFn = { v, _ -> "Captura un coche con más de $v CV" },
                targetValue = 500, goal = 1, xpReward = 250
            ))

            // ── CAPTURE_UNIQUE_BRANDS ────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_UNIQUE_BRANDS,
                title = "Explorador de marcas",
                descriptionFn = { v, _ -> "Captura coches de $v marcas distintas hoy" },
                targetValue = 2, goal = 2, xpReward = 90
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_UNIQUE_BRANDS,
                title = "Gran turismo",
                descriptionFn = { v, _ -> "Captura coches de $v marcas distintas hoy" },
                targetValue = 4, goal = 4, xpReward = 160
            ))

            // ── CAPTURE_YEAR_OLD ─────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_YEAR_OLD,
                title = "Clásico vintage",
                descriptionFn = { v, _ -> "Captura un coche fabricado antes de $v" },
                targetValue = 2000, goal = 1, xpReward = 100
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_YEAR_OLD,
                title = "Coche histórico",
                descriptionFn = { v, _ -> "Captura un coche fabricado antes de $v" },
                targetValue = 1990, goal = 1, xpReward = 180
            ))

            // ── CAPTURE_YEAR_NEW ─────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_YEAR_NEW,
                title = "Lo último del mercado",
                descriptionFn = { v, _ -> "Captura un coche fabricado después de $v" },
                targetValue = 2022, goal = 1, xpReward = 70
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_YEAR_NEW,
                title = "Recién salido del concesionario",
                descriptionFn = { v, _ -> "Captura un coche fabricado después de $v" },
                targetValue = 2023, goal = 1, xpReward = 90
            ))

            // ── CAPTURE_FAST ─────────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_FAST,
                title = "Velocidad relámpago",
                descriptionFn = { v, _ -> "Captura un coche que acelere 0-100 en menos de ${v}s" },
                targetValue = 6, goal = 1, xpReward = 120
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_FAST,
                title = "Piloto de carreras",
                descriptionFn = { v, _ -> "Captura un coche que acelere 0-100 en menos de ${v}s" },
                targetValue = 4, goal = 1, xpReward = 200
            ))

            // ── CAPTURE_ELECTRIC ─────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_ELECTRIC,
                title = "Conductor ecológico",
                descriptionFn = { _, _ -> "Captura un coche eléctrico o híbrido" },
                targetValue = 1, goal = 1, xpReward = 100
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_ELECTRIC,
                title = "Futuro verde",
                descriptionFn = { v, _ -> "Captura $v coches eléctricos o híbridos hoy" },
                targetValue = 2, goal = 2, xpReward = 180
            ))

            // ── CAPTURE_TORQUE ────────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_TORQUE,
                title = "Torque máximo",
                descriptionFn = { v, _ -> "Captura un coche con más de $v Nm de par motor" },
                targetValue = 400, goal = 1, xpReward = 110
            ))
            add(MissionTemplate(
                type = MissionType.CAPTURE_TORQUE,
                title = "Fuerza bruta",
                descriptionFn = { v, _ -> "Captura un coche con más de $v Nm de par motor" },
                targetValue = 700, goal = 1, xpReward = 200
            ))

            // ── CAPTURE_HEAVY ─────────────────────────────────────────────────
            add(MissionTemplate(
                type = MissionType.CAPTURE_HEAVY,
                title = "El más pesado",
                descriptionFn = { v, _ -> "Captura un coche que pese más de ${v} kg" },
                targetValue = 2000, goal = 1, xpReward = 90
            ))
        }
    }

    /** Clave del día actual */
    private fun todayKey(): String = LocalDate.now().format(DATE_FORMATTER)

    /** Flujo reactivo de las misiones de hoy */
    fun getTodayMissions(): Flow<List<DailyMissionEntity>> =
        dailyMissionDao.getMissionsForDate(todayKey())

    /**
     * Genera (si no existen) las 3 misiones del día.
     * Se llama al iniciar el ViewModel de misiones.
     */
    suspend fun ensureDailyMissions() {
        val today = todayKey()
        dailyMissionDao.deleteOldMissions(today)

        val existing = dailyMissionDao.getMissionsForDateSync(today)
        if (existing.isNotEmpty()) return

        val pool = buildPool()
        val selected = selectMissions(pool, count = 3)
        val entities = selected.mapIndexed { idx, t ->
            val desc = t.descriptionFn(t.targetValue, t.targetBrand)
            DailyMissionEntity(
                missionId = "${today}_${t.type.name}_${t.targetBrand ?: t.targetValue}_$idx",
                type = t.type.name,
                title = t.title,
                description = desc,
                targetBrand = t.targetBrand,
                targetValue = t.targetValue,
                currentProgress = 0,
                goal = t.goal,
                xpReward = t.xpReward,
                dateKey = today,
                isCompleted = false
            )
        }
        dailyMissionDao.insertMissions(entities)
    }

    /**
     * Selecciona [count] misiones del pool asegurando variedad de tipos.
     */
    private fun selectMissions(pool: List<MissionTemplate>, count: Int): List<MissionTemplate> {
        val shuffled = pool.shuffled()
        val selected = mutableListOf<MissionTemplate>()
        val usedTypes = mutableSetOf<MissionType>()

        // Primera pasada: un tipo diferente por misión
        for (t in shuffled) {
            if (selected.size >= count) break
            if (t.type !in usedTypes) {
                selected.add(t)
                usedTypes.add(t.type)
            }
        }
        // Segunda pasada por si no hay suficientes tipos únicos
        for (t in shuffled) {
            if (selected.size >= count) break
            if (t !in selected) selected.add(t)
        }
        return selected.take(count)
    }

    /**
     * Evalúa las misiones activas tras capturar un coche.
     * @return Lista de misiones recién completadas (para otorgar XP).
     */
    suspend fun onCarCaptured(model: ModelEntity): List<DailyMissionEntity> {
        val today = todayKey()
        val missions = dailyMissionDao.getMissionsForDateSync(today)
        val justCompleted = mutableListOf<DailyMissionEntity>()

        for (mission in missions) {
            if (mission.isCompleted) continue

            val type = runCatching { MissionType.valueOf(mission.type) }.getOrNull() ?: continue
            val increment = computeIncrement(type, mission, model)

            if (increment > 0) {
                val newProgress = (mission.currentProgress + increment).coerceAtMost(mission.goal)
                val nowCompleted = newProgress >= mission.goal
                val updated = mission.copy(
                    currentProgress = newProgress,
                    isCompleted = nowCompleted
                )
                dailyMissionDao.updateMission(updated)
                if (nowCompleted) justCompleted.add(updated)
            }
        }
        return justCompleted
    }

    /**
     * Calcula cuánto incrementar el progreso de una misión dada la captura.
     * Para misiones de tipo "al menos X", solo se hace 1 si se cumple el criterio.
     */
    private fun computeIncrement(
        type: MissionType,
        mission: DailyMissionEntity,
        model: ModelEntity
    ): Int {
        return when (type) {
            MissionType.CAPTURE_ANY -> 1

            MissionType.CAPTURE_BRAND ->
                if (model.make_name.equals(mission.targetBrand, ignoreCase = true)) 1 else 0

            MissionType.CAPTURE_HP -> {
                val hp = model.specsPowerHp?.toDoubleOrNull()?.toInt() ?: 0
                if (hp > mission.targetValue) 1 else 0
            }

            MissionType.CAPTURE_UNIQUE_BRANDS -> {
                // Progreso = número de marcas únicas capturadas hoy; calculado externamente
                // Aquí siempre incrementamos 1 y dejamos que el goal controle el límite
                1
            }

            MissionType.CAPTURE_YEAR_OLD -> {
                val year = model.years.toIntOrNull() ?: 9999
                if (year < mission.targetValue) 1 else 0
            }

            MissionType.CAPTURE_YEAR_NEW -> {
                val year = model.years.toIntOrNull() ?: 0
                if (year > mission.targetValue) 1 else 0
            }

            MissionType.CAPTURE_FAST -> {
                val accel = model.specsAcceleration0100?.toDoubleOrNull() ?: 99.0
                if (accel < mission.targetValue.toDouble()) 1 else 0
            }

            MissionType.CAPTURE_HEAVY -> {
                val weight = model.specsCurbWeightKg?.toDoubleOrNull()?.toInt() ?: 0
                if (weight > mission.targetValue) 1 else 0
            }

            MissionType.CAPTURE_ELECTRIC -> {
                val fuel = model.specsFuelType?.lowercase() ?: ""
                if (fuel.contains("electric") || fuel.contains("hybrid") ||
                    fuel.contains("eléctric") || fuel.contains("híbrid") ||
                    fuel.contains("electric") || fuel.contains("plug")
                ) 1 else 0
            }

            MissionType.CAPTURE_TORQUE -> {
                val torque = model.specsTorqueNm?.toDoubleOrNull()?.toInt() ?: 0
                if (torque > mission.targetValue) 1 else 0
            }
        }
    }
}
