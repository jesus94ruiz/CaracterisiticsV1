package com.jera.caracterisiticsv1.viewmodels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.jera.caracterisiticsv1.repository.AuthRepository
import com.jera.caracterisiticsv1.repository.FirestoreRepository
import com.jera.caracterisiticsv1.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Observar el estado de autenticación en tiempo real
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = user != null,
                    user = user
                )
            }
        }
    }

    /** Lanza el intent de Google Sign-In */
    fun getSignInIntent(): Intent = authRepository.getGoogleSignInClient().signInIntent

    /**
     * Procesa el resultado del Google Sign-In Intent.
     * Se llama desde el ActivityResultLauncher en LoginScreen.
     */
    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                    ?: throw IllegalStateException("No se obtuvo idToken de Google")

                val firebaseUser = authRepository.firebaseSignInWithGoogle(idToken)

                // Aseguramos que el perfil local existe
                userRepository.ensureProfileExists()

                // Sincronizamos el perfil con Firestore
                val localProfile = userRepository.getUserProfileOnce()
                if (localProfile != null) {
                    // Si el username aún es el default, lo actualizamos con el nombre de Google
                    if (localProfile.username == "Driver") {
                        userRepository.updateUsername(firebaseUser.displayName ?: "Driver")
                    }
                    firestoreRepository.syncUserProfile(
                        uid = firebaseUser.uid,
                        profile = localProfile.copy(
                            username = if (localProfile.username == "Driver")
                                firebaseUser.displayName ?: "Driver"
                            else localProfile.username
                        ),
                        email = firebaseUser.email ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    user = firebaseUser
                )
            } catch (e: ApiException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de Google Sign-In: ${e.statusCode}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido al iniciar sesión"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState(isLoggedIn = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
