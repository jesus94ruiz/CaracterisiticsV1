package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.repository.AuthRepository
import com.jera.caracterisiticsv1.repository.FirestoreCapturedCar
import com.jera.caracterisiticsv1.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialMapViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _capturedCars = MutableStateFlow<List<FirestoreCapturedCar>>(emptyList())
    val capturedCars: StateFlow<List<FirestoreCapturedCar>> = _capturedCars.asStateFlow()

    init {
        // Solo cargar coches sociales si el usuario está autenticado
        if (authRepository.isLoggedIn) {
            loadSocialCars()
        }
    }

    private fun loadSocialCars() {
        viewModelScope.launch {
            runCatching {
                val currentUid = authRepository.getCurrentUid()
                val cars = firestoreRepository.getAllCapturedCarsOnMap()
                // Filtrar los coches del propio usuario (ya se muestran con los marcadores locales)
                _capturedCars.value = cars.filter { it.uid != currentUid }
            }
        }
    }

    fun refresh() = loadSocialCars()
}
