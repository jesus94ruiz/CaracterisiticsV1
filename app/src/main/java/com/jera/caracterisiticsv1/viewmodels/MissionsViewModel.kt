package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.database.entities.DailyMissionEntity
import com.jera.caracterisiticsv1.repository.MissionRepository
import com.jera.caracterisiticsv1.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

data class MissionsUiState(
    val missions: List<DailyMissionEntity> = emptyList(),
    val isLoading: Boolean = true,
    val completedMissionXp: Int = 0,          // XP de la última misión completada (para animación)
    val lastCompletedMissionTitle: String = "" // Título de la última misión completada
)

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionsUiState())
    val uiState: StateFlow<MissionsUiState> = _uiState.asStateFlow()

    /** Misiones recién completadas: leídas por CaptureRewardScreen */
    val pendingCompletedMissions: StateFlow<List<DailyMissionEntity>> =
        missionRepository.pendingCompletedMissions
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun clearPendingCompleted() = missionRepository.clearPendingCompleted()

    init {
        viewModelScope.launch {
            // Genera las misiones del día si no existen
            missionRepository.ensureDailyMissions()

            // Observa los cambios en las misiones del día
            missionRepository.getTodayMissions().collectLatest { missions ->
                _uiState.value = _uiState.value.copy(
                    missions = missions,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Resetea la notificación de misión completada una vez consumida por la UI.
     */
    fun clearCompletedMissionNotification() {
        _uiState.value = _uiState.value.copy(
            completedMissionXp = 0,
            lastCompletedMissionTitle = ""
        )
    }

    // ── Propiedades derivadas útiles para la UI ──────────────────────────────

    val allMissionsCompleted: Boolean
        get() = _uiState.value.missions.isNotEmpty() &&
                _uiState.value.missions.all { it.isCompleted }

    val completedCount: Int
        get() = _uiState.value.missions.count { it.isCompleted }

    val totalXpAvailable: Int
        get() = _uiState.value.missions.sumOf { it.xpReward }

    val earnedXp: Int
        get() = _uiState.value.missions
            .filter { it.isCompleted }
            .sumOf { it.xpReward }
}
