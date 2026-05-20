package com.jera.caracterisiticsv1.utilities

import kotlin.math.pow

/**
 * Gestor centralizado de reglas de XP y cálculo de niveles.
 */
object XpManager {

    // ── Constantes de XP por acción ───────────────────────────────────────────
    const val XP_CAR_COMMON = 10
    const val XP_CAR_RARE = 25
    const val XP_CAR_EPIC = 50
    const val XP_FIRST_CAPTURE_BONUS = 20   // bonus por primera captura de ese modelo
    const val XP_POI_VISITED = 15
    const val XP_BRAND_COLLECTION = 100     // completar colección de una marca

    // ── Clasificación por probabilidad ───────────────────────────────────────
    fun xpForCapture(probability: Double, isFirstCapture: Boolean): Int {
        val baseXp = when {
            probability >= 0.85 -> XP_CAR_EPIC
            probability >= 0.60 -> XP_CAR_RARE
            else                -> XP_CAR_COMMON
        }
        return baseXp + if (isFirstCapture) XP_FIRST_CAPTURE_BONUS else 0
    }

    // ── XP necesario para alcanzar un nivel ──────────────────────────────────
    // Formula: requiredXp = baseXp * (level ^ 1.5)
    private const val BASE_XP = 100.0

    fun xpRequiredForLevel(level: Int): Int {
        if (level <= 1) return 0
        return (BASE_XP * level.toDouble().pow(1.5)).toInt()
    }

    /**
     * Dado el XP total acumulado, calcula el nivel actual y el XP
     * restante dentro del nivel.
     * Devuelve Triple(level, currentXpInLevel, xpRequiredForNextLevel)
     */
    fun calculateLevel(totalXp: Int): Triple<Int, Int, Int> {
        var level = 1
        var xpConsumed = 0

        while (true) {
            val xpForNext = xpRequiredForLevel(level + 1)
            if (xpConsumed + xpForNext > totalXp) {
                val currentXpInLevel = totalXp - xpConsumed
                return Triple(level, currentXpInLevel, xpForNext)
            }
            xpConsumed += xpForNext
            level++
            if (level >= 100) break   // techo de nivel
        }
        return Triple(level, 0, xpRequiredForLevel(level + 1))
    }

    /** Devuelve true si con el nuevo XP total se ha subido de nivel respecto al anterior. */
    fun didLevelUp(oldTotalXp: Int, newTotalXp: Int): Boolean {
        return calculateLevel(newTotalXp).first > calculateLevel(oldTotalXp).first
    }
}
