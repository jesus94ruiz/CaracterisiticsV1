package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.repository.AuthRepository
import com.jera.caracterisiticsv1.repository.DatabaseRepository
import com.jera.caracterisiticsv1.repository.FirestoreRepository
import com.jera.caracterisiticsv1.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShowcaseUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val allCars: List<CarModel> = emptyList(),
    val selectedCarIds: Set<Int> = emptySet(),   // ids de los coches seleccionados (máx 3)
    val currentShowcase: List<FirestoreRepository.ShowcaseCar> = emptyList(),
    val savedSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ShowcaseViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val firestoreRepository: FirestoreRepository,
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowcaseUiState())
    val uiState: StateFlow<ShowcaseUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val cars = databaseRepository.getModelsFromDatabase()
                val uid = authRepository.getCurrentUid()
                val currentShowcase = if (uid != null) {
                    firestoreRepository.getShowcase(uid)
                } else emptyList()

                // Pre-seleccionar los coches que ya están en el expositor
                val preSelected = currentShowcase.mapNotNull { s ->
                    cars.firstOrNull { it.id.toString() == s.carId }?.id
                }.toSet()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allCars = cars,
                    currentShowcase = currentShowcase,
                    selectedCarIds = preSelected
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error cargando datos: ${e.message}"
                )
            }
        }
    }

    fun toggleCarSelection(carId: Int) {
        val current = _uiState.value.selectedCarIds.toMutableSet()
        if (current.contains(carId)) {
            current.remove(carId)
        } else {
            if (current.size >= 3) {
                _uiState.value = _uiState.value.copy(error = "Máximo 3 coches en el expositor")
                return
            }
            current.add(carId)
        }
        _uiState.value = _uiState.value.copy(selectedCarIds = current, error = null)
    }

    /**
     * Guarda el expositor: sube las fotos a Storage y persiste en Firestore.
     */
    fun saveShowcase() {
        val uid = authRepository.getCurrentUid() ?: run {
            _uiState.value = _uiState.value.copy(error = "Debes iniciar sesión para guardar el expositor")
            return
        }
        val selectedIds = _uiState.value.selectedCarIds
        val allCars = _uiState.value.allCars

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val showcaseCars = selectedIds.mapNotNull { id ->
                    val car = allCars.firstOrNull { it.id == id } ?: return@mapNotNull null
                    val carId = car.id.toString()

                    // Subir foto a Firebase Storage si existe ruta local
                    val storageUrl = if (!car.path.isNullOrBlank()) {
                        storageRepository.uploadShowcasePhoto(uid, carId, car.path) ?: ""
                    } else ""

                    FirestoreRepository.ShowcaseCar(
                        carId = carId,
                        makeName = car.make_name,
                        modelName = car.model_name,
                        years = car.years ?: "",
                        storageUrl = storageUrl
                    )
                }

                firestoreRepository.saveShowcase(uid, showcaseCars)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    savedSuccess = true,
                    currentShowcase = showcaseCars
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error guardando expositor: ${e.message}"
                )
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearSavedSuccess() { _uiState.value = _uiState.value.copy(savedSuccess = false) }
}
