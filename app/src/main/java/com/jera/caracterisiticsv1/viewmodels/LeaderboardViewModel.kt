package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.repository.AuthRepository
import com.jera.caracterisiticsv1.repository.FirestoreLeaderboardEntry
import com.jera.caracterisiticsv1.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LeaderboardTab { XP, COLLECTION }

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val entries: List<FirestoreLeaderboardEntry> = emptyList(),
    val selectedTab: LeaderboardTab = LeaderboardTab.XP,
    val currentUid: String? = null,
    val error: String? = null
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(currentUid = authRepository.getCurrentUid())
        loadLeaderboard(LeaderboardTab.XP)
    }

    fun selectTab(tab: LeaderboardTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        loadLeaderboard(tab)
    }

    fun refresh() = loadLeaderboard(_uiState.value.selectedTab)

    private fun loadLeaderboard(tab: LeaderboardTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val entries = when (tab) {
                    LeaderboardTab.XP -> firestoreRepository.getLeaderboardByXp()
                    LeaderboardTab.COLLECTION -> firestoreRepository.getLeaderboardByCollection()
                }
                _uiState.value = _uiState.value.copy(isLoading = false, entries = entries)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar el ranking: ${e.message}"
                )
            }
        }
    }
}
