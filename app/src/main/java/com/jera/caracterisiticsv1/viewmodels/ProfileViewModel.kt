package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import com.jera.caracterisiticsv1.data.database.entities.UserProfileEntity
import com.jera.caracterisiticsv1.repository.UserRepository
import com.jera.caracterisiticsv1.utilities.XpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Evento de notificación de logro ──────────────────────────────────────────
data class AchievementEvent(
    val icon: String,
    val title: String,
    val description: String
)

data class ProfileUiState(
    val profile: UserProfileEntity = UserProfileEntity(),
    val achievements: List<AchievementEntity> = emptyList(),
    val xpForNextLevel: Int = 100,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // ── Eventos efímeros de feedback ─────────────────────────────────────────
    private val _xpGainedEvent = MutableStateFlow<Int?>(null)
    val xpGainedEvent: StateFlow<Int?> = _xpGainedEvent.asStateFlow()

    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent: StateFlow<Int?> = _levelUpEvent.asStateFlow()

    private val _pendingAchievements = MutableStateFlow<List<AchievementEvent>>(emptyList())
    val pendingAchievements: StateFlow<List<AchievementEvent>> = _pendingAchievements.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.ensureProfileExists()
        }
        collectProfile()
        collectAchievements()
        collectEvents()
    }

    private fun collectProfile() {
        viewModelScope.launch {
            userRepository.getUserProfileFlow().collect { profile ->
                if (profile != null) {
                    val (_, _, xpForNext) = XpManager.calculateLevel(profile.totalXp)
                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        xpForNextLevel = xpForNext,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun collectAchievements() {
        viewModelScope.launch {
            userRepository.getAllAchievementsFlow().collect { list ->
                _uiState.value = _uiState.value.copy(achievements = list)
            }
        }
    }

    private fun collectEvents() {
        // XP ganado
        viewModelScope.launch {
            userRepository.xpGainedChannel.collect { xp ->
                _xpGainedEvent.value = xp
            }
        }
        // Subida de nivel
        viewModelScope.launch {
            userRepository.levelUpChannel.collect { level ->
                _levelUpEvent.value = level
            }
        }
        // Logros desbloqueados — se acolan a la cola
        viewModelScope.launch {
            userRepository.achievementChannel.collect { achievements ->
                val events = achievements.map { ach ->
                    AchievementEvent(
                        icon = ach.icon,
                        title = ach.title,
                        description = ach.description
                    )
                }
                _pendingAchievements.value = _pendingAchievements.value + events
            }
        }
    }

    // ── Consume functions (llamadas desde UI tras mostrar el feedback) ────────
    fun consumeXpGainedEvent() { _xpGainedEvent.value = null }
    fun consumeLevelUpEvent() { _levelUpEvent.value = null }
    fun consumeFirstAchievement() {
        val current = _pendingAchievements.value
        if (current.isNotEmpty()) {
            _pendingAchievements.value = current.drop(1)
        }
    }
}
