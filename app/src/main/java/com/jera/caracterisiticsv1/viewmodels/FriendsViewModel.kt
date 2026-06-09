package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.repository.AuthRepository
import com.jera.caracterisiticsv1.repository.FirestoreRepository
import com.jera.caracterisiticsv1.repository.FirestoreUserProfile
import com.jera.caracterisiticsv1.repository.FriendData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendsUiState(
    val isLoading: Boolean = false,
    val friends: List<FriendData> = emptyList(),
    val followingUids: List<String> = emptyList(),
    // Búsqueda por email
    val searchEmail: String = "",
    val searchResult: FirestoreUserProfile? = null,
    val isSearching: Boolean = false,
    val searchError: String? = null,
    // Estado de la operación follow/unfollow
    val isFollowLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        val uid = authRepository.getCurrentUid() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val followingUids = firestoreRepository.getFollowing(uid)
                val friends = firestoreRepository.getFriendsData(followingUids)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    friends = friends,
                    followingUids = followingUids
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error cargando amigos: ${e.message}"
                )
            }
        }
    }

    fun onSearchEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            searchEmail = email,
            searchResult = null,
            searchError = null
        )
    }

    fun searchByEmail() {
        val email = _uiState.value.searchEmail.trim()
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(searchError = "Introduce un email")
            return
        }
        val myUid = authRepository.getCurrentUid()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, searchError = null, searchResult = null)
            try {
                val found = firestoreRepository.findUserByEmail(email)
                when {
                    found == null -> _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchError = "No se encontró ningún usuario con ese email"
                    )
                    found.uid == myUid -> _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchError = "No puedes seguirte a ti mismo"
                    )
                    else -> _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchResult = found
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchError = "Error buscando: ${e.message}"
                )
            }
        }
    }

    fun followUser(targetUid: String) {
        val myUid = authRepository.getCurrentUid() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFollowLoading = true)
            try {
                firestoreRepository.followUser(myUid, targetUid)
                _uiState.value = _uiState.value.copy(
                    isFollowLoading = false,
                    searchResult = null,
                    searchEmail = ""
                )
                loadFriends() // Recargar lista
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isFollowLoading = false,
                    error = "Error al seguir: ${e.message}"
                )
            }
        }
    }

    fun unfollowUser(targetUid: String) {
        val myUid = authRepository.getCurrentUid() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFollowLoading = true)
            try {
                firestoreRepository.unfollowUser(myUid, targetUid)
                _uiState.value = _uiState.value.copy(isFollowLoading = false)
                loadFriends()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isFollowLoading = false,
                    error = "Error al dejar de seguir: ${e.message}"
                )
            }
        }
    }

    fun isFollowing(uid: String) = _uiState.value.followingUids.contains(uid)

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
